package com.cimbul.faqeldb

import com.amazon.ionelement.api.loadSingleElement
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.partiql.lang.domains.PartiqlAst

class ParserTest : DescribeSpec({
    val parser = Parser()

    describe("parse") {
        it("should parse literals") {
            parser.parse("1 + 1") shouldBe PartiqlAst.build {
                query(
                    PartiqlAst.Expr.Plus(listOf(
                        PartiqlAst.Expr.Lit(loadSingleElement("1")),
                        PartiqlAst.Expr.Lit(loadSingleElement("1")),
                    ))
                )
            }
        }
    }
})
