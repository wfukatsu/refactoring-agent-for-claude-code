# ドメイン-コード対応表

## マッピング概要

Scalar Auditor for BOXは、Spring Bootのレイヤードアーキテクチャを採用しています。ドメインモデルは `model` パッケージに配置され、ビジネスロジックは `service` パッケージで実装されています。

```
application/
├── controller/     # REST APIエンドポイント
├── service/        # ビジネスロジック
├── repository/     # データアクセス層（ScalarDB）
├── model/          # ドメインモデル（エンティティ）
├── dto/            # データ転送オブジェクト
├── responsedto/    # APIレスポンス用DTO
├── constant/       # 定数・Enum
├── security/       # 認証・認可
├── business/       # BOX連携ビジネスロジック
├── config/         # 設定クラス
├── exception/      # 例外クラス
└── utility/        # ユーティリティ
```

## ドメイン別マッピング

### 1. ユーザー管理ドメイン

| 概念カテゴリ | 設計書での名称 | コード上の実装 | ファイルパス | 実装パターン |
|------------|--------------|--------------|-------------|-------------|
| エンティティ | Organization User | `User` | model/User.java | Data Model |
| エンティティ | Organization | `Organization` | model/Organization.java | Data Model |
| エンティティ | User Token | `UserToken` | model/UserToken.java | Data Model |
| エンティティ | User OTP | `UserOtp` | model/UserOtp.java | Data Model |
| 値オブジェクト | User Role | `UserRoles` | constant/UserRoles.java | Enum |
| リポジトリ | User保存 | `UserRepository` | repository/UserRepository.java | Repository |
| リポジトリ | Org保存 | `OrganizationRepository` | repository/OrganizationRepository.java | Repository |
| サービス | ユーザー管理 | `UserService` | service/UserService.java | Service |
| コントローラ | ユーザーAPI | `UserController` | controller/UserController.java | REST Controller |

### 2. 監査セット管理ドメイン

| 概念カテゴリ | 設計書での名称 | コード上の実装 | ファイルパス | 実装パターン |
|------------|--------------|--------------|-------------|-------------|
| エンティティ | Audit Set | `AuditSet` | model/AuditSet.java | Data Model |
| エンティティ | Audit Set Item | `AuditSetItem` | model/AuditSetItem.java | Data Model |
| エンティティ | Audit Set Collaborators | `AuditSetCollaborators` | model/AuditSetCollaborators.java | Data Model |
| 値オブジェクト | Collaborator Role | `CollaboratorUserRoles` | constant/CollaboratorUserRoles.java | Enum |
| リポジトリ | AuditSet保存 | `AuditSetRepository` | repository/AuditSetRepository.java | Repository |
| リポジトリ | AuditSetItem保存 | `AuditSetItemRepository` | repository/AuditSetItemRepository.java | Repository |
| サービス | 監査セット管理 | `AuditSetService` | service/AuditSetService.java | Service |
| サービス | 監査セットアイテム管理 | `AuditSetItemService` | service/AuditSetItemService.java | Service |
| サービス | コラボレーター管理 | `AuditSetCollaboratorService` | service/AuditSetCollaboratorService.java | Service |
| コントローラ | 監査セットAPI | `AuditSetController` | controller/AuditSetController.java | REST Controller |
| コントローラ | コラボレーターAPI | `AuditSetCollaboratorController` | controller/AuditSetCollaboratorController.java | REST Controller |

### 3. 監査グループ管理ドメイン

| 概念カテゴリ | 設計書での名称 | コード上の実装 | ファイルパス | 実装パターン |
|------------|--------------|--------------|-------------|-------------|
| エンティティ | Audit Group | `AuditGroup` | model/AuditGroup.java | Data Model |
| エンティティ | User-AuditGroup関連 | `UserAuditGroup` | model/UserAuditGroup.java | Data Model |
| エンティティ | AuditGroup-AuditSet関連 | `AuditGrpAuditSetMapping` | model/AuditGrpAuditSetMapping.java | Data Model |
| 値オブジェクト | Group Privileges | `AuditGroupPrivileges` | constant/AuditGroupPrivileges.java | Enum |
| リポジトリ | AuditGroup保存 | `AuditGroupRepository` | repository/AuditGroupRepository.java | Repository |
| リポジトリ | UserAuditGroup保存 | `UserAuditGroupRepository` | repository/UserAuditGroupRepository.java | Repository |
| サービス | 監査グループ管理 | `AuditGroupService` | service/AuditGroupService.java | Service |
| コントローラ | 監査グループAPI | `AuditGroupController` | controller/AuditGroupController.java | REST Controller |

### 4. イベントログ管理ドメイン

| 概念カテゴリ | 設計書での名称 | コード上の実装 | ファイルパス | 実装パターン |
|------------|--------------|--------------|-------------|-------------|
| エンティティ | Event | `Events` | model/Events.java | Data Model |
| エンティティ | Enterprise Event Logs | `EnterpriseEventLogs` | model/EnterpriseEventLogs.java | Data Model |
| エンティティ | Item Events | `ItemEvents` | model/ItemEvents.java | Data Model |
| エンティティ | Auditor Logs | `AuditorLogs` | model/AuditorLogs.java | Data Model |
| エンティティ | System Event Dates | `SystemEventDates` | model/SystemEventDates.java | Data Model |
| 値オブジェクト | Event Type | `EventType` | constant/EventType.java | Enum |
| リポジトリ | Events保存 | `EventsRepository` | repository/EventsRepository.java | Repository |
| リポジトリ | EnterpriseEventLogs保存 | `EnterpriseEventLogsRepository` | repository/EnterpriseEventLogsRepository.java | Repository |
| サービス | イベントログ管理 | `EventLogService` | service/EventLogService.java | Service |
| コントローラ | イベントログAPI | `EventLogController` | controller/EventLogController.java | REST Controller |
| バッチ処理 | イベント取得 | `EventListener` | EventListener.java | Event Listener |

### 5. ファイル・アイテム管理ドメイン

| 概念カテゴリ | 設計書での名称 | コード上の実装 | ファイルパス | 実装パターン |
|------------|--------------|--------------|-------------|-------------|
| エンティティ | Item | `Item` | model/Item.java | Data Model |
| エンティティ | Items By SHA1 | `ItemsBySha1` | model/ItemsBySha1.java | Data Model |
| エンティティ | Item Status | `ItemStatus` | model/ItemStatus.java | Data Model |
| 値オブジェクト | Item Type | `ItemType` | constant/ItemType.java | Enum |
| 値オブジェクト | Tampering Status | `TamperingStatusType` | constant/TamperingStatusType.java | Enum |
| リポジトリ | Item保存 | `ItemRepository` | repository/ItemRepository.java | Repository |
| リポジトリ | ItemsBySha1保存 | `ItemsBySha1Repository` | repository/ItemsBySha1Repository.java | Repository |
| サービス | ファイル管理 | `FileService` | service/FileService.java | Service |
| サービス | フォルダ管理 | `FolderService` | service/FolderService.java | Service |
| サービス | アセット管理 | `AssetService` | service/AssetService.java | Service |
| コントローラ | ファイルAPI | `FileController` | controller/FileController.java | REST Controller |
| コントローラ | フォルダAPI | `FolderController` | controller/FolderController.java | REST Controller |
| コントローラ | アイテムAPI | `ItemController` | controller/ItemController.java | REST Controller |

### 6. 外部連携ドメイン

| 概念カテゴリ | 設計書での名称 | コード上の実装 | ファイルパス | 実装パターン |
|------------|--------------|--------------|-------------|-------------|
| 連携 | BOX API連携 | `business/` パッケージ | business/*.java | Integration |
| 連携 | ScalarDL連携 | `ScalardlRepository` | repository/ScalardlRepository.java | Repository |
| 設定 | アプリケーション設定 | `ApplicationBeans` | ApplicationBeans.java | Configuration |

## 未マッピング項目

### 設計書にあるがコードで未確認

| 概念 | 設計書での記載箇所 | 考えられる理由 |
|-----|------------------|--------------|
| ファイルプレビュー機能 | HL Design | BOX SDKのプレビューURLを直接使用 |
| ファイルダウンロード機能 | HL Design | BOX SDKのダウンロードURLを直接使用 |

### コードにあるが設計書で言及なし

| クラス/関数 | ファイルパス | 推測されるドメイン |
|------------|-------------|------------------|
| `PositionTracker` | model/PositionTracker.java | イベント取得位置管理 |
| `RoleUser` | model/RoleUser.java | ロール-ユーザー関連 |
| `CommonService` | service/CommonService.java | 共通ユーティリティ |

## DTO一覧

### リクエストDTO

| DTO | 用途 | 関連ドメイン |
|----|------|------------|
| `LoginRequest` | ログインリクエスト | ユーザー管理 |
| `CreateAuditSet` | 監査セット作成 | 監査セット管理 |
| `UpdateAuditSet` | 監査セット更新 | 監査セット管理 |
| `CreateAuditGroup` | 監査グループ作成 | 監査グループ管理 |
| `AddItem` | アイテム追加 | ファイル管理 |
| `EditUserDto` | ユーザー編集 | ユーザー管理 |

### レスポンスDTO

responsedto/ パッケージに18種類のレスポンスDTO が定義されています。

## フロントエンド連携

### Scalar-Box-WebApp (React)

| 機能 | 関連API | 備考 |
|-----|--------|------|
| ダッシュボード | 複数API | メイン画面 |
| 監査セット管理 | AuditSet API | CRUD操作 |
| イベントログ閲覧 | EventLog API | フィルタリング機能 |
| ユーザー管理 | User API | 管理者機能 |

### Scalar-WebApp-Integration-Menu

| 機能 | 関連API | 備考 |
|-----|--------|------|
| BOX統合メニュー | 複数API | BOXアプリ内表示 |
| ファイル詳細表示 | File API | コンテキストメニュー |
| 監査セット追加 | AuditSetItem API | ファイル選択UI |
