package com.cimbul.faqueldb.session

import com.amazon.ion.IonSystem
import com.amazon.ion.IonValue
import com.amazon.ionelement.api.AnyElement
import com.amazon.ionelement.api.IonElement
import com.amazon.ionelement.api.ionNull
import com.amazon.ionelement.api.toIonValue
import com.cimbul.faqueldb.ionElement
import com.cimbul.faqueldb.toBinary
import com.cimbul.faqueldb.toText

data class ValueHolder(
    val ionText: String? = null,
    val ionBinary: Bytes? = null,
) {
    fun toIonElement(): AnyElement =
        when {
            ionText != null -> ionElement(ionText)
            ionBinary != null -> ionElement(ionBinary.bytes)
            else -> ionNull().asAnyElement()
        }

    fun toIonValue(ion: IonSystem): IonValue = toIonElement().toIonValue(ion)

    companion object {
        fun binaryFrom(ion: IonValue) = ValueHolder(ionBinary = Bytes(ion.toBinary()))
        fun binaryFrom(ion: IonElement) = ValueHolder(ionBinary = Bytes(ion.toBinary()))
        fun textFrom(ion: IonValue) = ValueHolder(ionText = ion.toText())
        fun textFrom(ion: IonElement) = ValueHolder(ionText = ion.toText())
    }
}
