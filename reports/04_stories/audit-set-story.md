# ドメインストーリー: 監査セット管理

## 概要

監査セット管理ドメインにおける主要なビジネスプロセスをドメインストーリーテリング形式で可視化します。

---

## ストーリー1: 監査セットの作成

### シナリオ
監査管理者が新しい財務監査のための監査セットを作成する。

### 登場人物・オブジェクト
- 👤 監査管理者（Audit Admin）
- 📁 監査セット（Audit Set）
- 📋 Scalar Auditor WebApp
- 💾 ScalarDB

### ストーリー図

```mermaid
sequenceDiagram
    actor AA as 👤 監査管理者
    participant WEB as 📋 WebApp
    participant API as 🔧 API
    participant DB as 💾 ScalarDB
    
    AA->>WEB: 1. 「監査セット作成」をクリック
    WEB->>WEB: 2. 作成フォームを表示
    AA->>WEB: 3. 名前「2024年度財務監査」と説明を入力
    WEB->>API: 4. 監査セット作成リクエスト
    API->>DB: 5. 監査セットを保存
    API->>DB: 6. 作成者を所有者として登録
    DB-->>API: 7. 保存完了
    API-->>WEB: 8. 作成成功レスポンス
    WEB-->>AA: 9. 監査セット詳細画面を表示
```

### アクティビティ記述

| # | アクター | アクション | オブジェクト | 結果 |
|---|---------|-----------|-------------|------|
| 1 | 監査管理者 | クリック | 監査セット作成ボタン | フォーム表示 |
| 2 | 監査管理者 | 入力 | 監査セット情報 | データ準備 |
| 3 | WebApp | 送信 | 作成リクエスト | API呼び出し |
| 4 | API | 保存 | 監査セット | DB永続化 |
| 5 | API | 登録 | 所有者コラボレーター | 権限設定 |
| 6 | WebApp | 表示 | 監査セット詳細 | 作成完了 |

---

## ストーリー2: ファイルを監査セットに追加

### シナリオ
一般ユーザーがBOX統合メニューからファイルを監査セットに追加する。

### 登場人物・オブジェクト
- 👤 一般ユーザー（General User）
- 📄 ファイル（File）
- 📁 監査セット（Audit Set）
- 🔗 BOX統合メニュー
- 🔐 ScalarDL

### ストーリー図

```mermaid
sequenceDiagram
    actor GU as 👤 一般ユーザー
    participant BOX as 📦 BOX
    participant MENU as 🔗 統合メニュー
    participant API as 🔧 API
    participant DL as 🔐 ScalarDL
    
    GU->>BOX: 1. ファイルを選択
    BOX->>MENU: 2. 統合メニューを開く
    GU->>MENU: 3. 「監査セットに追加」を選択
    MENU->>MENU: 4. 監査セット一覧を表示
    GU->>MENU: 5. 対象の監査セットを選択
    MENU->>API: 6. ファイル追加リクエスト
    API->>API: 7. 権限チェック
    API->>API: 8. ファイル情報取得
    API->>DL: 9. ファイルを監視対象として登録
    DL-->>API: 10. 登録完了
    API-->>MENU: 11. 追加成功
    MENU-->>GU: 12. 成功メッセージ表示
```

### ビジネスルール

1. **権限チェック**: ユーザーは監査セットへのMEMBER以上の権限が必要
2. **重複チェック**: 同一ファイルの重複追加は不可
3. **監視登録**: 追加時にScalarDLへの自動登録

---

## ストーリー3: 監査セットの検証実行

### シナリオ
外部監査人が監査セット全体のファイル整合性を検証する。

### 登場人物・オブジェクト
- 👤 外部監査人（External Auditor）
- 📁 監査セット（Audit Set）
- 📄 ファイル（Files）
- 🔐 ScalarDL
- 📊 検証レポート

### ストーリー図

```mermaid
sequenceDiagram
    actor EA as 👤 外部監査人
    participant WEB as 📋 WebApp
    participant API as 🔧 Verification API
    participant DL as 🔐 ScalarDL
    
    EA->>WEB: 1. 監査セットを選択
    WEB->>WEB: 2. 監査セット詳細表示
    EA->>WEB: 3. 「全体検証」をクリック
    WEB->>API: 4. 検証リクエスト
    
    loop 各ファイル
        API->>DL: 5. アセット検証実行
        DL-->>API: 6. 検証結果
    end
    
    API->>API: 7. レポート生成
    API-->>WEB: 8. 検証レポート
    WEB-->>EA: 9. 結果表示（改ざん有無）
```

### 検証ステータス

```mermaid
stateDiagram-v2
    [*] --> NOT_MONITORED: ファイル追加
    NOT_MONITORED --> MONITORED: 監視登録
    MONITORED --> NOT_TAMPERED: 検証成功
    MONITORED --> TAMPERED: 改ざん検知
    NOT_TAMPERED --> TAMPERED: 再検証で改ざん検知
    TAMPERED --> [*]: 調査完了
```

---

## ストーリー4: 外部監査人の招待

### シナリオ
監査管理者が外部監査人を監査セットに招待する。

### ストーリー図

```mermaid
sequenceDiagram
    actor AA as 👤 監査管理者
    participant WEB as 📋 WebApp
    participant API as 🔧 API
    participant MAIL as 📧 メールサービス
    actor EA as 👤 外部監査人
    
    AA->>WEB: 1. 監査グループ管理を開く
    AA->>WEB: 2. 「外部監査人追加」をクリック
    WEB->>WEB: 3. 登録フォーム表示
    AA->>WEB: 4. メールアドレス・名前を入力
    WEB->>API: 5. 外部監査人作成リクエスト
    API->>API: 6. 仮パスワード生成
    API->>API: 7. ユーザー作成
    API->>MAIL: 8. 招待メール送信
    MAIL-->>EA: 9. 招待メール受信
    EA->>WEB: 10. リンクからログイン
    EA->>WEB: 11. パスワード変更
```

---

## ストーリー5: イベント履歴の確認

### シナリオ
監査管理者がファイルの変更履歴を確認する。

### ストーリー図

```mermaid
sequenceDiagram
    actor AA as 👤 監査管理者
    participant WEB as 📋 WebApp
    participant API as 🔧 Event API
    participant DB as 💾 ScalarDB
    
    AA->>WEB: 1. 「イベント履歴」を開く
    WEB->>WEB: 2. フィルター設定画面
    AA->>WEB: 3. 日付範囲・イベントタイプを選択
    WEB->>API: 4. イベント検索リクエスト
    API->>DB: 5. イベントログ取得
    DB-->>API: 6. イベント一覧
    API-->>WEB: 7. 検索結果
    WEB-->>AA: 8. イベント履歴表示
    AA->>WEB: 9. 特定イベントをクリック
    WEB-->>AA: 10. イベント詳細表示
```

---

## ドメイン語彙（このストーリーで使用）

| 用語 | 定義 | 使用箇所 |
|------|------|---------|
| 監査セット | 監査対象ファイルのグループ | 全ストーリー |
| コラボレーター | 監査セットへのアクセス権を持つユーザー | ストーリー1, 2 |
| 検証 | ScalarDLによる改ざんチェック | ストーリー3 |
| 外部監査人 | 外部組織からの監査担当者 | ストーリー3, 4 |
| イベント | BOXでの操作履歴 | ストーリー5 |

---

## 次のステップ

1. 各ストーリーの詳細化（例外フロー、エラーケース）
2. UIモックアップとの対応付け
3. API設計との整合性確認
