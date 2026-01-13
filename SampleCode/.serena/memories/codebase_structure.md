# コードベース構造

## Backend (Scalar-Box-Event-Log-Tool)

### パッケージ構成
```
com.scalar.events_log_tool.application/
├── controller/     # REST API エンドポイント
├── service/        # ビジネスロジック
├── business/       # ビジネスレイヤー（Controller-Service間）
├── repository/     # データアクセス層（ScalarDB）
├── model/          # エンティティ
├── dto/            # Data Transfer Objects
├── responsedto/    # レスポンス用DTO
├── config/         # 設定クラス
├── security/       # JWT認証・Spring Security
├── constant/       # 定数・Enum
├── exception/      # カスタム例外
└── utility/        # ユーティリティクラス
```

### 主要コントローラー
- UserController: ユーザー管理
- AuditSetController: 監査セット管理
- AuditGroupController: 監査グループ管理
- EventLogController: イベントログ取得
- FileController: ファイル操作
- FolderController: フォルダ操作
- ItemController: アイテム共通操作

### 主要サービス
- UserService: ユーザー認証・管理
- AuditSetService: 監査セットCRUD
- AuditGroupService: 監査グループ管理
- EventLogService: イベントログ検索
- FileService: ファイル詳細・改ざん検出
- AssetService: ScalarDL連携

## Frontend (Scalar-Box-WebApp)

### ディレクトリ構成
```
src/
├── pages/              # ページコンポーネント
│   ├── auth/           # 認証ページ
│   ├── AuditSet/       # 監査セット管理
│   ├── AuditorsAndGroups/ # 監査人・グループ管理
│   ├── ViewAllEventHistory/ # イベント履歴
│   └── ...
├── api/                # API呼び出し
├── redux/              # Redux状態管理
├── hooks/              # カスタムフック
├── common/             # 共通コンポーネント
├── utils/              # ユーティリティ
├── i18n/               # 多言語対応
└── assets/             # 静的ファイル
```
