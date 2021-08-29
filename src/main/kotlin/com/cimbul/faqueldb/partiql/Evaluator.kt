package com.cimbul.faqueldb.partiql

import com.amazon.ion.system.IonSystemBuilder
import com.amazon.ionelement.api.AnyElement
import com.amazon.ionelement.api.IonElement
import com.amazon.ionelement.api.toIonElement
import com.cimbul.faqueldb.data.StatementContext
import com.cimbul.faqueldb.partiql.function.createFunctions
import com.cimbul.faqueldb.partiql.procedure.createProcedures
import org.partiql.lang.CompilerPipeline
import org.partiql.lang.ast.toAstStatement
import org.partiql.lang.ast.toExprNode
import org.partiql.lang.eval.Bindings
import org.partiql.lang.eval.EvaluationSession
import org.partiql.lang.eval.ExprValueFactory
import org.partiql.lang.eval.visitors.PipelinedVisitorTransform

class Evaluator(private val context: StatementContext) {
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

        for (procedure in createProcedures(context, valueFactory)) {
            addProcedure(procedure)
        }

        for (function in createFunctions(context, valueFactory)) {
            addFunction(function)
        }
    }

    fun evaluate(statement: String, parameters: List<IonElement> = listOf()): AnyElement {
        val expression = compiler.compile(statement)
        val parameterValues = parameters.map(valueFactory::newFromIonElement)
        val session = EvaluationSession.build {
            parameters(parameterValues)
            globals(Bindings.over { name ->
                context.transaction.database.getBinding(name, valueFactory)
            })
        }
        val value = expression.eval(session)
        return value.ionValue.toIonElement()
    }
}
