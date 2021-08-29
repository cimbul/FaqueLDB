package com.cimbul.faqueldb

import com.amazon.ion.IonValue
import com.amazon.ion.Timestamp
import com.amazon.ion.system.IonBinaryWriterBuilder
import com.amazon.ion.system.IonReaderBuilder
import com.amazon.ion.system.IonTextWriterBuilder
import com.amazon.ionelement.api.AnyElement
import com.amazon.ionelement.api.IonElement
import com.amazon.ionelement.api.IonElementLoaderOptions
import com.amazon.ionelement.api.createIonElementLoader
import com.amazon.ionhash.IonHashWriterBuilder
import com.amazon.ionhash.IonHasherProvider
import java.io.ByteArrayOutputStream
import java.time.Instant

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
    writer.close()
    return out.toByteArray()
}

fun IonValue.hash(hasherProvider: IonHasherProvider): ByteArray {
    val out = ByteArrayOutputStream()
    val writer = binaryWriterBuilder.build(out)
    val hashWriter = IonHashWriterBuilder.standard()
        .withHasherProvider(hasherProvider)
        .withWriter(writer)
        .build()
    writeTo(hashWriter)
    hashWriter.close()
    return hashWriter.digest()
}

fun IonElement.toText(): String {
    return toString()
}

fun IonElement.toBinary(): ByteArray {
    val out = ByteArrayOutputStream()
    val writer = binaryWriterBuilder.build(out)
    writeTo(writer)
    writer.close()
    return out.toByteArray()
}

fun IonElement.hash(hasherProvider: IonHasherProvider): ByteArray {
    val out = ByteArrayOutputStream()
    val writer = binaryWriterBuilder.build(out)
    val hashWriter = IonHashWriterBuilder.standard()
        .withHasherProvider(hasherProvider)
        .withWriter(writer)
        .build()
    writeTo(hashWriter)
    hashWriter.close()
    return hashWriter.digest()
}

fun ionElement(text: String): AnyElement {
    return loader.loadSingleElement(readerBuilder.build(text))
}

fun ionElement(binary: ByteArray): AnyElement {
    return loader.loadSingleElement(readerBuilder.build(binary))
}

fun Instant.toIonTimestamp(): Timestamp {
    return Timestamp.forMillis(this.toEpochMilli(), 0)
}
