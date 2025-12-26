# API エンドポイント一覧

## 1. 認証・ユーザー API

### UserController

| メソッド | エンドポイント | 説明 |
|---------|---------------|------|
| POST | /api/users/login | ログイン |
| POST | /api/users/register | ユーザー登録 |
| POST | /api/users/refresh-token | トークン更新 |
| GET | /api/users/managed | 管理対象ユーザー一覧 |
| DELETE | /api/users/{email} | ユーザー削除 |
| PUT | /api/users/role | ロール更新 |
| GET | /api/users/external-auditors | 外部監査人一覧 |
| POST | /api/users/forgot-password | パスワードリセット要求 |
| POST | /api/users/reset-password | パスワードリセット実行 |
| PUT | /api/users/edit | ユーザー編集 |
| PUT | /api/users/language | 言語設定更新 |

---

## 2. 監査セット API

### AuditSetController

| メソッド | エンドポイント | 説明 |
|---------|---------------|------|
| POST | /api/audit-sets | 監査セット作成 |
| DELETE | /api/audit-sets/{id} | 監査セット削除 |
| GET | /api/audit-sets/my | 自分の監査セット一覧 |
| GET | /api/audit-sets/{id}/ext-auditor-logs | 外部監査人ログ表示 |
| PUT | /api/audit-sets/{id}/info | 監査セット情報更新 |
| POST | /api/audit-sets/{id}/verify | 監査セット検証 |
| GET | /api/audit-sets/item/{itemId} | アイテムの監査セット一覧 |
| PUT | /api/audit-sets/item/{itemId} | アイテムの監査セット更新 |

### AuditSetItemController

| メソッド | エンドポイント | 説明 |
|---------|---------------|------|
| POST | /api/audit-set-items | アイテム追加 |
| DELETE | /api/audit-set-items/{auditSetId}/{itemId} | アイテム削除 |
| GET | /api/audit-set-items/{auditSetId} | アイテム一覧 |
| PUT | /api/audit-set-items/visibility | 表示設定更新 |

### AuditSetCollaboratorController

| メソッド | エンドポイント | 説明 |
|---------|---------------|------|
| POST | /api/audit-set-collaborators | コラボレーター追加 |
| DELETE | /api/audit-set-collaborators/{auditSetId}/{email} | コラボレーター削除 |
| GET | /api/audit-set-collaborators/{auditSetId} | コラボレーター一覧 |
| PUT | /api/audit-set-collaborators | コラボレーター更新 |

---

## 3. 監査グループ API

### AuditGroupController

| メソッド | エンドポイント | 説明 |
|---------|---------------|------|
| POST | /api/audit-groups | グループ作成 |
| DELETE | /api/audit-groups/{id} | グループ削除 |
| GET | /api/audit-groups | グループ一覧 |
| GET | /api/audit-groups/{id} | グループ詳細 |
| PUT | /api/audit-groups/{id} | グループ更新 |
| POST | /api/audit-groups/{id}/members | メンバー追加 |
| DELETE | /api/audit-groups/{id}/members/{email} | メンバー削除 |

---

## 4. イベントログ API

### EventLogController

| メソッド | エンドポイント | 説明 |
|---------|---------------|------|
| GET | /api/events | イベント一覧（日付範囲） |
| GET | /api/events/user/{userId} | ユーザー別イベント |
| GET | /api/events/type/{eventType} | タイプ別イベント |
| GET | /api/events/file/{fileId} | ファイル別イベント |
| GET | /api/events/user/{userId}/type/{eventType} | ユーザー＋タイプ別 |

**クエリパラメータ:**
- `startDate`: 開始日時
- `endDate`: 終了日時

---

## 5. ファイル・フォルダ API

### FileController

| メソッド | エンドポイント | 説明 |
|---------|---------------|------|
| GET | /api/files/{id} | ファイル詳細 |
| GET | /api/files/{id}/copies | ファイルコピー一覧 |
| GET | /api/files/{id}/versions | ファイルバージョン一覧 |
| GET | /api/files/{id}/collaborators | コラボレーター一覧 |
| POST | /api/files/{id}/verify | 改ざん検証 |
| GET | /api/files/{id}/download | ファイルダウンロード |
| GET | /api/files/{id}/preview | ファイルプレビュー |

### FolderController

| メソッド | エンドポイント | 説明 |
|---------|---------------|------|
| GET | /api/folders/{id} | フォルダ詳細 |
| GET | /api/folders/{id}/items | フォルダ内アイテム一覧 |
| GET | /api/folders/{id}/collaborators | コラボレーター一覧 |

### ItemController

| メソッド | エンドポイント | 説明 |
|---------|---------------|------|
| GET | /api/items/{id}/status | アイテムステータス |
| GET | /api/items/by-sha1/{sha1} | SHA1ハッシュ検索 |

---

## 6. 認証ヘッダー

すべてのAPI（ログイン以外）で以下のヘッダーが必要:

```
Authorization: Bearer <JWT_TOKEN>
```

---

## 7. 共通レスポンス形式

### 成功時
```json
{
  "status": "success",
  "data": { ... },
  "message": "Operation completed successfully"
}
```

### エラー時
```json
{
  "status": "error",
  "error": {
    "code": "ERROR_CODE",
    "message": "Error description"
  }
}
```
