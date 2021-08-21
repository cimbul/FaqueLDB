package com.cimbul.faqeldb.session

import com.amazon.ion.IonSystem
import com.amazon.ion.IonValue
import com.amazon.ionelement.api.AnyElement
import com.amazon.ionelement.api.IonElement
import com.amazon.ionelement.api.ionNull
import com.amazon.ionelement.api.toIonValue
import com.cimbul.faqeldb.ionElement
import com.cimbul.faqeldb.toBinary
import com.cimbul.faqeldb.toText

data class ValueHolder(
    val ionText: String? = null,
    val ionBinary: ByteArray? = null,
) {
    fun toIonElement(): AnyElement =
        when {
            ionText != null -> ionElement(ionText)
            ionBinary != null -> ionElement(ionBinary)
            else -> ionNull().asAnyElement()
        }

    fun toIonValue(ion: IonSystem): IonValue = toIonElement().toIonValue(ion)

    companion object {
        fun binaryFrom(ion: IonValue) = ValueHolder(ionBinary = ion.toBinary())
        fun binaryFrom(ion: IonElement) = ValueHolder(ionBinary = ion.toBinary())
        fun textFrom(ion: IonValue) = ValueHolder(ionText = ion.toText())
        fun textFrom(ion: IonElement) = ValueHolder(ionText = ion.toText())
    }
}
