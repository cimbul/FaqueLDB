package com.cimbul.faqeldb

import com.amazon.ionelement.api.IonElement
import com.amazon.ionelement.api.toIonValue
import org.partiql.lang.eval.ExprValue
import org.partiql.lang.eval.ExprValueFactory

fun ExprValueFactory.newFromIonElement(elem: IonElement): ExprValue {
    return this.newFromIonValue(elem.asAnyElement().toIonValue(this.ion))
}
