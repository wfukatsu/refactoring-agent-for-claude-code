# ドメインエンティティ一覧

## 1. ユーザードメイン

### User
ユーザーマスタ情報

| フィールド | 型 | 説明 |
|-----------|-----|------|
| user_email | TEXT | メールアドレス（PK） |
| id | BIGINT | ユーザーID |
| name | TEXT | ユーザー名 |
| password | TEXT | パスワード（ハッシュ） |
| role_json | TEXT | ロール情報（JSON） |
| org_id | TEXT | 組織ID |
| organization_name | TEXT | 組織名 |
| image_url | TEXT | プロフィール画像URL |
| is_deleted | BOOLEAN | 削除フラグ |
| is_box_admin | BOOLEAN | Box管理者フラグ |
| language_code | TEXT | 言語コード |

### RoleUser
ロール別ユーザー

| フィールド | 型 | 説明 |
|-----------|-----|------|
| role_name | TEXT | ロール名（PK） |
| user_id | BIGINT | ユーザーID |
| user_name | TEXT | ユーザー名 |
| user_email | TEXT | メールアドレス |

### Organization
組織マスタ

| フィールド | 型 | 説明 |
|-----------|-----|------|
| org_id | TEXT | 組織ID（PK） |
| organization_name | TEXT | 組織名 |

---

## 2. 監査ドメイン

### AuditSet
監査セット

| フィールド | 型 | 説明 |
|-----------|-----|------|
| audit_set_id | TEXT | 監査セットID（PK） |
| audit_set_name | TEXT | 監査セット名 |
| description | TEXT | 説明 |
| owner_id | BIGINT | オーナーID |
| owner_name | TEXT | オーナー名 |
| owner_email | TEXT | オーナーメール |
| acl_json | TEXT | アクセス制御リスト（JSON） |
| is_deleted | BOOLEAN | 削除フラグ |
| createdAt | BIGINT | 作成日時 |
| audit_group_list_json | TEXT | グループリスト（JSON） |

### AuditSetCollaborators
監査セットコラボレーター

| フィールド | 型 | 説明 |
|-----------|-----|------|
| user_email | TEXT | メールアドレス（PK） |
| audit_set_id | TEXT | 監査セットID |
| audit_set_name | TEXT | 監査セット名 |
| user_name | TEXT | ユーザー名 |
| audit_set_role | TEXT | ロール |
| access_status | TEXT | アクセス状態 |
| is_favourite | BOOLEAN | お気に入りフラグ |

### AuditGroup
監査グループ

| フィールド | 型 | 説明 |
|-----------|-----|------|
| audit_group_id | TEXT | グループID（PK） |
| audit_group_name | TEXT | グループ名 |
| description | TEXT | 説明 |
| owner_id | BIGINT | オーナーID |
| owner_name | TEXT | オーナー名 |
| user_email | TEXT | オーナーメール |
| member_list_json | TEXT | メンバーリスト（JSON） |
| created_at | BIGINT | 作成日時 |
| is_deleted | BOOLEAN | 削除フラグ |

### AuditSetItem (auditset_folder_file_mapping)
監査セットアイテム

| フィールド | 型 | 説明 |
|-----------|-----|------|
| audit_set_id | TEXT | 監査セットID（PK） |
| item_id | BIGINT | アイテムID |
| item_name | TEXT | アイテム名 |
| item_type | TEXT | タイプ（file/folder） |
| access_list_type | TEXT | アクセスリストタイプ |
| list_json | TEXT | リスト情報（JSON） |
| created_at | BIGINT | 作成日時 |
| assigned_by_user_id | BIGINT | 割り当てユーザーID |

---

## 3. イベントドメイン

### Events
BOXイベントログ

| フィールド | 型 | 説明 |
|-----------|-----|------|
| yyyy_mm_dd | TEXT | 日付（PK） |
| timestamp | TEXT | タイムスタンプ |
| user_id | BIGINT | ユーザーID |
| user_name | TEXT | ユーザー名 |
| user_email | TEXT | メールアドレス |
| asset_id | TEXT | アセットID |
| asset_age | INT | アセット年齢 |
| item_id | BIGINT | アイテムID |
| item_version_id | BIGINT | バージョンID |
| sha1_hash | TEXT | SHA1ハッシュ |
| event_id | TEXT | イベントID |
| event_type | TEXT | イベントタイプ |
| event_created_at | BIGINT | イベント作成日時 |
| event_occured_on | TEXT | イベント発生日 |
| parent_folder_id | BIGINT | 親フォルダID |
| source_json | TEXT | ソースJSON |

### ItemEvents
アイテム別イベント

| フィールド | 型 | 説明 |
|-----------|-----|------|
| item_id | BIGINT | アイテムID（PK） |
| item_version_id | BIGINT | バージョンID |
| event_id | TEXT | イベントID |
| event_type | TEXT | イベントタイプ |
| event_date | BIGINT | イベント日時 |
| event_json_data | TEXT | イベントJSON |

### AuditorLogs
外部監査人操作ログ

| フィールド | 型 | 説明 |
|-----------|-----|------|
| audit_set_id | TEXT | 監査セットID（PK） |
| item_id | BIGINT | アイテムID |
| user_email | TEXT | メールアドレス |
| event_type | TEXT | イベントタイプ |
| event_date | BIGINT | イベント日時 |
| item_type | TEXT | アイテムタイプ |
| custom_json_eventDetails | TEXT | カスタムイベント詳細 |

---

## 4. ファイルドメイン

### ItemStatus
アイテム検証ステータス

| フィールド | 型 | 説明 |
|-----------|-----|------|
| item_id | BIGINT | アイテムID（PK） |
| item_type | TEXT | アイテムタイプ |
| status | TEXT | 改ざん検証ステータス |
| last_validated_at | TEXT | 最終検証日時 |
| monitored_status | TEXT | 監視ステータス |
| list_of_auditset_json | TEXT | 監査セットリスト |

### ItemsBySha1
SHA1ハッシュ別ファイル

| フィールド | 型 | 説明 |
|-----------|-----|------|
| sha1_hash | TEXT | SHA1ハッシュ（PK） |
| item_id | BIGINT | アイテムID |
| item_version_id | BIGINT | バージョンID |
| item_version_number | INT | バージョン番号 |
| item_name | TEXT | アイテム名 |
| owner_by_json | TEXT | オーナー情報 |
| path | TEXT | パス |
| created_at | BIGINT | 作成日時 |

---

## 5. 定数・Enum

### UserRoles
```java
- AUDIT_ADMIN
- GENERAL_USER
- EXTERNAL_AUDITOR
```

### EventType
```java
- UPLOAD
- DOWNLOAD
- PREVIEW
- DELETE
- MOVE
- COPY
- RENAME
- ... (Box Event Types)
```

### TamperingStatusType
```java
- VALID
- TAMPERED
- NOT_VERIFIED
```

### ItemType
```java
- FILE
- FOLDER
```

### CollaboratorUserRoles
```java
- OWNER
- REVIEWER
- VIEWER
```
