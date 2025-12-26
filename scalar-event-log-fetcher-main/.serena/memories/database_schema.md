# データベーススキーマ

ScalarDB Cluster上のCassandraを使用。
スキーマ定義: `schema-loader/scalar_box_schema.json`

## テーブル一覧

### イベント関連
| テーブル | パーティションキー | 説明 |
|---------|-----------------|------|
| events | yyyy_mm_dd | BOXイベントログ（日付ベース） |
| item_events | item_id | アイテム別イベント |
| auditor_logs | audit_set_id | 外部監査人操作ログ |

### ユーザー関連
| テーブル | パーティションキー | 説明 |
|---------|-----------------|------|
| user | user_email | ユーザーマスタ |
| role_user | role_name | ロール別ユーザー |
| user_token | user_email | JWT/OAuth2トークン |
| user_otp | user_email | ワンタイムパスワード |
| organization | org_id | 組織マスタ |

### 監査関連
| テーブル | パーティションキー | 説明 |
|---------|-----------------|------|
| audit_set | audit_set_id | 監査セット |
| audit_set_collaborators | user_email | 監査セット協力者 |
| audit_group | audit_group_id | 監査グループ |
| user_audit_group | user_email | ユーザー別グループ |
| audit_grp_audit_set_mapping | audit_group_id | グループ-セット関連 |
| auditset_folder_file_mapping | audit_set_id | セット-ファイル関連 |

### ファイル関連
| テーブル | パーティションキー | 説明 |
|---------|-----------------|------|
| item_status | item_id | 改ざん検証ステータス |
| items_by_sha1 | sha1_hash | SHA1ハッシュ別ファイル |
| position_tracker | user_id | イベント取得位置 |
