# Scalar Auditor for BOX - プロジェクト概要

## 目的
BOXアプリケーションと連携し、ユーザーイベントログを外部に保存・管理するシステム。
ファイルの改ざん検出機能を備え、外部監査人が重要ファイルを監査可能にする。

## 技術スタック

### Backend
- **フレームワーク**: Spring Boot 3.2.1
- **Java Version**: 17
- **ビルドツール**: Gradle
- **認証**: Spring Security + JWT

### Frontend
- **フレームワーク**: React 18 + Vite
- **状態管理**: Redux Toolkit + Redux Persist
- **UI**: MUI (Material-UI) v5
- **スタイリング**: Tailwind CSS
- **i18n**: i18next
- **Box SDK**: box-ui-elements v19

### Database
- **Database**: Cassandra（ScalarDB経由）
- **Transaction管理**: ScalarDB Cluster 3.14.0
- **改ざん検出**: ScalarDL 3.10.0

## サブプロジェクト構成

| ディレクトリ | 説明 |
|------------|------|
| Scalar-Box-Event-Log-Tool | Spring Boot Backend |
| Scalar-Box-WebApp | React Frontend (メイン) |
| Scalar-WebApp-Integration-Menu | React Frontend (統合メニュー) |
| K8s-scalardb-cluster | Kubernetes設定 |
| Documentation | セットアップドキュメント |

## 主要機能
1. イベントログ保存・表示（日時、イベントタイプ、ユーザーでフィルタ）
2. ファイル詳細表示（メタデータ、バージョン、SHA1ハッシュ重複確認）
3. 組織ユーザーロール管理（Audit Admin / General User）
4. 外部監査人管理（CRUD）
5. Audit Set管理（ファイル/フォルダのグループ化）
6. ファイル改ざん検出（ScalarDL連携）
