package com.cimbul.faqeldb.partiql.procedure

import com.cimbul.faqeldb.data.Database
import org.partiql.lang.eval.ExprValueFactory
import org.partiql.lang.eval.builtins.storedprocedure.StoredProcedure

fun createProcedures(database: Database, valueFactory: ExprValueFactory): List<StoredProcedure> =
    listOf(
        CreateTable(database, valueFactory),
        DropTable(database, valueFactory),
        Insert(database, valueFactory),
    )
