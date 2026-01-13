# MMI モジュール別詳細評価

## 評価方法

各モジュールを4軸（Cohesion, Coupling, Independence, Reusability）で評価し、
重み付け（30%, 30%, 20%, 20%）でMMIスコアを算出します。

---

## バックエンドサービス詳細

### 1. UserService (MMI: 34.0 - 未成熟)

**ファイル**: `service/UserService.java`
**行数**: 1,118行
**依存数**: 20+

| 軸 | スコア | 評価理由 |
|----|--------|----------|
| Cohesion | 25/100 | 6以上の責務が混在（認証、CRUD、ロール、トークン、メール、BOX連携） |
| Coupling | 30/100 | 多数のリポジトリ・ユーティリティに依存、循環依存リスク |
| Independence | 40/100 | 他サービスからの依存が多く、単独デプロイ困難 |
| Reusability | 45/100 | 汎用ロジックとドメイン固有が混在 |

**責務分析**:
```
UserService (God Class)
├── 認証関連
│   ├── login()
│   ├── verifyPassword()
│   └── handleOAuth()
├── ユーザーCRUD
│   ├── createUser()
│   ├── updateUser()
│   └── deleteUser()
├── ロール管理
│   ├── assignRole()
│   ├── removeRole()
│   └── getRoles()
├── トークン管理
│   ├── generateToken()
│   ├── refreshToken()
│   └── revokeToken()
├── メール送信
│   ├── sendOTP()
│   └── sendWelcomeEmail()
└── BOX連携
    ├── syncBoxUser()
    └── getBoxToken()
```

**推奨分割**:
1. `AuthenticationService` - 認証・認可
2. `UserManagementService` - ユーザーCRUD
3. `RoleService` - ロール管理
4. `TokenService` - トークン管理
5. `NotificationService` - メール送信
6. `BoxIntegrationService` - BOX連携

---

### 2. AuditSetService (MMI: 46.5 - 低中成熟度)

**ファイル**: `service/AuditSetService.java`
**行数**: 845行
**依存数**: 12

| 軸 | スコア | 評価理由 |
|----|--------|----------|
| Cohesion | 40/100 | 監査セット管理に集中しているが、コラボレーター管理も含む |
| Coupling | 45/100 | CommonService、複数リポジトリに依存 |
| Independence | 50/100 | 監査セットドメインとして独立可能だが依存多い |
| Reusability | 55/100 | 監査セット操作は再利用可能 |

**責務分析**:
```
AuditSetService
├── 監査セットCRUD
│   ├── createAuditSet()
│   ├── updateAuditSet()
│   └── deleteAuditSet()
├── コラボレーター管理（分離候補）
│   ├── addCollaborator()
│   └── removeCollaborator()
└── 所有者変更
    └── changeOwner()
```

---

### 3. AuditSetItemService (MMI: 41.5 - 低中成熟度)

**ファイル**: `service/AuditSetItemService.java`
**行数**: 902行
**依存数**: 15

| 軸 | スコア | 評価理由 |
|----|--------|----------|
| Cohesion | 35/100 | アイテム管理とBOX連携が混在 |
| Coupling | 40/100 | AuditSetService、AssetServiceに強依存 |
| Independence | 45/100 | 監査セットサービスなしでは動作不可 |
| Reusability | 50/100 | 監査セットアイテム固有のロジック |

**問題点**:
- AuditSetServiceへの直接参照
- BOX API呼び出しとビジネスロジックの混在
- トランザクション境界が不明確

---

### 4. AuditGroupService (MMI: 57.5 - 中成熟度)

**ファイル**: `service/AuditGroupService.java`
**行数**: 420行
**依存数**: 7

| 軸 | スコア | 評価理由 |
|----|--------|----------|
| Cohesion | 55/100 | 監査グループ管理に集中 |
| Coupling | 55/100 | 適度な依存関係 |
| Independence | 60/100 | 比較的独立してデプロイ可能 |
| Reusability | 65/100 | グループ管理ロジックは汎用的 |

**良い点**:
- 単一責任に近い設計
- 依存関係が限定的
- テスト容易性が高い

---

### 5. FileService (MMI: 52.5 - 低中成熟度)

**ファイル**: `service/FileService.java`
**行数**: 580行
**依存数**: 10

| 軸 | スコア | 評価理由 |
|----|--------|----------|
| Cohesion | 50/100 | ファイル操作に集中だがUserService依存 |
| Coupling | 50/100 | UserServiceへの不必要な依存 |
| Independence | 55/100 | UserService分離後は独立可能 |
| Reusability | 60/100 | ファイル操作は汎用的 |

**問題点**:
- `UserService`への依存（ユーザー情報取得のため）
- BOX APIとのインターフェースが直接埋め込み

---

### 6. EventLogService (MMI: 72.5 - 中成熟度)

**ファイル**: `service/EventLogService.java`
**行数**: 350行
**依存数**: 5

| 軸 | スコア | 評価理由 |
|----|--------|----------|
| Cohesion | 70/100 | イベントログ管理に特化 |
| Coupling | 75/100 | リポジトリのみに依存 |
| Independence | 70/100 | 独立してデプロイ可能 |
| Reusability | 75/100 | イベント管理は他コンテキストでも利用可能 |

**良い点**:
- 明確な単一責任
- 低い外部依存
- マイクロサービス化の候補

---

### 7. AssetService (MMI: 67.5 - 中成熟度)

**ファイル**: `service/AssetService.java`
**行数**: 480行
**依存数**: 6

| 軸 | スコア | 評価理由 |
|----|--------|----------|
| Cohesion | 65/100 | ScalarDL連携に特化 |
| Coupling | 70/100 | ScalarDLリポジトリのみに依存 |
| Independence | 65/100 | ScalarDL依存だが分離可能 |
| Reusability | 70/100 | 検証ロジックは汎用的 |

**良い点**:
- 改ざん検知という明確な責務
- 外部システム連携が適切にカプセル化

---

### 8. CommonService (MMI: 78.5 - 高成熟度)

**ファイル**: `service/CommonService.java`
**行数**: 280行
**依存数**: 3

| 軸 | スコア | 評価理由 |
|----|--------|----------|
| Cohesion | 80/100 | 共通ユーティリティ機能に特化 |
| Coupling | 85/100 | 最小限の依存 |
| Independence | 75/100 | ユーティリティとして独立 |
| Reusability | 70/100 | 複数サービスで再利用可能 |

**注意点**:
- ユーティリティクラスとして適切だが、サービス層にあるべきか検討

---

### 9. FolderService (MMI: 62.5 - 中成熟度)

**ファイル**: `service/FolderService.java`
**行数**: 420行
**依存数**: 8

| 軸 | スコア | 評価理由 |
|----|--------|----------|
| Cohesion | 60/100 | フォルダ操作に集中 |
| Coupling | 65/100 | FileServiceと類似の依存パターン |
| Independence | 60/100 | FileServiceと統合検討可能 |
| Reusability | 65/100 | フォルダ操作は汎用的 |

---

### 10. AuditSetCollaboratorService (MMI: 57.5 - 中成熟度)

**ファイル**: `service/AuditSetCollaboratorService.java`
**行数**: 380行
**依存数**: 7

| 軸 | スコア | 評価理由 |
|----|--------|----------|
| Cohesion | 55/100 | コラボレーター管理に集中 |
| Coupling | 60/100 | AuditSetServiceへの依存あり |
| Independence | 55/100 | 監査セットと密結合 |
| Reusability | 60/100 | コラボレーター管理は汎用化可能 |

---

## フロントエンドモジュール詳細

### 1. pages/auth (MMI: 57.5)

| 軸 | スコア | 評価理由 |
|----|--------|----------|
| Cohesion | 60/100 | 認証画面に集中 |
| Coupling | 55/100 | Redux storeとAPI hookに依存 |
| Independence | 60/100 | 認証モジュールとして独立可能 |
| Reusability | 55/100 | プロジェクト固有のUI |

---

### 2. pages/AuditSet (MMI: 47.5)

| 軸 | スコア | 評価理由 |
|----|--------|----------|
| Cohesion | 45/100 | 1000行超の大規模コンポーネント |
| Coupling | 50/100 | 多数のサブコンポーネントに依存 |
| Independence | 50/100 | 分割後は独立可能 |
| Reusability | 45/100 | プロジェクト固有 |

**問題点**:
- コンポーネントが大きすぎる
- 状態管理が複雑
- サブコンポーネント分割が必要

---

### 3. redux (MMI: 60.0)

| 軸 | スコア | 評価理由 |
|----|--------|----------|
| Cohesion | 65/100 | Slice単位で適切に分離 |
| Coupling | 60/100 | 適度な依存関係 |
| Independence | 55/100 | アプリ全体の状態管理 |
| Reusability | 60/100 | Sliceパターンは再利用可能 |

---

## スコア分布図

```
         0    20    40    60    80   100
         |     |     |     |     |     |
UserService          ████████████░░░░░░░░░░░░░░░░░░░ 34.0 ❌
AuditSetItemService  ████████████████░░░░░░░░░░░░░░░ 41.5
AuditSetService      █████████████████████░░░░░░░░░░ 46.5
FileService          ████████████████████████░░░░░░░ 52.5
AuditGroupService    █████████████████████████████░░ 57.5
AudSetCollabService  █████████████████████████████░░ 57.5
FolderService        ██████████████████████████████░ 62.5
AssetService         ████████████████████████████████ 67.5 ✓
EventLogService      █████████████████████████████████ 72.5 ✓
CommonService        ███████████████████████████████████ 78.5 ✓
         |     |     |     |     |     |
         0    20    40    60    80   100

Legend: ❌ 要緊急対応  ✓ マイクロサービス化適格
```

## 評価サマリー

### マイクロサービス化適格モジュール
1. **EventLogService** (72.5) - イベント管理サービスとして独立可能
2. **AssetService** (67.5) - 検証サービスとして独立可能
3. **CommonService** (78.5) - 共有ライブラリとして提供

### 要改善モジュール
1. **UserService** (34.0) - 6サービスへの分割が必須
2. **AuditSetItemService** (41.5) - BOX連携の分離が必要
3. **AuditSetService** (46.5) - コラボレーター管理の分離

### 統合検討モジュール
- FileService + FolderService → ItemService への統合
- AuditSetCollaboratorService → AuditSetService への統合またはAccess Controlサービス化
