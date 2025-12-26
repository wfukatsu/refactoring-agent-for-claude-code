# GraphDB可視化サマリー

**生成日時**: 2025-12-26 11:12:14

## 統計情報

| 項目 | 件数 |
|------|------|
| ドメイン | 7 |
| エンティティ | 60 |
| 用語 | 26 |
| リレーション | 39 |

## 生成ファイル

| ファイル | 形式 | 用途 |
|---------|------|------|
| graph.mmd | Mermaid | ドキュメント埋め込み |
| graph.dot | DOT | Graphviz変換 |
| graph.html | HTML | インタラクティブビュー |

## 使用方法

### Mermaid → PNG変換
```bash
mmdc -i graph.mmd -o graph.png
```

### DOT → PNG変換
```bash
dot -Tpng graph.dot -o graph-dot.png
```

### HTMLビュー
```bash
open graph.html  # macOS
xdg-open graph.html  # Linux
```
