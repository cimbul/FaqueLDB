package com.cimbul.faqeldb

import com.cimbul.faqeldb.procedure.fullProcedureName
import org.partiql.lang.domains.PartiqlAst
import org.partiql.lang.domains.PartiqlAst.DdlOp
import org.partiql.lang.domains.PartiqlAst.Expr
import org.partiql.lang.domains.PartiqlAst.Statement
import org.partiql.pig.runtime.SymbolPrimitive
import org.partiql.pig.runtime.asPrimitive

class Transformer : PartiqlAst.VisitorTransform() {
    /**
     * Transform DDL statements into stored procedure calls, so they can be evaluated at the right
     * time.
     */
    override fun transformStatementDdl(node: Statement.Ddl): Statement {
        val (procedure, args) = when (val op = node.op) {
            is DdlOp.CreateTable ->
                // CREATE TABLE table
                // EXEC create_table `table`
                "create_table" to listOf(op.tableName.toExpr())
            is DdlOp.CreateIndex ->
                // CREATE INDEX ON table (field, ...)
                // EXEC create_index `table` [field, ...]
                "create_index" to listOf(op.indexName.name.toExpr(), Expr.List(op.fields))
            is DdlOp.DropTable ->
                // DROP TABLE table
                // EXEC drop_table `table`
                "drop_table" to listOf(op.tableName.name.toExpr())
            is DdlOp.DropIndex ->
                // DROP INDEX index ON table
                // EXEC drop_index `table` `index`
                "drop_index" to listOf(op.table.name.toExpr(), op.keys.name.toExpr())
        }
        return Statement.Exec(fullProcedureName(procedure).asPrimitive(), args)
    }

    private fun SymbolPrimitive.toExpr(): Expr {
        return Expr.Lit(this.toIonElement().asAnyElement())
    }
}
