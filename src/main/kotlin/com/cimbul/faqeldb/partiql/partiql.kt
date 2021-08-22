package com.cimbul.faqeldb.partiql

import com.amazon.ionelement.api.IonElement
import com.amazon.ionelement.api.toIonValue
import org.partiql.lang.domains.PartiqlAst
import org.partiql.lang.eval.ExprValue
import org.partiql.lang.eval.ExprValueFactory
import org.partiql.pig.runtime.SymbolPrimitive

fun ExprValueFactory.newFromIonElement(elem: IonElement): ExprValue {
    return this.newFromIonValue(elem.asAnyElement().toIonValue(this.ion))
}

fun SymbolPrimitive.toLiteral(): PartiqlAst.Expr {
    return PartiqlAst.Expr.Lit(this.toIonElement().asAnyElement())
}
