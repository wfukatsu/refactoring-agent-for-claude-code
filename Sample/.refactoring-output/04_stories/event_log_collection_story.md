# ドメインストーリー: イベントログ収集

## 概要

**ドメイン**: Event Log + BOX Integration
**ストーリータイプ**: バックグラウンド処理
**アクター**: システム（EventFetchJob）

## ストーリー

### シナリオ: BOXイベントの定期収集と保存

システムが定期的にBOXからエンタープライズイベントを取得し、検索可能な形式で保存する。

---

## ドメインストーリーテリング図

```
┌─────────────────────────────────────────────────────────────────────────┐
│                     イベントログ収集ストーリー                            │
└─────────────────────────────────────────────────────────────────────────┘

    ┌──────────────┐
    │ Scheduler    │
    │ (Cron)       │
    └──────┬───────┘
           │
           │ ① 定期実行トリガー（5分間隔）
           ▼
    ┌──────────────┐
    │ Event Log    │
    │   Service    │
    │ (EventFetch │
    │    Job)      │
    └──────┬───────┘
           │
           │ ② 前回取得位置を確認
           ▼
    ┌──────────────┐
    │ Position     │
    │ Tracker DB   │
    │              │
    │ position:    │
    │ "123456789"  │
    └──────┬───────┘
           │
           │ ③ BOXイベント取得リクエスト
           ▼
    ┌──────────────┐         ┌─────────────┐
    │ BOX Integration│────────▶│  BOX API    │
    │   Service     │         │             │
    │              │◀────────│ /events     │
    └──────┬───────┘         └─────────────┘
           │
           │ ④ イベント一覧を返却（最大500件）
           ▼
    ┌──────────────────────────────────────────┐
    │              イベントデータ                 │
    │ ┌──────────────────────────────────────┐ │
    │ │ Event 1: ITEM_UPLOAD                 │ │
    │ │   user: user-123                     │ │
    │ │   item: file-456                     │ │
    │ │   timestamp: 2024-01-15T10:00:00Z    │ │
    │ ├──────────────────────────────────────┤ │
    │ │ Event 2: ITEM_MODIFY                 │ │
    │ │   user: user-789                     │ │
    │ │   item: file-012                     │ │
    │ │   timestamp: 2024-01-15T10:01:00Z    │ │
    │ ├──────────────────────────────────────┤ │
    │ │ ...                                  │ │
    │ └──────────────────────────────────────┘ │
    └──────────────┬───────────────────────────┘
                   │
                   │ ⑤ イベントをパース・変換
                   ▼
    ┌──────────────┐
    │ Event Log    │
    │   Service    │
    │ (EventParser│
    │  Service)    │
    └──────┬───────┘
           │
           │ ⑥ 重複チェック
           ▼
    ┌──────────────┐
    │ Events DB    │
    │              │
    │ Check:       │
    │ eventId      │
    │ exists?      │
    └──────┬───────┘
           │
           │ ⑦ 新規イベントを保存
           ▼
    ┌─────────────────────────────────────────────────────┐
    │                    Events Table                       │
    │ ┌─────────┬──────────┬─────────┬──────────┬───────┐ │
    │ │ yyyyMMdd│ eventId  │ userId  │ itemId   │ type  │ │
    │ ├─────────┼──────────┼─────────┼──────────┼───────┤ │
    │ │ 20240115│ evt-001  │ user-123│ file-456 │ UPLOAD│ │
    │ │ 20240115│ evt-002  │ user-789│ file-012 │ MODIFY│ │
    │ └─────────┴──────────┴─────────┴──────────┴───────┘ │
    └──────────────┬──────────────────────────────────────┘
                   │
                   │ ⑧ アイテム別インデックス更新
                   ▼
    ┌──────────────┐
    │ ItemEvents   │
    │   Table      │
    │              │
    │ itemId →     │
    │   eventIds   │
    └──────┬───────┘
           │
           │ ⑨ 取得位置を更新
           ▼
    ┌──────────────┐
    │ Position     │
    │ Tracker DB   │
    │              │
    │ position:    │
    │ "123457000"  │
    └──────────────┘
           │
           │ ⑩ 処理完了ログ出力
           ▼
    ┌──────────────────────────────────────────┐
    │ [INFO] EventFetchJob completed           │
    │   processed: 150 events                  │
    │   new: 148, duplicate: 2                 │
    │   duration: 3.2s                         │
    │   next_position: 123457000               │
    └──────────────────────────────────────────┘

```

---

## ステップ詳細

### ① 定期実行トリガー

**トリガー**: Cronスケジューラ
**間隔**: 5分

```
Scheduler が EventFetchJob を 5分間隔 で 起動する
```

### ② 前回取得位置を確認

**テーブル**: PositionTracker
**カラム**: position（BOX Stream Position）

```
EventFetchJob が PositionTracker から 前回の取得位置 を 取得する
```

### ③ BOXイベント取得リクエスト

**API**: `GET /events`
**パラメータ**:
- stream_position: 前回位置
- stream_type: admin_logs
- limit: 500

```
EventFetchJob が BOX Integration Service 経由で イベント を 要求する
```

### ④ イベント一覧を返却

**レスポンス**:
- chunk_size: 取得件数
- next_stream_position: 次回開始位置
- entries: イベント配列

```
BOX API が イベント一覧 と 次回位置 を 返却する
```

### ⑤ イベントをパース・変換

**変換処理**:
- BOXイベント形式 → 内部イベント形式
- タイムスタンプの正規化
- ユーザーID/アイテムIDの抽出

```
EventParserService が BOXイベント を 内部形式 に 変換する
```

### ⑥ 重複チェック

**チェック条件**:
- 同一eventIdが既に存在するか

```
EventLogService が eventId で 重複チェック を 実行する
```

### ⑦ 新規イベントを保存

**テーブル**: Events
**パーティションキー**: yyyyMMdd（日付）
**クラスタリングキー**: eventId

```
EventLogService が 新規イベント を Events テーブル に 保存する
```

### ⑧ アイテム別インデックス更新

**テーブル**: ItemEvents
**目的**: アイテムIDでのイベント検索を高速化

```
EventLogService が ItemEvents インデックス を 更新する
```

### ⑨ 取得位置を更新

**更新内容**:
- position: next_stream_position
- updated_at: 現在時刻

```
EventFetchJob が PositionTracker の 位置 を 更新する
```

### ⑩ 処理完了ログ出力

**ログ内容**:
- 処理件数
- 新規/重複の内訳
- 処理時間
- 次回位置

```
EventFetchJob が 処理完了 を ログ に 記録する
```

---

## データフロー

### イベントタイプマッピング

| BOXイベントタイプ | 内部タイプ | 説明 |
|-----------------|----------|------|
| ITEM_UPLOAD | UPLOAD | ファイルアップロード |
| ITEM_MODIFY | MODIFY | ファイル更新 |
| ITEM_DOWNLOAD | DOWNLOAD | ファイルダウンロード |
| ITEM_PREVIEW | PREVIEW | ファイルプレビュー |
| ITEM_MOVE | MOVE | ファイル移動 |
| ITEM_COPY | COPY | ファイルコピー |
| ITEM_RENAME | RENAME | ファイル名変更 |
| ITEM_TRASH | TRASH | ファイル削除 |
| ITEM_UNDELETE_VIA_TRASH | RESTORE | ファイル復元 |

### データモデル

```sql
-- Events テーブル
CREATE TABLE events (
    yyyymmdd TEXT,          -- パーティションキー
    event_id TEXT,          -- クラスタリングキー
    timestamp TIMESTAMP,
    user_id TEXT,
    user_name TEXT,
    item_id TEXT,
    item_name TEXT,
    item_type TEXT,
    event_type TEXT,
    additional_details TEXT,
    PRIMARY KEY ((yyyymmdd), event_id)
);

-- ItemEvents インデックス
CREATE TABLE item_events (
    item_id TEXT,           -- パーティションキー
    event_id TEXT,          -- クラスタリングキー
    yyyymmdd TEXT,
    PRIMARY KEY ((item_id), event_id)
);
```

---

## ビジネスルール

| ルール | 説明 |
|-------|------|
| BR-201 | 同一eventIdは一度だけ保存される |
| BR-202 | 過去90日分のイベントのみ保持（古いものは削除） |
| BR-203 | 取得失敗時は次回ジョブで再試行 |
| BR-204 | 1回の取得で最大500件まで |

---

## 例外フロー

### E1: BOX API レート制限

```
BOX API が 429 Too Many Requests を 返した場合:
  → 指定された retry-after 秒待機
  → 再試行（最大3回）
  → 失敗した場合、次回ジョブまで待機
```

### E2: 接続タイムアウト

```
BOX API が タイムアウト した場合:
  → 30秒後に再試行（最大3回）
  → 失敗した場合、アラート発報
```

### E3: パース エラー

```
イベントデータ が パースできない 場合:
  → エラーログ出力
  → 該当イベントをスキップ
  → 処理は継続
```

---

## 運用考慮事項

### モニタリング指標

| 指標 | 説明 | アラート閾値 |
|-----|------|------------|
| events_fetched_total | 取得イベント総数 | - |
| events_saved_total | 保存イベント総数 | - |
| fetch_duration_seconds | 取得処理時間 | > 60s |
| fetch_errors_total | 取得エラー数 | > 3/hour |
| position_lag_seconds | 位置遅延（最新からの差） | > 1800s |

### リカバリ手順

```
1. PositionTracker の position を過去の時点に戻す
2. EventFetchJob を手動実行
3. 重複チェックにより既存データは影響なし
```

### 容量見積もり

| 項目 | 値 |
|-----|-----|
| 平均イベントサイズ | 500 bytes |
| 日次イベント数（想定） | 10,000 |
| 日次データ量 | 5 MB |
| 年間データ量 | 1.8 GB |
| 保持期間（90日） | 450 MB |
