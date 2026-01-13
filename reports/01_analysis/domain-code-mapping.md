# ドメイン-コード対応表

## マッピング概要

Scalar Auditor for BOXのドメイン概念とコード実装の対応関係を整理します。本システムは以下のレイヤー構造で実装されています：

- **Controller層**: REST APIエンドポイント定義
- **Service層**: ビジネスロジック実装
- **Repository層**: ScalarDBを使用したデータアクセス
- **Model層**: ドメインエンティティ（@Builderパターン使用）
- **DTO層**: データ転送オブジェクト
- **Constant層**: Enum・定数定義

## ドメイン別マッピング

### 監査セット管理ドメイン

| 概念カテゴリ | 設計書での名称 | コード上の実装 | ファイルパス | 実装パターン |
|------------|--------------|--------------|-------------|-------------|
| エンティティ | 監査セット | AuditSet | model/AuditSet.java | Lombok @Builder Entity |
| エンティティ | 監査グループ | AuditGroup | model/AuditGroup.java | Lombok @Builder Entity |
| エンティティ | 監査セットアイテム | AuditSetItem | model/AuditSetItem.java | Lombok @Builder Entity |
| エンティティ | 監査セットコラボレーター | AuditSetCollaborators | model/AuditSetCollaborators.java | Lombok @Builder Entity |
| エンティティ | 監査グループ-監査セットマッピング | AuditGrpAuditSetMapping | model/AuditGrpAuditSetMapping.java | Mapping Entity |
| サービス | 監査セット管理 | AuditSetService | service/AuditSetService.java | @Service |
| サービス | 監査グループ管理 | AuditGroupService | service/AuditGroupService.java | @Service |
| サービス | 監査セットアイテム管理 | AuditSetItemService | service/AuditSetItemService.java | @Service |
| サービス | コラボレーター管理 | AuditSetCollaboratorService | service/AuditSetCollaboratorService.java | @Service |
| リポジトリ | 監査セット永続化 | AuditSetRepository | repository/AuditSetRepository.java | ScalarDB Repository |
| リポジトリ | 監査グループ永続化 | AuditGroupRepository | repository/AuditGroupRepository.java | ScalarDB Repository |
| API | 監査セットCRUD | AuditSetController | controller/AuditSetController.java | @RestController |
| API | 監査グループCRUD | AuditGroupController | controller/AuditGroupController.java | @RestController |
| DTO | 監査セット作成 | CreateAuditSet | dto/CreateAuditSet.java | Request DTO |
| DTO | 監査セット更新 | UpdateAuditSet | dto/UpdateAuditSet.java | Request DTO |
| DTO | 監査グループ作成 | CreateAuditGroup | dto/CreateAuditGroup.java | Request DTO |
| レスポンス | 監査セット一覧 | AuditSetList | responsedto/AuditSetList.java | Response DTO |

### イベント管理ドメイン

| 概念カテゴリ | 設計書での名称 | コード上の実装 | ファイルパス | 実装パターン |
|------------|--------------|--------------|-------------|-------------|
| エンティティ | BOXイベント | Events | model/Events.java | Lombok @Builder Entity |
| エンティティ | アイテムイベント | ItemEvents | model/ItemEvents.java | Lombok @Builder Entity |
| エンティティ | 監査人ログ | AuditorLogs | model/AuditorLogs.java | Lombok @Builder Entity |
| エンティティ | イベント取得位置 | PositionTracker | model/PositionTracker.java | Tracking Entity |
| 値オブジェクト | イベントタイプ | EventType | constant/EventType.java | Enum |
| 値オブジェクト | アクションタイプ | ActionType | constant/ActionType.java | Enum |
| サービス | イベントログ管理 | EventLogService | service/EventLogService.java | @Service |
| リポジトリ | イベント永続化 | EventsRepository | repository/EventsRepository.java | ScalarDB Repository |
| リポジトリ | アイテムイベント永続化 | ItemEventsRepository | repository/ItemEventsRepository.java | ScalarDB Repository |
| リポジトリ | 監査人ログ永続化 | AuditorLogsRepository | repository/AuditorLogsRepository.java | ScalarDB Repository |
| API | イベント履歴照会 | EventLogController | controller/EventLogController.java | @RestController |
| ビジネス | イベントログ処理 | EventLogBusiness | business/EventLogBusiness.java | Business Logic |

### ファイル管理・検証ドメイン

| 概念カテゴリ | 設計書での名称 | コード上の実装 | ファイルパス | 実装パターン |
|------------|--------------|--------------|-------------|-------------|
| エンティティ | アイテム | Item | model/Item.java | Lombok @Builder Entity |
| エンティティ | アイテムステータス | ItemStatus | model/ItemStatus.java | Status Entity |
| エンティティ | SHA1別アイテム | ItemsBySha1 | model/ItemsBySha1.java | Index Entity |
| 値オブジェクト | アイテムタイプ | ItemType | constant/ItemType.java | Enum (FILE, FOLDER) |
| 値オブジェクト | 改ざんステータス | TamperingStatusType | constant/TamperingStatusType.java | Enum |
| サービス | ファイル管理 | FileService | service/FileService.java | @Service |
| サービス | フォルダ管理 | FolderService | service/FolderService.java | @Service |
| サービス | アセット管理（検証） | AssetService | service/AssetService.java | @Service (ScalarDL) |
| リポジトリ | アイテム永続化 | ItemRepository | repository/ItemRepository.java | ScalarDB Repository |
| リポジトリ | アイテムステータス永続化 | ItemStatusRepository | repository/ItemStatusRepository.java | ScalarDB Repository |
| リポジトリ | ScalarDL連携 | ScalardlRepository | repository/ScalardlRepository.java | ScalarDL Repository |
| API | ファイル操作 | FileController | controller/FileController.java | @RestController |
| API | フォルダ操作 | FolderController | controller/FolderController.java | @RestController |
| API | アイテム操作 | ItemController | controller/ItemController.java | @RestController |
| DTO | ファイル詳細 | ItemDetails | dto/ItemDetails.java | Response DTO |
| DTO | ファイルバージョン | FileVersion | dto/FileVersion.java | Response DTO |
| レスポンス | 検証ステータス | VerifyItemStatus | responsedto/VerifyItemStatus.java | Response DTO |
| ユーティリティ | BOX API連携 | BoxUtility | utility/BoxUtility.java | Utility Class |

### ユーザー・認証ドメイン

| 概念カテゴリ | 設計書での名称 | コード上の実装 | ファイルパス | 実装パターン |
|------------|--------------|--------------|-------------|-------------|
| エンティティ | ユーザー | User | model/User.java | Lombok @Builder Entity |
| エンティティ | ロールユーザー | RoleUser | model/RoleUser.java | Mapping Entity |
| エンティティ | ユーザートークン | UserToken | model/UserToken.java | Token Entity |
| エンティティ | ユーザーOTP | UserOtp | model/UserOtp.java | OTP Entity |
| エンティティ | ユーザー監査グループ | UserAuditGroup | model/UserAuditGroup.java | Mapping Entity |
| エンティティ | 組織 | Organization | model/Organization.java | Organization Entity |
| 値オブジェクト | ユーザーロール | UserRoles | constant/UserRoles.java | Enum |
| 値オブジェクト | コラボレーターロール | CollaboratorUserRoles | constant/CollaboratorUserRoles.java | Enum |
| 値オブジェクト | 監査グループ権限 | AuditGroupPrivileges | constant/AuditGroupPrivileges.java | Enum |
| 値オブジェクト | アクセスステータス | AccessStatus | constant/AccessStatus.java | Enum |
| サービス | ユーザー管理 | UserService | service/UserService.java | @Service |
| サービス | 共通サービス | CommonService | service/CommonService.java | @Service |
| リポジトリ | ユーザー永続化 | UserRepository | repository/UserRepository.java | ScalarDB Repository |
| リポジトリ | ロールユーザー永続化 | RoleUserRepository | repository/RoleUserRepository.java | ScalarDB Repository |
| リポジトリ | トークン永続化 | UserTokenRepository | repository/UserTokenRepository.java | ScalarDB Repository |
| API | ユーザー管理 | UserController | controller/UserController.java | @RestController |
| セキュリティ | JWT認証フィルタ | JwtAuthFilter | security/JwtAuthFilter.java | Filter |
| セキュリティ | JWTヘルパー | JwtHelper | security/JwtHelper.java | JWT Utility |
| セキュリティ | セキュリティ設定 | SecurityConfig | security/SecurityConfig.java | @Configuration |
| DTO | ログイン要求 | LoginRequest | dto/LoginRequest.java | Request DTO |
| DTO | トークン送信 | SubmitToken | dto/SubmitToken.java | Request DTO |
| DTO | ユーザー編集 | EditUserDto | dto/EditUserDto.java | Request DTO |
| ユーティリティ | メール送信 | EmailUtility | utility/EmailUtility.java | Utility Class |

### フロントエンドマッピング

| 概念カテゴリ | 設計書での名称 | コード上の実装 | ファイルパス | 実装パターン |
|------------|--------------|--------------|-------------|-------------|
| 画面 | ログイン画面 | LoginandSignup | pages/auth/LoginandSignup.jsx | React Component |
| 画面 | パスワードリセット | ForgotPassword | pages/auth/ForgotPassword.jsx | React Component |
| 画面 | 監査セット管理 | AuditSet | pages/AuditSet/AuditSet.jsx | React Component |
| 画面 | 外部監査人・グループ管理 | AuditorsAndGroups | pages/AuditorsAndGroups/AuditorsAndGroups.jsx | React Component |
| 画面 | ユーザーロール管理 | UserRole | pages/UserRole/UserRole.jsx | React Component |
| 画面 | 外部監査人ビュー | ExternalAuditor | pages/ExternalAuditorPage/ExternalAuditor.jsx | React Component |
| 画面 | イベント履歴 | ViewAllEventHistory | pages/ViewAllEventHistory/ViewAllEventHistory.jsx | React Component |
| 画面 | 監査対象アイテム | ViewItemsUnderAudit | pages/ViewItemsUnderAudit/ | React Component |
| 状態管理 | 認証状態 | authSlice | redux/reducerSlice/authSlice.js | Redux Slice |
| 状態管理 | トークン状態 | tokenSlice | redux/reducerSlice/tokenSlice.js | Redux Slice |
| 状態管理 | ファイル・フォルダ状態 | folderAndFileSlice | redux/reducerSlice/folderAndFileSlice.js | Redux Slice |
| フック | 認証済みAxios | useAxiosPrivate | hooks/useAxiosPrivate.js | Custom Hook |
| フック | トークンリフレッシュ | useRefreshToken | hooks/useRefreshToken.js | Custom Hook |

## データベーススキーママッピング

| 設計概念 | テーブル名 | モデルクラス | 備考 |
|---------|-----------|------------|------|
| BOXイベントログ | scalar_box.events | Events | パーティションキー: yyyy_mm_dd |
| アイテム別イベント | scalar_box.item_events | ItemEvents | パーティションキー: item_id |
| 監査セット | scalar_box.audit_set | AuditSet | パーティションキー: audit_set_id |
| 監査セットコラボレーター | scalar_box.audit_set_collaborators | AuditSetCollaborators | パーティションキー: user_email |
| 監査セット-アイテムマッピング | scalar_box.auditset_folder_file_mapping | AuditSetItem | パーティションキー: audit_set_id |
| ユーザー | scalar_box.user | User | パーティションキー: user_email |
| ロールユーザー | scalar_box.role_user | RoleUser | パーティションキー: role_name |
| 監査グループ | scalar_box.audit_group | AuditGroup | パーティションキー: audit_group_id |
| ユーザー監査グループ | scalar_box.user_audit_group | UserAuditGroup | パーティションキー: user_email |
| 監査グループ-監査セット | scalar_box.audit_grp_audit_set_mapping | AuditGrpAuditSetMapping | パーティションキー: audit_group_id |
| アイテムステータス | scalar_box.item_status | ItemStatus | パーティションキー: item_id |
| SHA1別アイテム | scalar_box.items_by_sha1 | ItemsBySha1 | パーティションキー: sha1_hash |
| 監査人ログ | scalar_box.auditor_logs | AuditorLogs | パーティションキー: audit_set_id |
| イベント取得位置 | scalar_box.position_tracker | PositionTracker | パーティションキー: user_id |
| ユーザートークン | scalar_box.user_token | UserToken | パーティションキー: user_email |
| ユーザーOTP | scalar_box.user_otp | UserOtp | パーティションキー: user_email |
| 組織 | scalar_box.organization | Organization | パーティションキー: org_id |

## 未マッピング項目

### 設計書にあるがコードにない

| 概念 | 設計書での記載箇所 | 考えられる理由 |
|-----|------------------|--------------|
| ITEM_CREATE イベント | Scalar-Auditor-For-BOX-HL-Design.md | EventType enumに未実装（BOX APIから取得可能だが処理対象外） |
| イベントタイプの設定可能化 | Scalar-Auditor-For-BOX-HL-Design.md | Release 1.0では未実装と明記 |
| 検証時間の最適化 | features.md | V3.0で予定と明記 |

### コードにあるが設計書にない

| クラス/関数 | ファイルパス | 推測されるドメイン |
|------------|-------------|------------------|
| SystemEventDates | model/SystemEventDates.java | イベント日付管理（内部管理用） |
| EnterpriseEventLogs | model/EnterpriseEventLogs.java | エンタープライズイベントログ（拡張機能） |
| LanguageSupported | constant/LanguageSupported.java | 国際化サポート |
| ErrorLogMessages | constant/ErrorLogMessages.java | エラーメッセージ定数 |
| Constants | constant/Constants.java | システム定数 |
| GenericUtility | utility/GenericUtility.java | 汎用ユーティリティ |
| Translator | utility/Translator.java | 翻訳ユーティリティ |

## 実装パターン概要

### バックエンド実装パターン

| パターン | 使用箇所 | 説明 |
|---------|---------|------|
| Lombok @Builder | 全Model | ビルダーパターンによるオブジェクト生成 |
| @RestController | 全Controller | REST APIエンドポイント |
| @Service | 全Service | ビジネスロジックコンテナ |
| Repository Pattern | 全Repository | ScalarDBアクセス層 |
| DTO Pattern | dto/, responsedto/ | リクエスト/レスポンスデータ転送 |
| JWT Authentication | security/ | トークンベース認証 |

### フロントエンド実装パターン

| パターン | 使用箇所 | 説明 |
|---------|---------|------|
| React Functional Component | 全pages/ | 関数コンポーネント |
| Redux Toolkit | redux/ | 状態管理 |
| Custom Hooks | hooks/ | ロジック再利用 |
| i18next | i18n/ | 国際化 |
| Material UI | 全UI | UIコンポーネントライブラリ |
| Axios | api/ | HTTP通信 |
