# 開発コマンド

## Backend (Scalar-Box-Event-Log-Tool)

```bash
# ビルド
./gradlew build

# テスト実行
./gradlew test

# アプリケーション起動
./gradlew bootRun

# スキーマロード
java -jar schema-loader/scalardb-schema-loader-3.14.0.jar \
  --config scalardb.properties \
  --schema-file schema-loader/scalar_box_schema.json \
  --coordinator
```

## Frontend (Scalar-Box-WebApp)

```bash
cd Scalar-Box-WebApp

# 依存関係インストール
npm install

# 開発サーバー起動 (port 3000)
npm run dev

# プロダクションビルド
npm run build

# Lint
npm run lint
```

## Frontend (Scalar-WebApp-Integration-Menu)

```bash
cd Scalar-WebApp-Integration-Menu

npm install
npm run dev
npm run build
npm run lint
```

## システム要件
- Java 17
- Node.js (npm)
- Cassandra (ScalarDB経由)
- ScalarDL Client SDK
