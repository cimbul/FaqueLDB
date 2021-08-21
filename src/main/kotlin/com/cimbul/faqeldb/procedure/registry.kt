package com.cimbul.faqeldb.procedure

import org.partiql.lang.eval.ExprValueFactory
import org.partiql.lang.eval.builtins.storedprocedure.StoredProcedure

const val procedureNamePrefix = "_ql_faqe_"

fun createProcedures(valueFactory: ExprValueFactory): List<StoredProcedure> =
    listOf(
        CreateTable(valueFactory),
    )
