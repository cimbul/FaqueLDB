package com.cimbul.faqeldb.partiql.function

import com.cimbul.faqeldb.data.Database
import org.partiql.lang.eval.ExprValueFactory

fun createFunctions(database: Database, valueFactory: ExprValueFactory) = listOf(
    Blocks(database, valueFactory)
)
