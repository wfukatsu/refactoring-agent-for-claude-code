# モジュール別MMI評価 (MMI by Module)

## 1. スコアサマリー

### 1.1 全モジュール一覧

| モジュール | 行数 | メソッド数 | Cohesion | Coupling | Independence | Reusability | **MMI** |
|-----------|-----|----------|:--------:|:--------:|:------------:|:-----------:|:-------:|
| UserService | 1116 | 29 | 1 | 1 | 2 | 2 | **28** |
| AuditSetItemService | 899 | 11 | 2 | 2 | 2 | 2 | **40** |
| AuditSetService | 845 | 14 | 2 | 2 | 2 | 2 | **40** |
| FileService | 555 | 11 | 2 | 2 | 3 | 3 | **48** |
| AuditGroupService | 439 | 8 | 3 | 2 | 3 | 3 | **54** |
| AuditSetCollaboratorService | 288 | 7 | 3 | 3 | 3 | 3 | **60** |
| EventLogService | 222 | 6 | 4 | 3 | 3 | 4 | **68** |
| AssetService | 199 | 5 | 3 | 3 | 2 | 3 | **54** |
| FolderService | 115 | 2 | 3 | 3 | 3 | 3 | **60** |
| CommonService | 33 | 3 | 4 | 4 | 4 | 4 | **80** |
| **平均** | - | - | **2.2** | **2.0** | **2.5** | **2.7** | **48.6** |

### 1.2 MMI計算式

```
MMI = ((0.3 × Cohesion + 0.3 × Coupling + 0.2 × Independence + 0.2 × Reusability) / 5) × 100
```

---

## 2. 個別モジュール評価

### 2.1 UserService

**基本情報**

| 項目 | 値 |
|------|-----|
| ファイル | `service/UserService.java` |
| 行数 | 1116 |
| メソッド数 | 29 |
| フィールド数 | 22 |
| **MMI** | **28** (未成熟) |

**スコア詳細**

| 軸 | スコア | 根拠 |
|----|:-----:|------|
| Cohesion | 1 | 認証、BOX連携、ユーザー管理、メール送信、トークン管理など多数の責務が集中 |
| Coupling | 1 | 10+リポジトリへの依存、他サービスとの双方向依存 |
| Independence | 2 | BOX API、ScalarDBへの強依存 |
| Reusability | 2 | BOX Platform固有ロジックが多く他システムでの再利用困難 |

**主な責務（問題）**

1. ユーザー認証・認可
2. BOX OAuth連携
3. ユーザーCRUD
4. パスワードリセット・OTP
5. メール送信
6. トークン管理
7. ロール管理
8. 言語設定

**改善提案**

```
UserService (1116行)
    ↓ 分割
├── AuthenticationService (~300行)
│   └── ログイン、JWT発行、パスワード検証
├── BoxUserService (~350行)
│   └── BOX OAuth、トークン更新、ユーザー情報取得
├── UserManagementService (~300行)
│   └── CRUD、ロール管理、一覧取得
└── PasswordResetService (~150行)
    └── OTP生成、メール送信、パスワード変更
```

---

### 2.2 AuditSetItemService

**基本情報**

| 項目 | 値 |
|------|-----|
| ファイル | `service/AuditSetItemService.java` |
| 行数 | 899 |
| メソッド数 | 11 |
| フィールド数 | 11 |
| **MMI** | **40** (低中成熟) |

**スコア詳細**

| 軸 | スコア | 根拠 |
|----|:-----:|------|
| Cohesion | 2 | アイテム管理、検証、監視ステータスの3責務が混在 |
| Coupling | 2 | AuditSetService、AssetServiceへの依存 |
| Independence | 2 | ScalarDB、ScalarDLへの依存 |
| Reusability | 2 | 監査セット固有のロジック |

**主な責務**

1. アイテム追加・削除
2. 監視ステータス管理
3. 改ざん検証
4. BOXアイテム情報取得

**改善提案**

```
AuditSetItemService (899行)
    ↓ 分割
├── AuditSetItemCrudService (~400行)
│   └── アイテムの追加・削除・一覧
├── ItemMonitoringService (~250行)
│   └── 監視ステータス管理
└── ItemVerificationService (~250行)
    └── 改ざん検証、検証結果管理
```

---

### 2.3 AuditSetService

**基本情報**

| 項目 | 値 |
|------|-----|
| ファイル | `service/AuditSetService.java` |
| 行数 | 845 |
| メソッド数 | 14 |
| フィールド数 | 11 |
| **MMI** | **40** (低中成熟) |

**スコア詳細**

| 軸 | スコア | 根拠 |
|----|:-----:|------|
| Cohesion | 2 | CRUD、コラボレーター管理、グループ管理が混在 |
| Coupling | 2 | 8つのリポジトリへの依存 |
| Independence | 2 | 複数テーブルへのトランザクション |
| Reusability | 2 | 監査セット固有のビジネスルール |

**主な責務**

1. 監査セットCRUD
2. コラボレーター管理
3. 監査グループとの関連管理
4. ACL管理

**改善提案**

```
AuditSetService (845行)
    ↓ 分割
├── AuditSetCrudService (~350行)
│   └── 作成・更新・削除・一覧
├── AuditSetAccessService (~250行)
│   └── コラボレーター管理、ACL
└── AuditSetGroupService (~200行)
    └── グループ関連付け
```

---

### 2.4 FileService

**基本情報**

| 項目 | 値 |
|------|-----|
| ファイル | `service/FileService.java` |
| 行数 | 555 |
| メソッド数 | 11 |
| フィールド数 | 8 |
| **MMI** | **48** (低中成熟) |

**スコア詳細**

| 軸 | スコア | 根拠 |
|----|:-----:|------|
| Cohesion | 2 | ファイル詳細、バージョン、コピー、ログの複数責務 |
| Coupling | 2 | UserService、BoxUtilityへの依存 |
| Independence | 3 | BOX APIへの外部依存のみ |
| Reusability | 3 | ファイル操作は比較的汎用的 |

**改善提案**

- `FileDetailsService`: ファイル詳細・メタデータ
- `FileVersionService`: バージョン管理
- `FileCollaboratorService`: コラボレーター情報

---

### 2.5 AuditGroupService

**基本情報**

| 項目 | 値 |
|------|-----|
| ファイル | `service/AuditGroupService.java` |
| 行数 | 439 |
| メソッド数 | 8 |
| フィールド数 | 7 |
| **MMI** | **54** (低中成熟) |

**スコア詳細**

| 軸 | スコア | 根拠 |
|----|:-----:|------|
| Cohesion | 3 | グループ管理に集中しているが、メンバー管理も含む |
| Coupling | 2 | 6つのリポジトリへの依存 |
| Independence | 3 | 比較的独立したドメイン |
| Reusability | 3 | グループ概念は汎用的 |

---

### 2.6 AuditSetCollaboratorService

**基本情報**

| 項目 | 値 |
|------|-----|
| ファイル | `service/AuditSetCollaboratorService.java` |
| 行数 | 288 |
| メソッド数 | 7 |
| フィールド数 | 6 |
| **MMI** | **60** (中成熟) |

**スコア詳細**

| 軸 | スコア | 根拠 |
|----|:-----:|------|
| Cohesion | 3 | コラボレーター管理に集中 |
| Coupling | 3 | 5つのリポジトリだが一方向依存 |
| Independence | 3 | 監査セットとの関連のみ |
| Reusability | 3 | コラボレーションパターンは汎用的 |

---

### 2.7 EventLogService

**基本情報**

| 項目 | 値 |
|------|-----|
| ファイル | `service/EventLogService.java` |
| 行数 | 222 |
| メソッド数 | 6 |
| フィールド数 | 2 |
| **MMI** | **68** (中成熟) |

**スコア詳細**

| 軸 | スコア | 根拠 |
|----|:-----:|------|
| Cohesion | 4 | イベントログ取得に特化 |
| Coupling | 3 | 2つのリポジトリのみ |
| Independence | 3 | イベントデータへの読み取りのみ |
| Reusability | 4 | 検索・フィルタリングロジックは汎用的 |

**良い点**
- 単一責務に近い設計
- 少ない依存関係
- クエリメソッドが明確

---

### 2.8 AssetService

**基本情報**

| 項目 | 値 |
|------|-----|
| ファイル | `service/AssetService.java` |
| 行数 | 199 |
| メソッド数 | 5 |
| フィールド数 | 5 |
| **MMI** | **54** (低中成熟) |

**スコア詳細**

| 軸 | スコア | 根拠 |
|----|:-----:|------|
| Cohesion | 3 | ScalarDL連携に集中 |
| Coupling | 3 | ScalarDL、BoxUtilityへの限定的依存 |
| Independence | 2 | ScalarDL Auditor/Ledgerへの強依存 |
| Reusability | 3 | 台帳操作は他システムでも利用可能 |

**特記事項**
- ScalarDLコントラクト呼び出しのラッパー
- 改ざん検証のコア機能

---

### 2.9 FolderService

**基本情報**

| 項目 | 値 |
|------|-----|
| ファイル | `service/FolderService.java` |
| 行数 | 115 |
| メソッド数 | 2 |
| フィールド数 | 3 |
| **MMI** | **60** (中成熟) |

**スコア詳細**

| 軸 | スコア | 根拠 |
|----|:-----:|------|
| Cohesion | 3 | フォルダ操作に集中 |
| Coupling | 3 | UserService、BoxUtilityへの限定的依存 |
| Independence | 3 | BOX APIへの外部依存 |
| Reusability | 3 | フォルダ一覧取得は汎用的 |

---

### 2.10 CommonService

**基本情報**

| 項目 | 値 |
|------|-----|
| ファイル | `service/CommonService.java` |
| 行数 | 33 |
| メソッド数 | 3 |
| フィールド数 | 1 |
| **MMI** | **80** (高成熟) |

**スコア詳細**

| 軸 | スコア | 根拠 |
|----|:-----:|------|
| Cohesion | 4 | 権限チェックユーティリティに特化 |
| Coupling | 4 | ObjectMapperのみに依存 |
| Independence | 4 | 完全に独立した判定ロジック |
| Reusability | 4 | 他コンテキストでも利用可能 |

**良い点**
- 小さく明確な責務
- 最小限の依存
- 純粋関数的な設計

---

## 3. 依存関係マトリクス

### 3.1 サービス間依存

```
                  US  ASI ASS FS  AGS ASC ELS AstS FoS CS
UserService       -
AuditSetItemSvc   X   -   X                   X
AuditSetService   X       -
FileService       X           -
AuditGroupSvc     X               -
AuditSetCollab    X               X   -
EventLogService                           -
AssetService                                  -
FolderService     X                               -
CommonService                                         -

X = 依存あり
```

### 3.2 リポジトリ依存数

| サービス | リポジトリ依存数 | 詳細 |
|---------|----------------|------|
| UserService | 10 | userRepo, roleUserRepo, tokenRepo, otpRepo, orgRepo, auditSetRepo, collaboratorRepo, groupRepo, userGroupRepo, auditorLogsRepo |
| AuditSetService | 8 | auditSetRepo, collaboratorRepo, userRepo, auditorLogsRepo, groupRepo, userGroupRepo, mappingRepo, itemRepo |
| AuditSetItemService | 6 | auditSetRepo, userRepo, itemRepo, assetSvc, statusRepo, collaboratorRepo |
| FileService | 5 | userRepo, statusRepo, auditorLogsRepo, auditSetRepo, sha1Repo |
| AuditGroupService | 6 | groupRepo, auditSetRepo, mappingRepo, userRepo, userGroupRepo |
| AuditSetCollaboratorService | 4 | collaboratorRepo, auditSetRepo, userRepo |
| EventLogService | 2 | eventsRepo, itemEventsRepo |
| AssetService | 1 | scalardlRepo |
| FolderService | 0 | (UserService経由) |
| CommonService | 0 | なし |

---

## 4. 改善優先度マトリクス

```
         緊急度 高 ←───────────────→ 低
影響度   ┌─────────────────────────────────┐
  高     │ UserService     │ AuditSetSvc  │
         │ AuditSetItemSvc │ FileService  │
         ├─────────────────┼──────────────┤
  低     │ AuditGroupSvc   │ EventLogSvc  │
         │ AssetService    │ CommonService│
         └─────────────────┴──────────────┘
```

**優先順位:**
1. UserService - 最優先で分割
2. AuditSetItemService - 循環依存解消
3. AuditSetService - リポジトリ依存削減
4. FileService - BOX連携抽象化
5. AuditGroupService - 標準化
6. AssetService - インターフェース導入
7. 他 - 維持

---

*Generated: 2025-12-26*
*Source: scalar-event-log-fetcher-main*
