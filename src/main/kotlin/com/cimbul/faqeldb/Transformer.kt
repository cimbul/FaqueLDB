package com.cimbul.faqeldb

import org.partiql.lang.domains.PartiqlAst
import org.partiql.pig.runtime.SymbolPrimitive
import org.partiql.pig.runtime.asPrimitive

class Transformer : PartiqlAst.VisitorTransform() {
    /**
     * Transform DDL statements into stored procedure calls, so they can be evaluated at the right
     * time.
     */
    override fun transformStatementDdl(node: PartiqlAst.Statement.Ddl): PartiqlAst.Statement {
        val (procedureSuffix, args) = when (val op = node.op) {
            is PartiqlAst.DdlOp.CreateTable ->
                Pair("create_table", listOf(op.tableName.toExpr()))
            is PartiqlAst.DdlOp.CreateIndex ->
                Pair("create_index", listOf(op.indexName.name.toExpr(), PartiqlAst.Expr.List(op.fields)))
            is PartiqlAst.DdlOp.DropTable ->
                Pair("drop_table", listOf(op.tableName.name.toExpr()))
            is PartiqlAst.DdlOp.DropIndex ->
                Pair("drop_index", listOf(op.table.name.toExpr(), op.keys.name.toExpr()))
        }
        val procedure = procedurePrefix + procedureSuffix
        return PartiqlAst.Statement.Exec(procedure.asPrimitive(), args)
    }

    private fun SymbolPrimitive.toExpr(): PartiqlAst.Expr {
        return PartiqlAst.Expr.Lit(this.toIonElement().asAnyElement())
    }

    companion object {
        private const val procedurePrefix = "_ql_faqe_"
    }
}
