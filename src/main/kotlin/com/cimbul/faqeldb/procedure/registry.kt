package com.cimbul.faqeldb.procedure

import com.cimbul.faqeldb.data.Database
import org.partiql.lang.eval.ExprValueFactory
import org.partiql.lang.eval.builtins.storedprocedure.StoredProcedure

fun fullProcedureName(name: String) = "_ql_faque_$name"

fun createProcedures(database: Database, valueFactory: ExprValueFactory): List<StoredProcedure> =
    listOf(
        CreateTable(database, valueFactory),
        DropTable(database, valueFactory),
    )
