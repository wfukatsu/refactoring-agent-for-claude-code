# MMI改善計画

## 改善目標

| 指標 | 現状 | Phase 1目標 | Phase 2目標 | 最終目標 |
|-----|------|------------|------------|---------|
| 平均MMI | 48 | 58 | 68 | 78+ |
| 未成熟モジュール数 | 3 | 1 | 0 | 0 |
| 最大行数（Service） | 1,118 | 400 | 300 | 250 |
| 最大リポジトリ依存 | 11 | 5 | 4 | 3 |

---

## Phase 1: 即時改善（1-2ヶ月）

### 1.1 UserService分割

**目標**: MMI 28 → 55+

**分割計画**:

```
UserService (現状: 1,118行, 11リポジトリ依存)
    ↓
├── AuthenticationService (新規: ~150行)
│   ├── login()
│   ├── getToken()
│   └── getNewAccessToken()
│   依存: UserRepository, UserDetailsService, JwtHelper
│
├── UserCrudService (新規: ~200行)
│   ├── createUser()
│   ├── deleteUser()
│   ├── editUser()
│   └── updateUserRole()
│   依存: UserRepository, RoleUserRepository
│
├── TokenService (新規: ~100行)
│   ├── registerUserAndSaveToken()
│   ├── updateLatestToken()
│   └── 関連ヘルパー
│   依存: UserTokenRepository
│
├── PasswordResetService (新規: ~120行)
│   ├── sendResetPasswordOTP()
│   ├── forgotPassword()
│   └── OTP関連
│   依存: UserOptRepository, EmailUtility
│
└── UserAuditBridgeService (新規: ~180行)
    ├── processAuditSetsForUser()
    ├── processCollaboratorForAuditSet()
    └── processUserGroupsForUser()
    依存: AuditSetRepository, AuditGroupRepository等
```

**工数**: 2-3週間
**リスク**: 低（内部リファクタリング）
**テスト**: 既存テストの分割対応必要

### 1.2 EventListener分割

**目標**: MMI 32 → 50+

**分割計画**:

```
EventListener (現状: 548行)
    ↓
├── BoxEventFetcherService (新規: ~150行)
│   └── BOX APIからのイベント取得
│
├── EventParserService (新規: ~100行)
│   └── イベントデータのパース・変換
│
├── EventPersistenceService (新規: ~150行)
│   └── イベントの保存（ScalarDB/DL）
│
└── EventPositionTracker (新規: ~50行)
    └── 取得位置の管理
```

**工数**: 1-2週間
**リスク**: 低
**テスト**: 単体テスト追加推奨

---

## Phase 2: 短期改善（2-3ヶ月）

### 2.1 AuditSetService/AuditSetItemService責務分離

**目標**: 合計MMI 74 → 110+（各55+）

**分割計画**:

```
AuditSetService (845行) + AuditSetItemService (902行)
    ↓
├── AuditSetCrudService (~250行)
│   └── 監査セットの作成・更新・削除
│
├── AuditSetQueryService (~200行)
│   └── 監査セットの検索・取得
│
├── AuditSetItemManagementService (~300行)
│   └── アイテムの追加・削除・更新
│
├── AuditSetValidationService (~150行)
│   └── 監査セットの検証（ScalarDL連携）
│
└── AuditSetCollaboratorIntegration (~200行)
    └── コラボレーター・グループ連携
```

**工数**: 2-3週間
**リスク**: 中（ビジネスロジックの理解必要）

### 2.2 リポジトリファサード導入

**目標**: 結合度低下（Coupling +1）

**実装計画**:

```java
// 例: UserDomainFacade
public class UserDomainFacade {
    private final UserRepository userRepository;
    private final RoleUserRepository roleUserRepository;
    private final OrganizationRepository organizationRepository;

    // 統合メソッド
    public UserWithRoles findUserWithRoles(Long userId) { ... }
    public void saveUserWithRole(User user, RoleUser role) { ... }
}
```

**対象**:
- UserDomainFacade（User, RoleUser, Organization）
- AuditDomainFacade（AuditSet, AuditSetItem, AuditSetCollaborators）
- EventDomainFacade（Events, ItemEvents, EnterpriseEventLogs）

**工数**: 2-3週間
**リスク**: 低

### 2.3 DTO統合・整理

**現状**: dto 47ファイル + responsedto 18ファイル = 65ファイル

**整理計画**:
- 重複DTOの統合
- 未使用DTOの削除
- ネーミング規則の統一

**工数**: 1週間
**リスク**: 低

---

## Phase 3: 中期改善（3-6ヶ月）

### 3.1 ドメインイベント導入

**目標**: Independence +1, Coupling +1

**導入箇所**:

```java
// 例: 監査セット作成時のイベント
public class AuditSetCreatedEvent {
    private final String auditSetId;
    private final Long ownerId;
    private final Instant createdAt;
}

// 例: ユーザー削除時のイベント
public class UserDeletedEvent {
    private final Long userId;
    private final Instant deletedAt;
}
```

**連携フロー**:
```
UserService.deleteUser()
    → UserDeletedEvent発行
        → AuditSetCollaboratorService: コラボレーター削除
        → AuditGroupService: グループから除外
```

**工数**: 1-2ヶ月
**リスク**: 中

### 3.2 データ所有権定義

**目標**: マイクロサービス化準備

**データ所有権マップ**:

| データ | 所有サービス | 参照サービス |
|-------|------------|------------|
| User, RoleUser, Organization | UserService | All |
| AuditSet, AuditSetItem, AuditSetCollaborators | AuditSetService | FileService, EventLogService |
| AuditGroup, UserAuditGroup | AuditGroupService | AuditSetService |
| Events, ItemEvents | EventLogService | FileService |
| Item, ItemsBySha1, ItemStatus | ItemService | AuditSetService |

**工数**: 2-3ヶ月（分析・設計含む）
**リスク**: 高

### 3.3 CQRS導入検討

**対象**: イベントログ検索（読み取り負荷が高い）

**構成**:
```
Write Model: EventLogService (現状)
Read Model: EventLogQueryService (新規)
    └── 読み取り最適化ビュー
```

**工数**: 1-2ヶ月
**リスク**: 中

---

## モニタリング計画

### MMI測定タイミング

| タイミング | 測定内容 |
|----------|---------|
| Phase開始時 | ベースライン測定 |
| 各施策完了時 | 対象モジュールのMMI再測定 |
| Phase終了時 | 全体MMI再測定 |

### 測定項目

```bash
# サービスクラス行数
wc -l service/*.java

# リポジトリ依存数
grep -c "Repository" service/*.java

# メソッド数
grep -c "public.*(" service/*.java
```

### 品質ゲート

| 指標 | 閾値 | アクション |
|-----|------|----------|
| サービス行数 | > 500 | 分割検討 |
| リポジトリ依存 | > 5 | ファサード検討 |
| メソッド数 | > 20 | 責務分析 |
| MMI | < 40 | 優先改善対象 |

---

## リスクと対策

| リスク | 影響 | 対策 |
|-------|-----|------|
| 分割時のバグ混入 | 高 | テストカバレッジ確保、段階的リリース |
| パフォーマンス低下 | 中 | 分割前後のベンチマーク |
| 開発チームの抵抗 | 中 | 改善効果の可視化、小さな成功から |
| スケジュール遅延 | 中 | バッファ確保、優先度の厳格化 |

---

## 成功基準

### Phase 1終了時
- [ ] UserServiceが5つ以下のサービスに分割
- [ ] 各分割サービスのMMI > 50
- [ ] EventListenerが4つのコンポーネントに分割
- [ ] 既存テストが全てパス

### Phase 2終了時
- [ ] 平均MMI > 65
- [ ] 未成熟モジュール数 = 0
- [ ] リポジトリファサード導入完了
- [ ] DTOファイル数 < 50

### Phase 3終了時
- [ ] 平均MMI > 75
- [ ] ドメインイベント導入完了
- [ ] データ所有権ドキュメント完成
- [ ] マイクロサービス化準備完了
