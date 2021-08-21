package com.cimbul.faqeldb

import com.amazon.ion.IonSystem
import com.amazon.ion.IonValue
import com.amazon.ionelement.api.AnyElement
import com.amazon.ionelement.api.IonElement
import com.amazon.ionelement.api.toIonValue
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.qldbsession.model.ValueHolder
import software.amazon.awssdk.utils.builder.SdkBuilder

fun <B, T> B.build(f: B.() -> Unit): T where B : SdkBuilder<B, T> {
    this.f()
    return this.build()
}

fun ionTextValue(text: String): ValueHolder {
    return ValueHolder.builder().build {
        ionText(text)
    }
}

fun ionTextValue(value: IonValue): ValueHolder {
    return ionTextValue(value.toText())
}

fun ionTextValue(value: IonElement): ValueHolder {
    return ionTextValue(value.toText())
}

fun ionBinaryValue(blob: ByteArray): ValueHolder {
    return ValueHolder.builder().build {
        ionBinary(SdkBytes.fromByteArray(blob))
    }
}

fun ionBinaryValue(value: IonValue) {
    ionBinaryValue(value.toBinary())
}

fun ionBinaryValue(value: IonElement) {
    ionBinaryValue(value.toBinary())
}

fun ValueHolder.ionElement(): AnyElement {
    return if (ionText() != null) {
        ionElement(ionText())
    } else {
        ionElement(ionBinary().asByteArray())
    }
}

fun ValueHolder.ionValue(ion: IonSystem): IonValue {
    return ionElement().toIonValue(ion)
}
