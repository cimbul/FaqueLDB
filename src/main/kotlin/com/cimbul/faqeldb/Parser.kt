package com.cimbul.faqeldb

import com.amazon.ion.system.IonSystemBuilder
import org.partiql.lang.domains.PartiqlAst
import org.partiql.lang.syntax.SqlParser

class Parser {
    private val ion = IonSystemBuilder.standard().build()
    private val parser = SqlParser(ion)

    fun parse(query: String): PartiqlAst.Statement {
        return parser.parseAstStatement(query)
    }
}
