package com.cimbul.faqeldb.procedure

import com.cimbul.faqeldb.data.Database
import org.partiql.lang.eval.ExprValueFactory
import org.partiql.lang.eval.builtins.storedprocedure.StoredProcedure

const val procedureNamePrefix = "_ql_faqe_"

fun createProcedures(database: Database, valueFactory: ExprValueFactory): List<StoredProcedure> =
    listOf(
        CreateTable(database, valueFactory),
        DropTable(database, valueFactory),
    )
