package com.cimbul.faqueldb.partiql.function

import com.cimbul.faqueldb.data.StatementContext
import org.partiql.lang.eval.ExprValueFactory

fun createFunctions(context: StatementContext, valueFactory: ExprValueFactory) = listOf(
    Blocks(context, valueFactory)
)
