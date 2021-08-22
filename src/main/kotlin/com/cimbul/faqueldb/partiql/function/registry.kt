package com.cimbul.faqueldb.partiql.function

import com.cimbul.faqueldb.data.Database
import org.partiql.lang.eval.ExprValueFactory

fun createFunctions(database: Database, valueFactory: ExprValueFactory) = listOf(
    Blocks(database, valueFactory)
)
