# モジュール別MMI評価

## 評価基準

各軸 0-5 のスコアで評価。MMI = ((0.3×Cohesion + 0.3×Coupling + 0.2×Independence + 0.2×Reusability) / 5) × 100

---

## 1. UserService

**ファイル**: `service/UserService.java`
**行数**: 1,118行
**メソッド数**: 30+
**リポジトリ依存**: 11個

### スコア

| 評価軸 | スコア | 根拠 |
|-------|-------|------|
| Cohesion | 1 | 認証、CRUD、トークン、OTP、メール、監査関連が混在 |
| Coupling | 2 | 11個のリポジトリに直接依存、他サービスからも参照される |
| Independence | 2 | 分離困難、多数の責務が絡み合う |
| Reusability | 2 | BOX連携、JWT、メール送信がハードコード |

**MMI**: (0.3×1 + 0.3×2 + 0.2×2 + 0.2×2) / 5 × 100 = **28**

### 問題点

1. **責務の混在**
   - 認証ロジック（login, getToken, getNewAccessToken）
   - ユーザーCRUD（createUser, deleteUser, editUser）
   - トークン管理（registerUserAndSaveToken, updateLatestToken）
   - OTP/パスワードリセット（sendResetPasswordOTP, forgotPassword）
   - 監査セット連携（processAuditSetsForUser, processCollaboratorForAuditSet）
   - 監査グループ連携（processUserGroupsForUser, processAuditGroupForUser）

2. **過剰な依存関係**
   - UserRepository, RoleUserRepository, UserTokenRepository
   - OrganizationRepository, AuditSetRepository
   - AuditSetCollaboratorsRepository, AuditGroupRepository
   - UserAuditGroupRepository, AuditorLogsRepository, UserOptRepository
   - UserDetailsService

### 改善提案

```
UserService（現状） → 分割後
├── AuthenticationService     # login, getToken, JWT処理
├── UserManagementService     # createUser, deleteUser, editUser, updateUserRole
├── TokenService              # registerUserAndSaveToken, updateLatestToken
├── PasswordResetService      # sendResetPasswordOTP, forgotPassword
└── UserAuditIntegrationService # 監査セット・グループ連携
```

---

## 2. AuditSetItemService

**ファイル**: `service/AuditSetItemService.java`
**行数**: 902行
**リポジトリ依存**: 7個

### スコア

| 評価軸 | スコア | 根拠 |
|-------|-------|------|
| Cohesion | 2 | アイテム追加、権限管理、BOX連携が混在 |
| Coupling | 2 | 7リポジトリ + AuditSetService依存 |
| Independence | 2 | AuditSetServiceとの循環依存の可能性 |
| Reusability | 3 | BOX SDK経由で一部再利用可能 |

**MMI**: (0.3×2 + 0.3×2 + 0.2×2 + 0.2×3) / 5 × 100 = **36**

### 問題点

1. AuditSetServiceへの依存
2. 複雑なアイテム追加ロジック
3. BOX API呼び出しが散在

---

## 3. AuditSetService

**ファイル**: `service/AuditSetService.java`
**行数**: 845行
**リポジトリ依存**: 9個

### スコア

| 評価軸 | スコア | 根拠 |
|-------|-------|------|
| Cohesion | 2 | 監査セットCRUD、コラボレーター、グループ連携が混在 |
| Coupling | 2 | 9リポジトリ依存 |
| Independence | 2 | 共有DBに強く依存 |
| Reusability | 3 | ドメインロジックは再利用可能 |

**MMI**: (0.3×2 + 0.3×2 + 0.2×2 + 0.2×3) / 5 × 100 = **38**

### 問題点

1. コラボレーター管理が別サービスと重複
2. 監査グループとの連携ロジックが複雑
3. JSONフィールドでの関連管理

---

## 4. AuditGroupService

**ファイル**: `service/AuditGroupService.java`
**行数**: 438行
**リポジトリ依存**: 5個

### スコア

| 評価軸 | スコア | 根拠 |
|-------|-------|------|
| Cohesion | 3 | グループCRUDに集中しているが、メンバー管理も含む |
| Coupling | 2 | 5リポジトリ依存 |
| Independence | 3 | 比較的独立 |
| Reusability | 2 | 特定ドメインに依存 |

**MMI**: (0.3×3 + 0.3×2 + 0.2×3 + 0.2×2) / 5 × 100 = **46**

---

## 5. FileService

**ファイル**: `service/FileService.java`
**行数**: 554行
**リポジトリ依存**: 6個

### スコア

| 評価軸 | スコア | 根拠 |
|-------|-------|------|
| Cohesion | 3 | ファイル操作に集中 |
| Coupling | 2 | 6リポジトリ + UserService依存 |
| Independence | 3 | BOX連携は分離可能 |
| Reusability | 3 | BOX SDK経由で再利用可能 |

**MMI**: (0.3×3 + 0.3×2 + 0.2×3 + 0.2×3) / 5 × 100 = **50**

---

## 6. AssetService

**ファイル**: `service/AssetService.java`
**行数**: 198行
**リポジトリ依存**: 1個（ScalarDL）

### スコア

| 評価軸 | スコア | 根拠 |
|-------|-------|------|
| Cohesion | 3 | アセット管理に集中 |
| Coupling | 3 | ScalarDL依存のみ |
| Independence | 2 | ScalarDLに強く依存 |
| Reusability | 3 | アセット検証は再利用可能 |

**MMI**: (0.3×3 + 0.3×3 + 0.2×2 + 0.2×3) / 5 × 100 = **52**

---

## 7. AuditSetCollaboratorService

**ファイル**: `service/AuditSetCollaboratorService.java`
**行数**: 287行
**リポジトリ依存**: 4個

### スコア

| 評価軸 | スコア | 根拠 |
|-------|-------|------|
| Cohesion | 3 | コラボレーター管理に集中 |
| Coupling | 3 | 4リポジトリ + CommonService |
| Independence | 3 | 比較的独立 |
| Reusability | 3 | ドメインロジックは再利用可能 |

**MMI**: (0.3×3 + 0.3×3 + 0.2×3 + 0.2×3) / 5 × 100 = **54**

---

## 8. CommonService

**ファイル**: `service/CommonService.java`
**行数**: 32行
**リポジトリ依存**: 0個

### スコア

| 評価軸 | スコア | 根拠 |
|-------|-------|------|
| Cohesion | 3 | 共通処理のみ |
| Coupling | 4 | 依存なし |
| Independence | 3 | 独立性高い |
| Reusability | 3 | 再利用可能 |

**MMI**: (0.3×3 + 0.3×4 + 0.2×3 + 0.2×3) / 5 × 100 = **58**

---

## 9. FolderService

**ファイル**: `service/FolderService.java`
**行数**: 114行
**リポジトリ依存**: 0個（UserService依存）

### スコア

| 評価軸 | スコア | 根拠 |
|-------|-------|------|
| Cohesion | 4 | フォルダ操作に集中 |
| Coupling | 3 | UserService依存のみ |
| Independence | 3 | BOX連携で独立可能 |
| Reusability | 3 | BOX SDK経由で再利用可能 |

**MMI**: (0.3×4 + 0.3×3 + 0.2×3 + 0.2×3) / 5 × 100 = **62**

---

## 10. EventLogService

**ファイル**: `service/EventLogService.java`
**行数**: 221行
**リポジトリ依存**: 2個

### スコア

| 評価軸 | スコア | 根拠 |
|-------|-------|------|
| Cohesion | 4 | イベントログ検索に集中 |
| Coupling | 4 | 2リポジトリのみ |
| Independence | 3 | 読み取り専用で独立可能 |
| Reusability | 3 | クエリロジックは再利用可能 |

**MMI**: (0.3×4 + 0.3×4 + 0.2×3 + 0.2×3) / 5 × 100 = **66**

---

## 11. EventListener

**ファイル**: `EventListener.java`
**行数**: 548行
**依存**: 多数（BOX SDK、複数リポジトリ）

### スコア

| 評価軸 | スコア | 根拠 |
|-------|-------|------|
| Cohesion | 2 | イベント取得、パース、保存が混在 |
| Coupling | 2 | 多数の依存 |
| Independence | 2 | バッチ処理として独立可能だが現状は密結合 |
| Reusability | 2 | BOXイベント形式に強く依存 |

**MMI**: (0.3×2 + 0.3×2 + 0.2×2 + 0.2×2) / 5 × 100 = **32**

### 改善提案

```
EventListener（現状） → 分割後
├── BoxEventFetcher        # BOX APIからイベント取得
├── EventParser            # イベントデータのパース
├── EventPersistenceService # イベントの保存
└── PositionTracker        # 取得位置の管理
```

---

## 評価サマリー

| ランク | モジュール | MMI | 改善優先度 |
|-------|----------|-----|----------|
| 1 | EventLogService | 66 | 低 |
| 2 | FolderService | 62 | 低 |
| 3 | CommonService | 58 | 低 |
| 4 | AuditSetCollaboratorService | 54 | 中 |
| 5 | AssetService | 52 | 中 |
| 6 | FileService | 50 | 中 |
| 7 | AuditGroupService | 46 | 中 |
| 8 | AuditSetService | 38 | 高 |
| 9 | AuditSetItemService | 36 | 高 |
| 10 | EventListener | 32 | 高 |
| 11 | UserService | 28 | 最高 |
