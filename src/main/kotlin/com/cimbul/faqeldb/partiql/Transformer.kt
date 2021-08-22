package com.cimbul.faqeldb.partiql

import com.cimbul.faqeldb.partiql.procedure.fullProcedureName
import org.partiql.lang.domains.PartiqlAst
import org.partiql.lang.domains.PartiqlAst.DdlOp
import org.partiql.lang.domains.PartiqlAst.DmlOp
import org.partiql.lang.domains.PartiqlAst.Expr
import org.partiql.lang.domains.PartiqlAst.Statement
import org.partiql.pig.runtime.SymbolPrimitive
import org.partiql.pig.runtime.asPrimitive

/** Transform DML and DDL into stored procedure calls */
class Transformer : PartiqlAst.VisitorTransform() {
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

    override fun transformStatementDml(node: Statement.Dml): Statement {
        require(node.operations.ops.size == 1) { "Only a single operation is supported" }
        require(node.returning == null) { "RETURNING is not supported" }

        val op = node.operations.ops.single()
        val (procedure, args) = when (op) {
            is DmlOp.Insert -> {
                // INSERT INTO table values
                // EXEC insert `table` values
                require(node.from == null) { "FROM clause not supported on INSERT statements" }
                require(node.where == null) { "WHERE clause not supported on INSERT statements" }
                val target = op.target
                require(target is Expr.Id) {
                    "Expected table identifier as target for INSERT statement"
                }
                "insert" to listOf(target.name.toExpr(), op.values)
            }
            is DmlOp.InsertValue -> {
                // INSERT INTO table VALUE value
                // EXEC insert `table` << value >>
                require(node.from == null) { "FROM clause not supported on INSERT statements" }
                require(node.where == null) { "WHERE clause not supported on INSERT statements" }
                require(op.index == null) { "Inserting into a specific index is not supported" }
                require(op.onConflict == null) { "ON CONFLICT is not supported" }
                val target = op.target
                require(target is Expr.Id) {
                    "Expected table identifier as target for INSERT statement"
                }
                "insert" to listOf(target.name.toExpr(), Expr.Bag(listOf(op.value)))
            }
            is DmlOp.Delete -> TODO("DELETE is not supported")
            is DmlOp.Remove -> TODO("REMOVE is not supported")
            is DmlOp.Set -> TODO("UPDATE/SET is not supported")
        }
        return Statement.Exec(fullProcedureName(procedure).asPrimitive(), args)
    }

    private fun SymbolPrimitive.toExpr(): Expr {
        return Expr.Lit(this.toIonElement().asAnyElement())
    }
}