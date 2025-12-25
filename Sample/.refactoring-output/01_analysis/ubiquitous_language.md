# ユビキタス言語集

## 概要

Scalar Auditor for BOXは、BOXクラウドストレージと連携した外部監査ツールです。ファイル操作のイベントログを収集・保存し、外部監査人へのセキュアな監査データ提供を実現します。

## 用語定義

### コアドメイン

#### ユーザー・認証関連

| 用語（日本語） | 用語（英語） | 定義 | コード上の表現 |
|--------------|------------|------|---------------|
| 組織ユーザー | Organization User | BOXアカウントを持つ組織内ユーザー | `User` クラス |
| 外部監査人 | External Auditor | 監査目的でシステムにアクセスする外部ユーザー | `UserRoles.EXTERNAL_AUDITOR` |
| 監査管理者 | Audit Admin | ユーザー管理・監査セット管理権限を持つ管理者 | `UserRoles.AUDIT_ADMIN` |
| 一般ユーザー | General User | 基本的なファイル操作・監査セット追加権限を持つユーザー | `UserRoles.GENERAL_USER` |
| ロール | Role | ユーザーに付与される権限の集合 | `roleJson` フィールド |
| 組織 | Organization | BOXエンタープライズに対応する組織単位 | `Organization` クラス |

#### 監査セット関連

| 用語（日本語） | 用語（英語） | 定義 | コード上の表現 |
|--------------|------------|------|---------------|
| 監査セット | Audit Set | 監査対象として選択されたファイル・フォルダの集合 | `AuditSet` クラス |
| 監査セットアイテム | Audit Set Item | 監査セットに含まれる個別のファイルまたはフォルダ | `AuditSetItem` クラス |
| コラボレーター | Collaborator | 監査セットにアクセス権を持つユーザー | `AuditSetCollaborators` クラス |
| オーナー | Owner | 監査セットの完全な管理権限を持つユーザー | `CollaboratorUserRoles.OWNER` |
| 共同オーナー | Co-Owner | オーナーに準ずる権限を持つユーザー | `CollaboratorUserRoles.CO_OWNER` |
| メンバー | Member | 読み取り・限定編集権限を持つユーザー | `CollaboratorUserRoles.MEMBER` |
| レビュアー | Reviewer | 読み取り専用権限を持つユーザー | `CollaboratorUserRoles.REVIEWER` |
| ACL | Access Control List | アクセス制御リスト | `aclJson` フィールド |

#### 監査グループ関連

| 用語（日本語） | 用語（英語） | 定義 | コード上の表現 |
|--------------|------------|------|---------------|
| 監査グループ | Audit Group | 外部監査人をグループ化した単位 | `AuditGroup` クラス |
| グループメンバー | Group Member | 監査グループに所属するユーザー | `UserAuditGroup` クラス |
| グループ権限 | Group Privileges | グループに付与される権限レベル | `AuditGroupPrivileges` |

#### イベント・ログ関連

| 用語（日本語） | 用語（英語） | 定義 | コード上の表現 |
|--------------|------------|------|---------------|
| イベント | Event | BOX上で発生したファイル操作の記録 | `Events` クラス |
| イベントログ | Event Log | イベントの履歴データ | `EnterpriseEventLogs` クラス |
| 監査ログ | Auditor Logs | 外部監査人の操作ログ | `AuditorLogs` クラス |
| イベントタイプ | Event Type | ファイル操作の種類 | `EventType` enum |

#### ファイル・アイテム関連

| 用語（日本語） | 用語（英語） | 定義 | コード上の表現 |
|--------------|------------|------|---------------|
| アイテム | Item | ファイルまたはフォルダの総称 | `Item` クラス |
| ファイルバージョン | File Version | ファイルの特定バージョン | `itemVersionId` フィールド |
| ファイルコピー | File Copy | 同一ハッシュを持つファイル | `ItemsBySha1` クラス |
| SHA1ハッシュ | SHA1 Hash | ファイル内容の一意識別子 | `sha1Hash` フィールド |
| アイテムイベント | Item Events | 特定アイテムに関連するイベント | `ItemEvents` クラス |
| アイテムステータス | Item Status | アイテムの現在の状態 | `ItemStatus` クラス |

#### 検証関連

| 用語（日本語） | 用語（英語） | 定義 | コード上の表現 |
|--------------|------------|------|---------------|
| 検証 | Validation | ファイルの改ざん検知処理 | ScalarDL連携 |
| 改ざんステータス | Tampering Status | ファイルの改ざん検知結果 | `TamperingStatusType` |
| アセット | Asset | ScalarDLで管理されるデータ単位 | `assetId` フィールド |

### イベントタイプ一覧

| イベントタイプ | 説明 |
|--------------|------|
| `ITEM_UPLOAD` | ファイルのアップロード |
| `ITEM_MOVE` | ファイル/フォルダの移動 |
| `ITEM_COPY` | ファイル/フォルダのコピー |
| `ITEM_TRASH` | ゴミ箱への移動 |
| `ITEM_UNDELETE_VIA_TRASH` | ゴミ箱からの復元 |
| `ITEM_RENAME` | 名前の変更 |
| `ITEM_MODIFY` | ファイル内容の変更 |

## 略語・頭字語

| 略語 | 正式名称 | 説明 |
|-----|---------|------|
| BOX | Box.com | クラウドストレージサービス |
| ACL | Access Control List | アクセス制御リスト |
| SHA1 | Secure Hash Algorithm 1 | ハッシュアルゴリズム |
| JWT | JSON Web Token | 認証トークン形式 |
| OTP | One-Time Password | ワンタイムパスワード |
| DL | ScalarDL | Scalar社の分散台帳技術 |
| DB | ScalarDB | Scalar社の分散データベース |

## 同義語・類義語マッピング

| 用語A | 用語B | 推奨用語 | 理由 |
|------|------|---------|------|
| File | Item | Item | フォルダも含む汎用的な表現 |
| External User | External Auditor | External Auditor | 役割を明確に示す |
| Audit Admin | Administrator | Audit Admin | 監査コンテキストを明確化 |
| Gen User | General User | General User | 略称より正式名称を使用 |
