package com.cimbul.faqeldb

import com.amazon.ion.system.IonSystemBuilder
import com.amazon.ionelement.api.AnyElement
import com.amazon.ionelement.api.IonElement
import com.amazon.ionelement.api.toIonElement
import com.cimbul.faqeldb.procedure.createProcedures
import org.partiql.lang.CompilerPipeline
import org.partiql.lang.ast.toAstStatement
import org.partiql.lang.ast.toExprNode
import org.partiql.lang.eval.EvaluationSession
import org.partiql.lang.eval.ExprValueFactory

class Evaluator {
    private val ion = IonSystemBuilder.standard().build()
    private val valueFactory = ExprValueFactory.standard(ion)
    private val transform = Transformer()
    private val compiler = CompilerPipeline.build(valueFactory) {
        addPreprocessingStep { exprNode, _ ->
            val preTransform = exprNode.toAstStatement()
            val postTransform = transform.transformStatement(preTransform)
            postTransform.toExprNode(ion)
        }

        for (procedure in createProcedures(valueFactory)) {
            addProcedure(procedure)
        }
    }

    fun evaluate(statement: String, parameters: List<IonElement>): AnyElement {
        val expression = compiler.compile(statement)
        val parameterValues = parameters.map(valueFactory::newFromIonElement)
        val session = EvaluationSession.build {
            parameters(parameterValues)
        }
        val value = expression.eval(session)
        return value.ionValue.toIonElement()
    }
}
