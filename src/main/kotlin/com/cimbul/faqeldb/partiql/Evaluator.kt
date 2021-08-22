package com.cimbul.faqeldb.partiql

import com.amazon.ion.system.IonSystemBuilder
import com.amazon.ionelement.api.AnyElement
import com.amazon.ionelement.api.IonElement
import com.amazon.ionelement.api.toIonElement
import com.cimbul.faqeldb.data.Database
import com.cimbul.faqeldb.partiql.function.createFunctions
import com.cimbul.faqeldb.partiql.procedure.createProcedures
import org.partiql.lang.CompilerPipeline
import org.partiql.lang.ast.toAstStatement
import org.partiql.lang.ast.toExprNode
import org.partiql.lang.eval.Bindings
import org.partiql.lang.eval.EvaluationSession
import org.partiql.lang.eval.ExprValueFactory
import org.partiql.lang.eval.visitors.PipelinedVisitorTransform

class Evaluator {
    private val database = Database()

    private val ion = IonSystemBuilder.standard().build()
    private val valueFactory = ExprValueFactory.standard(ion)
    private val transform = PipelinedVisitorTransform(
        QueryTransformer(),
        ProcedureTransformer(),
    )
    private val compiler = CompilerPipeline.build(valueFactory) {
        addPreprocessingStep { exprNode, _ ->
            val preTransform = exprNode.toAstStatement()
            val postTransform = transform.transformStatement(preTransform)
            postTransform.toExprNode(ion)
        }

        for (procedure in createProcedures(database, valueFactory)) {
            addProcedure(procedure)
        }

        for (function in createFunctions(database, valueFactory)) {
            addFunction(function)
        }
    }

    fun evaluate(statement: String, parameters: List<IonElement> = listOf()): AnyElement {
        val expression = compiler.compile(statement)
        val parameterValues = parameters.map(valueFactory::newFromIonElement)
        val session = EvaluationSession.build {
            parameters(parameterValues)
            globals(Bindings.over { name -> database.getBinding(name, valueFactory) })
        }
        val value = expression.eval(session)
        return value.ionValue.toIonElement()
    }
}
