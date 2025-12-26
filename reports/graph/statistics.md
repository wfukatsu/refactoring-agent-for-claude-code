# GraphDB統計情報

## 生成日時
2025-12-26 11:05:33

## ノード統計

| ノードタイプ | 件数 | 説明 |
|------------|------|------|
| UbiquitousTerm | 26 | ユビキタス言語の用語 |
| Domain | 7 | ビジネスドメイン |
| Entity | 60 | クラス/インターフェース |
| Actor | 6 | システムアクター |
| Role | 7 | ロール定義 |
| **合計** | **106** | |

## リレーション統計

| リレーションタイプ | 件数 | 説明 |
|------------------|------|------|
| BELONGS_TO | 60 | ドメイン所属 |
| REFERENCES | 39 | エンティティ参照 |
| HAS_TERM | 23 | 用語関連 |
| HAS_ROLE | 8 | ロール保持 |
| **合計** | **130** | |

## ドメイン別エンティティ数

| ドメイン | エンティティ数 |
|---------|--------------|
| Identity | 12 |
| Audit | 15 |
| AuditGroup | 8 |
| File | 13 |
| Event | 11 |
| Verification | 2 |
| BOXIntegration | - |

## データソース

- 分析結果: あり (`reports/01_analysis/`)
- コード解析: あり
- 対象ファイル数: 60+ クラス/インターフェース

## ファイル構成

```
reports/graph/
├── data/
│   ├── terms.csv          (26件)
│   ├── domains.csv         (7件)
│   ├── entities.csv       (60件)
│   ├── actors.csv          (6件)
│   ├── roles.csv           (7件)
│   ├── belongs_to.csv     (60件)
│   ├── has_term.csv       (23件)
│   ├── has_role.csv        (8件)
│   └── references.csv     (39件)
├── schema.md
└── statistics.md

knowledge.ryugraph/        # RyuGraphデータベース
```

## 利用方法

### クエリ実行

```bash
# インタラクティブモード
python scripts/query_graph.py --db-path ./knowledge.ryugraph --interactive

# 自然言語クエリ
python scripts/query_graph.py --db-path ./knowledge.ryugraph \
  --query "監査セットに関連するクラスを教えて"

# Cypherクエリ
python scripts/query_graph.py --db-path ./knowledge.ryugraph \
  --cypher "MATCH (e:Entity)-[:BELONGS_TO]->(d:Domain) RETURN e.name, d.name LIMIT 10"
```

---

*Generated: 2025-12-26*
*Source: Scalar Auditor for BOX*
