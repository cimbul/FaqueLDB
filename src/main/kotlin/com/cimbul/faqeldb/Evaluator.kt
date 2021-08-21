package com.cimbul.faqeldb

import com.amazon.ion.system.IonSystemBuilder
import com.amazon.ionelement.api.IonElement
import com.amazon.ionelement.api.toIonElement
import com.amazon.ionelement.api.toIonValue
import org.partiql.lang.CompilerPipeline
import org.partiql.lang.eval.EvaluationSession
import org.partiql.lang.eval.ExprValueFactory

class Evaluator {
    private val ion = IonSystemBuilder.standard().build()
    private val valueFactory = ExprValueFactory.standard(ion)
    private val compiler = CompilerPipeline.standard(valueFactory)

    fun evaluate(statement: String, parameters: List<IonElement>): AnyElement {
        val expression = compiler.compile(statement)
        val parameterValues = parameters
            .map { it.asAnyElement().toIonValue(ion) }
            .map(valueFactory::newFromIonValue)
        val session = EvaluationSession.build {
            parameters(parameterValues)
        }
        val value = expression.eval(session)
        return value.ionValue.toIonElement()
    }
}
