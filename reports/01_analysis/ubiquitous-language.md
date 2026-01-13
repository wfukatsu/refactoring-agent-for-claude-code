# ユビキタス言語集

## 概要

Scalar Auditor for BOXは、BOXクラウドストレージサービスと統合された外部監査ツールです。ファイルのイベントログを外部データベースに保存し、外部監査人がファイルの完全性を検証できるようにします。本言語集では、システムで使用されるビジネスドメインの用語を定義します。

## 用語定義

### コアドメイン - 監査セット管理

| 用語（日本語） | 用語（英語） | 定義 | 使用箇所 |
|--------------|------------|------|---------|
| 監査セット | Audit Set | 監査対象として選択されたファイルとフォルダのグループ。名前と説明を持つ | AuditSet.java, AuditSetController |
| 監査グループ | Audit Group | 外部監査人のグループ。監査セットに一括で割り当て可能 | AuditGroup.java, AuditGroupController |
| 監査対象アイテム | Audit Set Item | 監査セットに追加されたファイルまたはフォルダ | AuditSetItem.java |
| コラボレーター | Collaborator | 監査セットにアクセス権を持つユーザー | AuditSetCollaborators.java |
| アクセスリスト | Access List / ACL | 監査セット内で許可されたファイル・フォルダの一覧 | aclJson field |

### コアドメイン - イベント管理

| 用語（日本語） | 用語（英語） | 定義 | 使用箇所 |
|--------------|------------|------|---------|
| イベントログ | Event Log | BOXでの操作履歴（アップロード、移動、コピー等） | Events.java, EventLogService |
| アイテムイベント | Item Events | 特定アイテムに対するイベント履歴 | ItemEvents.java |
| 監査人ログ | Auditor Logs | 外部監査人の操作履歴（プレビュー、ダウンロード等） | AuditorLogs.java |
| イベント履歴 | Event History | 日付範囲やユーザーでフィルタ可能なイベント一覧 | ViewAllEventHistory |

### コアドメイン - ファイル検証

| 用語（日本語） | 用語（英語） | 定義 | 使用箇所 |
|--------------|------------|------|---------|
| 改ざん検知 | Tampering Detection | ScalarDLを使用したファイル改ざんの検出 | AssetService |
| 検証 | Validation | ファイルまたは監査セット全体の改ざんチェック | checkTamperingStatus API |
| 改ざんステータス | Tampering Status | TAMPERED, NOT_TAMPERED, MONITORED, NOT_MONITORED | TamperingStatusType |
| 検証レポート | Validation Report | 改ざん検証結果の報告 | UI translations |
| SHA1ハッシュ | SHA1 Hash | ファイルの一意性を識別するハッシュ値 | ItemsBySha1.java |
| ファイルコピー | File Copies | 同一ハッシュ値を持つファイルの一覧 | getFileCopies API |
| バージョン履歴 | Version History | ファイルの全バージョン一覧 | getFileVersion API |

### サブドメイン - ユーザー・権限管理

| 用語（日本語） | 用語（英語） | 定義 | 使用箇所 |
|--------------|------------|------|---------|
| 組織ユーザー | Organization User | BOXアカウントを持つ内部ユーザー | User.java, UserRoles |
| 外部監査人 | External Auditor | 外部組織からの監査担当者（BOXアカウント不要） | UserRoles.EXTERNAL_AUDITOR |
| 監査管理者 | Audit Admin | 監査セット・ユーザー管理権限を持つ管理者 | UserRoles.AUDIT_ADMIN |
| 一般ユーザー | General User | ファイル追加・閲覧権限を持つ一般ユーザー | UserRoles.GENERAL_USER |
| 所有者 | Owner | 監査セットの完全な管理権限を持つユーザー | CollaboratorUserRoles.OWNER |
| 共同所有者 | Co-Owner | 所有者に準ずる権限を持つユーザー | CollaboratorUserRoles.CO_OWNER |
| メンバー | Member | 読み取りと限定編集権限を持つユーザー | CollaboratorUserRoles.MEMBER |
| レビュー担当者 | Reviewer | 読み取り専用アクセスを持つユーザー | CollaboratorUserRoles.REVIEWER |

### サブドメイン - アイテム管理

| 用語（日本語） | 用語（英語） | 定義 | 使用箇所 |
|--------------|------------|------|---------|
| アイテム | Item | ファイルまたはフォルダの総称 | Item.java, ItemType |
| ファイル | File | BOX内の文書ファイル | ItemType.FILE |
| フォルダ | Folder | BOX内のディレクトリ | ItemType.FOLDER |
| アイテムステータス | Item Status | アイテムの監視・検証状態 | ItemStatus.java |
| アセット | Asset | ScalarDLで管理される検証対象 | AssetService |

### サブドメイン - BOXイベントタイプ

| 用語（日本語） | 用語（英語） | 定義 | 使用箇所 |
|--------------|------------|------|---------|
| アイテムアップロード | ITEM_UPLOAD | ファイルのアップロード操作 | EventType |
| アイテム移動 | ITEM_MOVE | ファイル・フォルダの移動操作 | EventType |
| アイテムコピー | ITEM_COPY | ファイル・フォルダのコピー操作 | EventType |
| アイテム削除 | ITEM_TRASH | ゴミ箱への移動操作 | EventType |
| アイテム復元 | ITEM_UNDELETE_VIA_TRASH | ゴミ箱からの復元操作 | EventType |
| アイテム名変更 | ITEM_RENAME | ファイル・フォルダの名前変更 | EventType |
| アイテム変更 | ITEM_MODIFY | ファイル内容の変更 | EventType |

### サブドメイン - 外部監査人アクションタイプ

| 用語（日本語） | 用語（英語） | 定義 | 使用箇所 |
|--------------|------------|------|---------|
| アイテムダウンロード | ITEM_DOWNLOAD | ファイルのダウンロード操作 | ActionType |
| アイテムプレビュー | ITEM_PREVIEW | ファイルのプレビュー表示 | ActionType |
| アイテム閲覧 | ITEM_VIEW | ファイル詳細の閲覧 | ActionType |

### サポートドメイン - 認証・セキュリティ

| 用語（日本語） | 用語（英語） | 定義 | 使用箇所 |
|--------------|------------|------|---------|
| JWTトークン | JWT Token | API認証用のJSON Webトークン | JwtHelper, JwtAuthFilter |
| リフレッシュトークン | Refresh Token | アクセストークン更新用トークン | UserToken.java |
| OTPコード | OTP Code | パスワードリセット用ワンタイムパスワード | UserOtp.java |
| アクセストークン | Access Token | BOX API呼び出し用トークン | UserToken.java |

### 技術用語

| 用語（日本語） | 用語（英語） | 定義 | 使用箇所 |
|--------------|------------|------|---------|
| ScalarDB | ScalarDB | 分散トランザクション管理レイヤー | Repository層 |
| ScalarDL | ScalarDL | 改ざん検知用分散台帳 | AssetService |
| BOX API | BOX API | BOXクラウドストレージのAPI | BoxUtility |
| イベントストリーム | Event Stream | BOXからのイベント取得メカニズム | PositionTracker |

## 略語・頭字語

| 略語 | 正式名称 | 説明 |
|-----|---------|------|
| ACL | Access Control List | アクセス制御リスト |
| SHA1 | Secure Hash Algorithm 1 | ファイルハッシュアルゴリズム |
| JWT | JSON Web Token | 認証トークン形式 |
| OTP | One-Time Password | ワンタイムパスワード |
| CRUD | Create, Read, Update, Delete | 基本データ操作 |
| DL | Distributed Ledger | 分散台帳（ScalarDL） |
| DB | Database | データベース（ScalarDB） |

## 同義語・類義語マッピング

| 用語A | 用語B | 推奨用語 | 理由 |
|------|------|---------|------|
| 外部監査人 | External User | 外部監査人 | ビジネスロールを明確にする |
| Organization User | BOX User | Organization User | システム内での識別名 |
| ファイル検証 | Validation | 検証 | UI表示と整合 |
| 改ざん | Tampering | 改ざん | 日本語で統一 |
| アクセス許可リスト | Allow List | アクセスリスト | コード上の表現と整合 |
| レビューア | Reviewer | レビュー担当者 | 日本語表現を優先 |

## コンテキスト別用語使用

### BOX統合メニューコンテキスト
- ファイル詳細表示、フォルダ詳細表示
- 監査セットへのファイル/フォルダ追加
- イベント履歴表示

### WebUIコンテキスト
- 監査セット管理（作成・更新・削除）
- 外部監査人・グループ管理
- ユーザーロール管理
- 全イベント履歴表示

### 外部監査人コンテキスト
- 監査セット監視
- ファイルプレビュー・ダウンロード
- 検証レポート確認
