# ScalarDB スキーマ設計

## 1. スキーマ概要

### 1.1 テーブル一覧

| ネームスペース | テーブル名 | 説明 | サービス |
|--------------|-----------|------|---------|
| audit | audit_set | 監査セット | Audit Management |
| audit | audit_group | 監査グループ | Audit Management |
| audit | audit_set_collaborators | コラボレーター | Audit Management |
| audit | audit_set_item | 監査セットアイテム | Audit Management |
| audit | audit_grp_audit_set_mapping | グループ-セットマッピング | Audit Management |
| verification | item_status | アイテム検証ステータス | Verification |
| verification | items_by_sha1 | SHA1インデックス | Verification |
| event | events | BOXイベント | Event |
| event | item_events | アイテム別イベント | Event |
| event | auditor_logs | 監査人ログ | Event |
| event | position_tracker | イベント位置 | Event |
| item | item | アイテム情報 | Item |
| identity | user | ユーザー | Identity |
| identity | role_user | ロールユーザー | Identity |
| identity | user_token | トークン | Identity |
| identity | user_otp | OTP | Identity |
| identity | user_audit_group | ユーザー-グループ | Identity |
| identity | organization | 組織 | Identity |

---

## 2. テーブル定義

### 2.1 Audit ネームスペース

#### audit_set

```json
{
  "tables": [{
    "table": "audit.audit_set",
    "partition_key": ["audit_set_id"],
    "clustering_key": [],
    "columns": {
      "audit_set_id": "TEXT",
      "name": "TEXT",
      "description": "TEXT",
      "owner_user_id": "TEXT",
      "owner_email": "TEXT",
      "acl_json": "TEXT",
      "created_at": "BIGINT",
      "updated_at": "BIGINT"
    },
    "secondary_index": ["owner_user_id"]
  }]
}
```

#### audit_group

```json
{
  "tables": [{
    "table": "audit.audit_group",
    "partition_key": ["audit_group_id"],
    "clustering_key": [],
    "columns": {
      "audit_group_id": "TEXT",
      "name": "TEXT",
      "description": "TEXT",
      "owner_user_id": "TEXT",
      "created_at": "BIGINT",
      "updated_at": "BIGINT"
    }
  }]
}
```

#### audit_set_collaborators

```json
{
  "tables": [{
    "table": "audit.audit_set_collaborators",
    "partition_key": ["user_email"],
    "clustering_key": ["audit_set_id"],
    "columns": {
      "user_email": "TEXT",
      "audit_set_id": "TEXT",
      "user_id": "TEXT",
      "role": "TEXT",
      "added_at": "BIGINT"
    }
  }]
}
```

#### audit_set_item

```json
{
  "tables": [{
    "table": "audit.audit_set_item",
    "partition_key": ["audit_set_id"],
    "clustering_key": ["item_id"],
    "columns": {
      "audit_set_id": "TEXT",
      "item_id": "TEXT",
      "item_type": "TEXT",
      "item_name": "TEXT",
      "parent_id": "TEXT",
      "sha1_hash": "TEXT",
      "added_by": "TEXT",
      "added_at": "BIGINT"
    }
  }]
}
```

---

### 2.2 Verification ネームスペース

#### item_status

```json
{
  "tables": [{
    "table": "verification.item_status",
    "partition_key": ["item_id"],
    "clustering_key": [],
    "columns": {
      "item_id": "TEXT",
      "tampering_status": "TEXT",
      "last_verified_at": "BIGINT",
      "last_sha1_hash": "TEXT",
      "version_id": "TEXT",
      "registered_at": "BIGINT"
    }
  }]
}
```

#### items_by_sha1

```json
{
  "tables": [{
    "table": "verification.items_by_sha1",
    "partition_key": ["sha1_hash"],
    "clustering_key": ["item_id"],
    "columns": {
      "sha1_hash": "TEXT",
      "item_id": "TEXT",
      "item_name": "TEXT",
      "version_id": "TEXT",
      "created_at": "BIGINT"
    }
  }]
}
```

---

### 2.3 Event ネームスペース

#### events

```json
{
  "tables": [{
    "table": "event.events",
    "partition_key": ["yyyy_mm_dd"],
    "clustering_key": ["event_id"],
    "clustering_order": {"event_id": "DESC"},
    "columns": {
      "yyyy_mm_dd": "TEXT",
      "event_id": "TEXT",
      "event_type": "TEXT",
      "item_id": "TEXT",
      "item_type": "TEXT",
      "item_name": "TEXT",
      "user_id": "TEXT",
      "user_email": "TEXT",
      "source_id": "TEXT",
      "additional_details": "TEXT",
      "created_at": "BIGINT"
    }
  }]
}
```

#### item_events

```json
{
  "tables": [{
    "table": "event.item_events",
    "partition_key": ["item_id"],
    "clustering_key": ["event_id"],
    "clustering_order": {"event_id": "DESC"},
    "columns": {
      "item_id": "TEXT",
      "event_id": "TEXT",
      "event_type": "TEXT",
      "user_id": "TEXT",
      "user_email": "TEXT",
      "created_at": "BIGINT"
    }
  }]
}
```

#### auditor_logs

```json
{
  "tables": [{
    "table": "event.auditor_logs",
    "partition_key": ["audit_set_id"],
    "clustering_key": ["log_id"],
    "clustering_order": {"log_id": "DESC"},
    "columns": {
      "audit_set_id": "TEXT",
      "log_id": "TEXT",
      "action_type": "TEXT",
      "item_id": "TEXT",
      "user_id": "TEXT",
      "user_email": "TEXT",
      "created_at": "BIGINT"
    }
  }]
}
```

#### position_tracker

```json
{
  "tables": [{
    "table": "event.position_tracker",
    "partition_key": ["user_id"],
    "clustering_key": [],
    "columns": {
      "user_id": "TEXT",
      "stream_position": "TEXT",
      "last_updated_at": "BIGINT"
    }
  }]
}
```

---

### 2.4 Item ネームスペース

#### item

```json
{
  "tables": [{
    "table": "item.item",
    "partition_key": ["item_id"],
    "clustering_key": [],
    "columns": {
      "item_id": "TEXT",
      "item_type": "TEXT",
      "name": "TEXT",
      "parent_id": "TEXT",
      "sha1_hash": "TEXT",
      "size": "BIGINT",
      "owner_id": "TEXT",
      "owner_email": "TEXT",
      "created_at": "BIGINT",
      "modified_at": "BIGINT",
      "box_version_id": "TEXT"
    }
  }]
}
```

---

### 2.5 Identity ネームスペース

#### user

```json
{
  "tables": [{
    "table": "identity.user",
    "partition_key": ["user_email"],
    "clustering_key": [],
    "columns": {
      "user_email": "TEXT",
      "user_id": "TEXT",
      "name": "TEXT",
      "password_hash": "TEXT",
      "user_type": "TEXT",
      "status": "TEXT",
      "language": "TEXT",
      "org_id": "TEXT",
      "box_user_id": "TEXT",
      "created_at": "BIGINT",
      "updated_at": "BIGINT"
    }
  }]
}
```

#### role_user

```json
{
  "tables": [{
    "table": "identity.role_user",
    "partition_key": ["role_name"],
    "clustering_key": ["user_email"],
    "columns": {
      "role_name": "TEXT",
      "user_email": "TEXT",
      "user_id": "TEXT",
      "assigned_at": "BIGINT"
    }
  }]
}
```

#### user_token

```json
{
  "tables": [{
    "table": "identity.user_token",
    "partition_key": ["user_email"],
    "clustering_key": [],
    "columns": {
      "user_email": "TEXT",
      "access_token": "TEXT",
      "refresh_token": "TEXT",
      "token_type": "TEXT",
      "expires_at": "BIGINT",
      "created_at": "BIGINT"
    }
  }]
}
```

---

## 3. パーティション設計

### 3.1 パーティションキー選定基準

| テーブル | パーティションキー | 理由 |
|---------|-----------------|------|
| events | yyyy_mm_dd | 日付でデータ分散、範囲クエリ効率化 |
| audit_set | audit_set_id | UUID で均等分散 |
| audit_set_collaborators | user_email | ユーザー単位アクセス最適化 |
| item_events | item_id | アイテム単位クエリ最適化 |

### 3.2 ホットスポット対策

**問題**: `events`テーブルで当日パーティションに負荷集中

**対策**:
```
パーティションキー: yyyy_mm_dd + shard_id (0-9)
例: 2024-01-15-3
```

```json
{
  "table": "event.events_sharded",
  "partition_key": ["partition_key"],
  "clustering_key": ["event_id"],
  "columns": {
    "partition_key": "TEXT",  // "2024-01-15-3"
    "event_id": "TEXT",
    ...
  }
}
```

---

## 4. インデックス設計

### 4.1 セカンダリインデックス

| テーブル | インデックス | 用途 |
|---------|------------|------|
| audit_set | owner_user_id | 所有者でフィルタ |
| user | org_id | 組織でフィルタ |
| item | parent_id | 親フォルダでフィルタ |

### 4.2 マテリアライズドビュー代替

ScalarDBではマテリアライズドビューが使えないため、
逆引き用テーブルを用意:

```json
// 監査セット → コラボレーター (順方向)
{
  "table": "audit.audit_set_collaborators",
  "partition_key": ["user_email"],
  "clustering_key": ["audit_set_id"]
}

// 監査セット ← コラボレーター (逆方向)
{
  "table": "audit.collaborators_by_audit_set",
  "partition_key": ["audit_set_id"],
  "clustering_key": ["user_email"]
}
```

---

## 5. データ移行

### 5.1 現行スキーマからの変更点

| 変更タイプ | 内容 |
|-----------|------|
| ネームスペース分割 | 単一namespace → サービス別 |
| テーブル名変更 | snake_case統一 |
| カラム型統一 | TIMESTAMP → BIGINT |
| 逆引きテーブル追加 | アクセスパターン最適化 |

### 5.2 移行手順

1. 新スキーマ作成
2. データ二重書き込み開始
3. 既存データ移行（バッチ処理）
4. データ整合性検証
5. 読み取り先切り替え
6. 二重書き込み停止
7. 旧テーブル削除

---

## 6. スキーマ管理

### 6.1 Schema Loader使用

```bash
# スキーマ作成
java -jar scalardb-schema-loader-3.14.0.jar \
  --config scalardb.properties \
  --schema-file schema.json \
  --coordinator

# スキーマ削除
java -jar scalardb-schema-loader-3.14.0.jar \
  --config scalardb.properties \
  --schema-file schema.json \
  --delete-all
```

### 6.2 統合スキーマファイル

```json
{
  "scalar_auditor.audit.audit_set": {
    "partition-key": ["audit_set_id"],
    "clustering-key": [],
    "columns": {
      "audit_set_id": "TEXT",
      "name": "TEXT",
      ...
    }
  },
  "scalar_auditor.verification.item_status": {
    ...
  }
}
```
