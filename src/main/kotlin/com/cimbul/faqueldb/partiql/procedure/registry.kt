package com.cimbul.faqueldb.partiql.procedure

import com.cimbul.faqueldb.data.StatementContext
import org.partiql.lang.eval.ExprValueFactory

fun createProcedures(context: StatementContext, valueFactory: ExprValueFactory) = listOf(
    CreateTable(context, valueFactory),
    DropTable(context, valueFactory),
    CreateIndex(context, valueFactory),
    DropIndex(context, valueFactory),
    Insert(context, valueFactory),
)
