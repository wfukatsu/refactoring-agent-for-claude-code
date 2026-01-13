# API設計概要

## 1. API設計方針

### 1.1 設計原則

| 原則 | 説明 |
|------|------|
| REST準拠 | RESTful API設計原則に従う |
| API First | OpenAPI仕様を先行して設計 |
| バージョニング | URLパスでバージョン管理（/api/v1/） |
| 一貫性 | 命名規則、エラー形式の統一 |
| セキュリティ | OAuth 2.0 + JWT認証 |

### 1.2 APIタイプ別採用基準

| タイプ | 用途 | 採用サービス |
|--------|------|-------------|
| REST | 外部公開API、CRUD操作 | 全サービス |
| gRPC | サービス間高速通信 | Verification ↔ ScalarDL |
| AsyncAPI | イベント駆動通信 | Event Service |
| GraphQL | 将来検討（複雑なクエリ） | - |

---

## 2. REST API設計規約

### 2.1 URL設計

```
https://api.scalar-auditor.example.com/api/v1/{resource}

例:
GET  /api/v1/audit-sets
POST /api/v1/audit-sets
GET  /api/v1/audit-sets/{id}
PUT  /api/v1/audit-sets/{id}
DELETE /api/v1/audit-sets/{id}
GET  /api/v1/audit-sets/{id}/items
POST /api/v1/audit-sets/{id}/items
```

### 2.2 HTTPメソッド

| メソッド | 用途 | 冪等性 |
|---------|------|--------|
| GET | リソース取得 | ✓ |
| POST | リソース作成 | ✗ |
| PUT | リソース全体更新 | ✓ |
| PATCH | リソース部分更新 | ✗ |
| DELETE | リソース削除 | ✓ |

### 2.3 HTTPステータスコード

| コード | 用途 |
|--------|------|
| 200 | 成功（データあり） |
| 201 | 作成成功 |
| 204 | 成功（データなし） |
| 400 | リクエスト不正 |
| 401 | 認証エラー |
| 403 | 認可エラー |
| 404 | リソース未発見 |
| 409 | コンフリクト |
| 422 | バリデーションエラー |
| 500 | サーバーエラー |
| 503 | サービス利用不可 |

### 2.4 レスポンス形式

**成功レスポンス**:
```json
{
  "data": {
    "id": "audit-set-123",
    "name": "2024 Financial Audit",
    "description": "Annual financial document audit",
    "owner": {
      "id": "user-456",
      "email": "admin@example.com"
    },
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "meta": {
    "requestId": "req-abc-123"
  }
}
```

**一覧レスポンス**:
```json
{
  "data": [
    { "id": "audit-set-1", "name": "Audit Set 1" },
    { "id": "audit-set-2", "name": "Audit Set 2" }
  ],
  "meta": {
    "requestId": "req-abc-123",
    "pagination": {
      "page": 1,
      "limit": 20,
      "total": 45,
      "totalPages": 3
    }
  }
}
```

**エラーレスポンス**:
```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Request validation failed",
    "details": [
      {
        "field": "name",
        "message": "Name is required"
      }
    ]
  },
  "meta": {
    "requestId": "req-abc-123"
  }
}
```

---

## 3. サービス別API概要

### 3.1 Audit Management Service API

| エンドポイント | メソッド | 説明 |
|--------------|---------|------|
| /api/v1/audit-sets | GET | 監査セット一覧 |
| /api/v1/audit-sets | POST | 監査セット作成 |
| /api/v1/audit-sets/{id} | GET | 監査セット詳細 |
| /api/v1/audit-sets/{id} | PUT | 監査セット更新 |
| /api/v1/audit-sets/{id} | DELETE | 監査セット削除 |
| /api/v1/audit-sets/{id}/items | GET | アイテム一覧 |
| /api/v1/audit-sets/{id}/items | POST | アイテム追加 |
| /api/v1/audit-sets/{id}/items/{itemId} | DELETE | アイテム削除 |
| /api/v1/audit-sets/{id}/collaborators | GET | コラボレーター一覧 |
| /api/v1/audit-sets/{id}/collaborators | POST | コラボレーター追加 |
| /api/v1/audit-groups | GET | 監査グループ一覧 |
| /api/v1/audit-groups | POST | 監査グループ作成 |
| /api/v1/audit-groups/{id} | PUT | 監査グループ更新 |
| /api/v1/audit-groups/{id} | DELETE | 監査グループ削除 |

### 3.2 Verification Service API

| エンドポイント | メソッド | 説明 |
|--------------|---------|------|
| /api/v1/items/{id}/validate | POST | アイテム検証実行 |
| /api/v1/audit-sets/{id}/validate | POST | 監査セット全体検証 |
| /api/v1/items/{id}/status | GET | 改ざんステータス取得 |
| /api/v1/items/{id}/register | POST | 監視対象として登録 |

### 3.3 Event Service API

| エンドポイント | メソッド | 説明 |
|--------------|---------|------|
| /api/v1/events | GET | イベント一覧（フィルタ付き） |
| /api/v1/items/{id}/events | GET | アイテム別イベント |
| /api/v1/audit-sets/{id}/events | GET | 監査セット別イベント |
| /api/v1/auditor-logs | GET | 監査人ログ |

### 3.4 Item Service API

| エンドポイント | メソッド | 説明 |
|--------------|---------|------|
| /api/v1/items/{id} | GET | アイテム詳細 |
| /api/v1/files/{id}/versions | GET | ファイルバージョン一覧 |
| /api/v1/files/by-sha1/{sha1} | GET | SHA1ハッシュでコピー検索 |
| /api/v1/folders/{id}/children | GET | フォルダ内容 |

### 3.5 Identity Service API

| エンドポイント | メソッド | 説明 |
|--------------|---------|------|
| /api/v1/auth/login | POST | ログイン |
| /api/v1/auth/logout | POST | ログアウト |
| /api/v1/auth/refresh | POST | トークン更新 |
| /api/v1/auth/box/callback | GET | BOX OAuthコールバック |
| /api/v1/users | GET | ユーザー一覧 |
| /api/v1/users | POST | ユーザー作成 |
| /api/v1/users/{id} | GET | ユーザー詳細 |
| /api/v1/users/{id} | PUT | ユーザー更新 |
| /api/v1/users/{id}/roles | PUT | ロール更新 |
| /api/v1/users/password/reset | POST | パスワードリセット |
| /api/v1/users/password/otp | POST | OTP送信 |

---

## 4. 共通機能

### 4.1 ページネーション

```
GET /api/v1/audit-sets?page=2&limit=20&sort=createdAt:desc
```

クエリパラメータ:
| パラメータ | 説明 | デフォルト |
|-----------|------|-----------|
| page | ページ番号（1始まり） | 1 |
| limit | 1ページあたり件数 | 20 |
| sort | ソート順（field:asc/desc） | createdAt:desc |

### 4.2 フィルタリング

```
GET /api/v1/events?startDate=2024-01-01&endDate=2024-01-31&eventType=ITEM_UPLOAD
```

### 4.3 部分レスポンス

```
GET /api/v1/audit-sets/{id}?fields=id,name,owner
```

### 4.4 リソース展開

```
GET /api/v1/audit-sets/{id}?expand=owner,items
```

---

## 5. API仕様書

詳細なOpenAPI仕様書は以下に格納:

- `api-specifications/audit-management-api.yaml`
- `api-specifications/verification-api.yaml`
- `api-specifications/event-api.yaml`
- `api-specifications/item-api.yaml`
- `api-specifications/identity-api.yaml`

---

## 6. 次のステップ

1. **API Gateway設計** - ルーティング、レート制限、CORS設定
2. **API Security設計** - OAuth 2.0、JWT、RBAC
3. **OpenAPI仕様書** - 各サービスの詳細API定義
