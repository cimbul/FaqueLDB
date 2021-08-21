package com.cimbul.faqeldb

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

fun ionBinaryValue(blob: ByteArray): ValueHolder {
    return ValueHolder.builder().build {
        ionBinary(SdkBytes.fromByteArray(blob))
    }
}
