package com.cimbul.faqeldb.partiql

import com.amazon.ionelement.api.ionString
import org.partiql.lang.domains.PartiqlAst
import org.partiql.lang.domains.PartiqlAst.CaseSensitivity
import org.partiql.lang.domains.PartiqlAst.Expr
import org.partiql.lang.domains.PartiqlAst.FromSource
import org.partiql.lang.domains.PartiqlAst.JoinType
import org.partiql.lang.domains.id

class QueryTransformer : PartiqlAst.VisitorTransform() {
    override fun transformFromSourceScan(node: FromSource.Scan): FromSource {
        // We only care about BY clauses
        if (node.byAlias == null) {
            return node
        }

        // FROM table AS alias AT index BY id
        // FROM blocks(`table`) AS block, block.data AS alias AT index, block.metadata.id AS id

        val tableExpr = node.expr
        require(tableExpr is Expr.Id) { "FROM ... BY is only supported on tables" }
        val tableName = tableExpr.name
        val tableAlias = node.asAlias ?: tableName

        val blockAlias = internalName("blocks_$tableAlias")
        return PartiqlAst.build {
            join(
                JoinType.Inner(),
                join(
                    JoinType.Inner(),
                    scan(
                        call(internalName("blocks"), listOf(tableName.toLiteral())),
                        asAlias = blockAlias,
                    ),
                    scan_(
                        path(
                            id(blockAlias),
                            pathExpr(lit(ionString("data")), CaseSensitivity.CaseInsensitive()),
                        ),
                        asAlias = node.asAlias ?: tableName,
                        atAlias = node.atAlias,
                    ),
                ),
                scan_(
                    path(
                        id(blockAlias),
                        pathExpr(lit(ionString("metadata")), CaseSensitivity.CaseInsensitive()),
                        pathExpr(lit(ionString("id")), CaseSensitivity.CaseInsensitive()),
                    ),
                    asAlias = node.byAlias,
                )
            )
        }
    }
}
