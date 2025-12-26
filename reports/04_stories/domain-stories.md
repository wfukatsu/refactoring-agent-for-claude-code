# ドメインストーリー集

## 概要

本ドキュメントは、Scalar Auditor for BOXシステムの6つの境界づけられたコンテキストにおける主要なビジネスプロセスをドメインストーリーテリング形式で記述します。

### 表記法

```
[アクター] が {ワークオブジェクト} を 《アクティビティ》 する
→ [次のアクター/システム] へ渡す
```

---

## 1. Identity & Access Context (認証・アクセス管理)

### Story 1.1: ユーザー認証フロー

```mermaid
graph LR
    subgraph "認証フロー"
        U[監査担当者] -->|1. ログイン要求| LP[ログインページ]
        LP -->|2. 認証情報送信| AS[AuthService]
        AS -->|3. 資格情報検証| DB[(UserDB)]
        DB -->|4. ユーザー情報| AS
        AS -->|5. JWTトークン発行| U
        U -->|6. 保護リソースアクセス| API[API Gateway]
        API -->|7. トークン検証| AS
    end
```

**ナラティブ:**

1. **[監査担当者]** が {ログインフォーム} に《認証情報を入力》する
2. **[AuthService]** が {認証情報} を《検証》する
3. **[AuthService]** が {JWTトークン} を《発行》して [監査担当者] へ渡す
4. **[監査担当者]** が {JWTトークン} を使用して《保護リソースにアクセス》する
5. **[API Gateway]** が {JWTトークン} を《検証》して [バックエンドサービス] へ転送する

**ビジネスルール:**
- トークン有効期限: 8時間
- リフレッシュトークン: 7日間
- 3回連続失敗でアカウントロック

---

### Story 1.2: ロール・権限管理

```mermaid
sequenceDiagram
    actor Admin as システム管理者
    participant RS as RoleService
    participant PS as PermissionService
    participant DB as Database

    Admin->>RS: ロール作成要求
    RS->>PS: 権限セット取得
    PS-->>RS: 利用可能な権限一覧
    RS->>DB: ロール永続化
    DB-->>RS: 保存完了
    RS-->>Admin: ロール作成完了

    Admin->>RS: ユーザーにロール割当
    RS->>DB: ユーザーロール更新
    DB-->>RS: 更新完了
    RS-->>Admin: 割当完了
```

**ナラティブ:**

1. **[システム管理者]** が {新規ロール} を《定義》する
2. **[RoleService]** が {権限セット} を《ロールに紐付け》する
3. **[システム管理者]** が {ユーザー} に《ロールを割り当て》る
4. **[PermissionService]** が {アクセス要求} を《権限チェック》する

**ビジネスルール:**
- 最小権限の原則
- ロール階層: Admin > Manager > Auditor > Viewer
- 権限の継承は上位ロールのみ

---

## 2. Audit Management Context (監査管理)

### Story 2.1: 監査セット作成

```mermaid
graph TB
    subgraph "監査セット作成フロー"
        A[監査担当者] -->|1. 作成要求| AS[AuditSetService]
        AS -->|2. 検証| VS[ValidationService]
        VS -->|3. OK| AS
        AS -->|4. 永続化| DB[(AuditDB)]
        DB -->|5. 作成完了| AS
        AS -->|6. イベント発行| EQ[EventQueue]
        EQ -->|7. 非同期処理| NS[NotificationService]
    end
```

**ナラティブ:**

1. **[監査担当者]** が {監査セット名と説明} を《入力》する
2. **[AuditSetService]** が {入力データ} を《バリデーション》する
3. **[AuditSetService]** が {監査セット} を《作成》してDBに保存する
4. **[EventPublisher]** が {AuditSetCreatedイベント} を《発行》する
5. **[NotificationService]** が {関係者} に《通知》する

**ビジネスルール:**
- 監査セット名は一意である必要がある
- 作成者は自動的にオーナーとなる
- 監査セットには最低1つの監査アイテムが必要（後から追加可）

---

### Story 2.2: 監査アイテム追加

```mermaid
sequenceDiagram
    actor Auditor as 監査担当者
    participant UI as WebUI
    participant AIS as AuditItemService
    participant FS as FileService
    participant BoxAPI as BOX API
    participant DB as Database
    participant EQ as EventQueue

    Auditor->>UI: ファイル選択
    UI->>FS: ファイル情報取得
    FS->>BoxAPI: ファイルメタデータ取得
    BoxAPI-->>FS: メタデータ
    FS-->>UI: ファイル情報表示

    Auditor->>UI: 監査アイテム追加
    UI->>AIS: 追加要求
    AIS->>DB: アイテム作成
    DB-->>AIS: 作成完了
    AIS->>EQ: ItemAddedイベント
    AIS-->>UI: 追加完了
```

**ナラティブ:**

1. **[監査担当者]** が {BOXファイル} を《選択》する
2. **[FileService]** が {ファイルメタデータ} を《BOXから取得》する
3. **[監査担当者]** が {監査アイテム} を《監査セットに追加》する
4. **[AuditItemService]** が {監査アイテム} を《作成》してDBに保存する
5. **[EventPublisher]** が {ItemAddedイベント} を《発行》する

**ビジネスルール:**
- 同一ファイルの重複追加は不可
- ファイルの現在バージョンがスナップショットとして保存される
- 監査アイテムステータス: Pending → InProgress → Completed

---

### Story 2.3: 監査セット削除（Saga）

```mermaid
sequenceDiagram
    actor Auditor as 監査担当者
    participant ASS as AuditSetService
    participant SAGA as SagaOrchestrator
    participant AIS as AuditItemService
    participant VS as VerificationService
    participant ES as EventService
    participant DB as Database

    Auditor->>ASS: 削除要求
    ASS->>SAGA: Saga開始

    SAGA->>VS: 検証データ削除
    VS-->>SAGA: 削除完了

    SAGA->>AIS: アイテム削除
    AIS-->>SAGA: 削除完了

    SAGA->>ES: 関連イベント削除
    ES-->>SAGA: 削除完了

    SAGA->>ASS: セット削除
    ASS->>DB: 論理削除
    DB-->>ASS: 完了
    ASS-->>SAGA: 削除完了

    SAGA-->>Auditor: 全削除完了
```

**ナラティブ:**

1. **[監査担当者]** が {監査セット} の《削除を要求》する
2. **[SagaOrchestrator]** が {削除Saga} を《開始》する
3. **[VerificationService]** が {関連する検証データ} を《削除》する
4. **[AuditItemService]** が {すべての監査アイテム} を《削除》する
5. **[EventService]** が {関連イベント} を《アーカイブ》する
6. **[AuditSetService]** が {監査セット} を《論理削除》する
7. **[SagaOrchestrator]** が {完了ステータス} を《報告》する

**補償トランザクション:**
- 各ステップで失敗した場合、前のステップをロールバック
- 削除は論理削除で、30日後に物理削除

---

## 3. File Management Context (ファイル管理)

### Story 3.1: ファイル同期

```mermaid
graph TB
    subgraph "ファイル同期フロー"
        SCH[Scheduler] -->|1. 同期トリガー| SS[SyncService]
        SS -->|2. 差分取得| BOX[BOX API]
        BOX -->|3. 変更一覧| SS
        SS -->|4. ローカル更新| DB[(FileDB)]
        DB -->|5. 更新完了| SS
        SS -->|6. イベント発行| EQ[EventQueue]
        EQ -->|7. 監査影響チェック| AS[AuditService]
    end
```

**ナラティブ:**

1. **[Scheduler]** が {同期ジョブ} を《定期実行》する（5分間隔）
2. **[SyncService]** が {BOX} から《変更差分を取得》する
3. **[SyncService]** が {ファイルメタデータ} を《ローカルDBに同期》する
4. **[EventPublisher]** が {FileSyncedイベント} を《発行》する
5. **[AuditService]** が {監査対象ファイルの変更} を《検出》して通知する

**ビジネスルール:**
- 削除されたファイルは論理削除として保持
- バージョン履歴は最大100世代保持
- ファイルサイズ上限: 5GB

---

### Story 3.2: コラボレーター管理

```mermaid
sequenceDiagram
    actor Admin as ファイル管理者
    participant CS as CollaboratorService
    participant BoxAPI as BOX API
    participant DB as Database
    participant NS as NotificationService

    Admin->>CS: コラボレーター追加要求
    CS->>BoxAPI: 権限設定API呼出
    BoxAPI-->>CS: 設定完了
    CS->>DB: ローカル記録
    DB-->>CS: 保存完了
    CS->>NS: 通知要求
    NS-->>Admin: 追加完了通知
```

**ナラティブ:**

1. **[ファイル管理者]** が {ユーザー} を《コラボレーターとして追加》する
2. **[CollaboratorService]** が {BOX API} 経由で《権限を設定》する
3. **[CollaboratorService]** が {コラボレーター情報} を《ローカルDBに記録》する
4. **[NotificationService]** が {新規コラボレーター} に《招待通知》する

**ビジネスルール:**
- 権限レベル: Owner > Editor > Viewer
- 継承権限のオーバーライドは明示的に行う
- 監査対象ファイルの権限変更はログ記録

---

## 4. Event Tracking Context (イベント追跡)

### Story 4.1: BOXイベント取得

```mermaid
graph TB
    subgraph "イベント取得フロー"
        WH[BOX Webhook] -->|1. イベント通知| EI[EventIngester]
        EI -->|2. 正規化| EP[EventProcessor]
        EP -->|3. 永続化| CS[(Cassandra)]
        CS -->|4. 保存完了| EP
        EP -->|5. 分析| EA[EventAnalyzer]
        EA -->|6. アラート| NS[NotificationService]
    end
```

**ナラティブ:**

1. **[BOX Webhook]** が {イベント} を《システムに通知》する
2. **[EventIngester]** が {生イベント} を《正規化》する
3. **[EventProcessor]** が {正規化イベント} を《Cassandraに永続化》する
4. **[EventAnalyzer]** が {イベントパターン} を《分析》する
5. **[NotificationService]** が {異常検知時} に《アラート》する

**ビジネスルール:**
- イベントは追記のみ（不変）
- 保持期間: 監査イベント=7年、一般イベント=1年
- 毎秒1000イベントの処理能力

---

### Story 4.2: イベント検索・フィルタリング

```mermaid
sequenceDiagram
    actor Auditor as 監査担当者
    participant UI as WebUI
    participant ES as EventSearchService
    participant CS as Cassandra
    participant CACHE as Redis

    Auditor->>UI: 検索条件入力
    UI->>ES: 検索クエリ
    ES->>CACHE: キャッシュ確認
    alt キャッシュヒット
        CACHE-->>ES: キャッシュ結果
    else キャッシュミス
        ES->>CS: クエリ実行
        CS-->>ES: 検索結果
        ES->>CACHE: 結果キャッシュ
    end
    ES-->>UI: 検索結果
    UI-->>Auditor: 結果表示
```

**ナラティブ:**

1. **[監査担当者]** が {検索条件（期間、ファイル、アクション）} を《入力》する
2. **[EventSearchService]** が {キャッシュ} を《確認》する
3. **[EventSearchService]** が {Cassandra} から《イベントを検索》する
4. **[EventSearchService]** が {検索結果} を《フィルタリング・ソート》する
5. **[WebUI]** が {イベント一覧} を《表示》する

**ビジネスルール:**
- 検索結果は最大10,000件まで
- ページネーション: 100件/ページ
- 頻繁なクエリは5分間キャッシュ

---

## 5. Integrity Verification Context (整合性検証)

### Story 5.1: ハッシュ計算と登録

```mermaid
graph TB
    subgraph "ハッシュ登録フロー"
        T[トリガー] -->|1. 検証要求| VS[VerificationService]
        VS -->|2. ファイル取得| FS[FileService]
        FS -->|3. ダウンロード| BOX[BOX API]
        BOX -->|4. ファイルデータ| FS
        FS -->|5. ファイル| VS
        VS -->|6. ハッシュ計算| HC[HashCalculator]
        HC -->|7. SHA-256| VS
        VS -->|8. 登録| SDL[ScalarDL]
        SDL -->|9. 改ざん防止記録| VS
    end
```

**ナラティブ:**

1. **[監査担当者/システム]** が {ファイル} の《検証を要求》する
2. **[VerificationService]** が {ファイル} を《BOXから取得》する
3. **[HashCalculator]** が {ファイルコンテンツ} の《SHA-256ハッシュを計算》する
4. **[VerificationService]** が {ハッシュ値} を《ScalarDLに登録》する
5. **[ScalarDL]** が {登録証明} を《発行》する

**ビジネスルール:**
- ハッシュアルゴリズム: SHA-256
- 同一ファイルの再登録は新バージョンとして記録
- ScalarDLによる改ざん防止保証

---

### Story 5.2: 整合性検証実行

```mermaid
sequenceDiagram
    actor Auditor as 監査担当者
    participant VS as VerificationService
    participant FS as FileService
    participant BoxAPI as BOX API
    participant HC as HashCalculator
    participant SDL as ScalarDL
    participant DB as Database

    Auditor->>VS: 検証実行要求
    VS->>FS: 現在のファイル取得
    FS->>BoxAPI: ダウンロード
    BoxAPI-->>FS: ファイルデータ
    FS-->>VS: ファイル

    VS->>HC: ハッシュ計算
    HC-->>VS: 現在のハッシュ

    VS->>SDL: 登録ハッシュ取得
    SDL-->>VS: 登録時のハッシュ

    VS->>VS: ハッシュ比較

    alt 一致
        VS->>DB: 検証成功記録
        VS-->>Auditor: 整合性確認OK
    else 不一致
        VS->>DB: 検証失敗記録
        VS-->>Auditor: 改ざん検出アラート
    end
```

**ナラティブ:**

1. **[監査担当者]** が {監査アイテム} の《整合性検証を実行》する
2. **[VerificationService]** が {現在のファイル} を《BOXから取得》する
3. **[HashCalculator]** が {現在のハッシュ} を《計算》する
4. **[VerificationService]** が {登録時のハッシュ} を《ScalarDLから取得》する
5. **[VerificationService]** が {ハッシュを比較} して《検証結果を判定》する
6. **[VerificationService]** が {検証結果} を《記録》する
7. 不一致の場合、**[NotificationService]** が《アラート》する

**ビジネスルール:**
- 検証結果: Verified / Tampered / NotRegistered / Error
- 改ざん検出時は即時アラート
- 検証履歴は永久保持

---

## 6. BOX Integration Context (BOX連携)

### Story 6.1: OAuth認証フロー

```mermaid
graph TB
    subgraph "OAuth認証フロー"
        U[ユーザー] -->|1. BOX連携要求| APP[Application]
        APP -->|2. 認可リダイレクト| BOX[BOX OAuth]
        BOX -->|3. ログイン/同意| U
        U -->|4. 認可コード| APP
        APP -->|5. トークン交換| BOX
        BOX -->|6. アクセストークン| APP
        APP -->|7. トークン保存| DB[(TokenDB)]
    end
```

**ナラティブ:**

1. **[ユーザー]** が {BOX連携} を《開始》する
2. **[Application]** が {認可リクエスト} を《BOXに送信》する
3. **[ユーザー]** が {BOXログイン画面} で《認証・同意》する
4. **[BOX]** が {認可コード} を《Applicationに返却》する
5. **[Application]** が {認可コード} を《アクセストークンに交換》する
6. **[TokenService]** が {トークン} を《暗号化して保存》する

**ビジネスルール:**
- アクセストークン有効期限: 60分
- リフレッシュトークン有効期限: 60日
- トークンはAES-256で暗号化保存

---

### Story 6.2: Webhookイベント処理

```mermaid
sequenceDiagram
    participant BoxPlatform as BOX Platform
    participant WH as WebhookHandler
    participant VAL as SignatureValidator
    participant EI as EventIngester
    participant QUEUE as EventQueue

    BoxPlatform->>WH: Webhookイベント
    WH->>VAL: 署名検証
    alt 署名有効
        VAL-->>WH: 検証OK
        WH->>EI: イベント正規化
        EI->>QUEUE: キューイング
        QUEUE-->>WH: 受付完了
        WH-->>BoxPlatform: 200 OK
    else 署名無効
        VAL-->>WH: 検証NG
        WH-->>BoxPlatform: 401 Unauthorized
    end
```

**ナラティブ:**

1. **[BOX Platform]** が {Webhookイベント} を《エンドポイントに送信》する
2. **[WebhookHandler]** が {リクエスト署名} を《検証》する
3. **[EventIngester]** が {イベントペイロード} を《正規化》する
4. **[EventQueue]** が {イベント} を《非同期処理キューに追加》する
5. **[WebhookHandler]** が {200 OK} を《即座に返却》する

**ビジネスルール:**
- Webhook署名はHMAC-SHA256で検証
- 応答は3秒以内（タイムアウト対策）
- リトライ: 最大5回（指数バックオフ）

---

### Story 6.3: API レート制限対応

```mermaid
graph TB
    subgraph "レート制限対応"
        REQ[APIリクエスト] --> RL[RateLimiter]
        RL -->|許可| BOX[BOX API]
        RL -->|制限中| QUEUE[待機キュー]
        QUEUE -->|遅延実行| RL
        BOX -->|429 Rate Limited| RL
        RL -->|バックオフ| QUEUE
    end
```

**ナラティブ:**

1. **[Application]** が {APIリクエスト} を《発行》する
2. **[RateLimiter]** が {レート制限状態} を《確認》する
3. 制限内の場合、**[RateLimiter]** が {リクエスト} を《BOX APIに転送》する
4. 429応答の場合、**[RateLimiter]** が {リクエスト} を《待機キューに追加》する
5. **[RateLimiter]** が {指数バックオフ} で《リトライ》する

**ビジネスルール:**
- BOX APIレート: 1000リクエスト/分
- ローカルレート制限: 800リクエスト/分（バッファ）
- バックオフ: 1秒 → 2秒 → 4秒 → ... 最大60秒

---

## 7. クロスコンテキストシナリオ

### Story 7.1: 監査完了レポート生成

```mermaid
graph TB
    subgraph "レポート生成フロー"
        A[監査担当者] -->|1. レポート要求| AS[AuditService]
        AS -->|2. アイテム取得| AIS[AuditItemService]
        AS -->|3. 検証結果取得| VS[VerificationService]
        AS -->|4. イベント取得| ES[EventService]
        AS -->|5. ファイル情報取得| FS[FileService]
        AS -->|6. レポート生成| RG[ReportGenerator]
        RG -->|7. PDF出力| A
    end
```

**ナラティブ:**

1. **[監査担当者]** が {監査セット} の《レポート生成を要求》する
2. **[AuditService]** が {各サービス} から《必要データを収集》する
3. **[ReportGenerator]** が {収集データ} を《レポート形式に整形》する
4. **[ReportGenerator]** が {PDF/Excel} として《エクスポート》する

---

### Story 7.2: ファイル変更検知と監査影響通知

```mermaid
sequenceDiagram
    participant BoxWebhook as BOX Webhook
    participant EI as EventIngester
    participant ES as EventService
    participant AS as AuditService
    participant NS as NotificationService
    actor Auditor as 監査担当者

    BoxWebhook->>EI: ファイル更新イベント
    EI->>ES: イベント保存
    ES->>AS: 監査影響チェック
    AS->>AS: 対象監査セット検索

    alt 監査対象ファイル
        AS->>NS: 通知要求
        NS->>Auditor: メール/Slack通知
    end
```

**ナラティブ:**

1. **[BOX]** が {ファイル更新イベント} を《Webhookで通知》する
2. **[EventService]** が {イベント} を《記録》する
3. **[AuditService]** が {監査対象かどうか} を《チェック》する
4. 監査対象の場合、**[NotificationService]** が {担当者} に《通知》する

---

## 8. ドメイン用語集

| 用語 | 日本語 | 定義 |
|------|--------|------|
| Audit Set | 監査セット | 監査対象ファイルをグループ化したコンテナ |
| Audit Item | 監査アイテム | 監査セット内の個別監査対象 |
| Verification | 検証 | ファイルの整合性を確認するプロセス |
| Hash | ハッシュ | ファイルの一意識別子（SHA-256） |
| Collaborator | コラボレーター | ファイルへのアクセス権を持つユーザー |
| Webhook | ウェブフック | BOXからのリアルタイムイベント通知 |
| ScalarDL | スカラーDL | 改ざん防止ストレージ |

---

*Generated: 2025-12-26*
*Version: 1.0.0*
*Source: Scalar Auditor for BOX Domain Analysis*
