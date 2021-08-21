package com.cimbul.faqeldb

import com.amazon.ion.IonValue
import com.amazon.ion.system.IonBinaryWriterBuilder
import com.amazon.ion.system.IonReaderBuilder
import com.amazon.ion.system.IonTextWriterBuilder
import com.amazon.ionelement.api.AnyElement
import com.amazon.ionelement.api.IonElement
import com.amazon.ionelement.api.IonElementLoaderOptions
import com.amazon.ionelement.api.createIonElementLoader
import java.io.ByteArrayOutputStream

private val readerBuilder = IonReaderBuilder.standard()
private val textWriterBuilder = IonTextWriterBuilder.standard()
private val binaryWriterBuilder = IonBinaryWriterBuilder.standard()
private val loader = createIonElementLoader(IonElementLoaderOptions(true))

fun IonValue.toText(): String {
    return toString(textWriterBuilder)
}

fun IonValue.toBinary(): ByteArray {
    val out = ByteArrayOutputStream()
    val writer = binaryWriterBuilder.build(out)
    writeTo(writer)
    return out.toByteArray()
}

fun IonElement.toText(): String {
    return toString()
}

fun IonElement.toBinary(): ByteArray {
    val out = ByteArrayOutputStream()
    val writer = binaryWriterBuilder.build(out)
    writeTo(writer)
    return out.toByteArray()
}

fun ionElement(text: String): AnyElement {
    return loader.loadSingleElement(readerBuilder.build(text))
}

fun ionElement(binary: ByteArray): AnyElement {
    return loader.loadSingleElement(readerBuilder.build(binary))
}
