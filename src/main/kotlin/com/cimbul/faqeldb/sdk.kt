package com.cimbul.faqeldb

import software.amazon.awssdk.utils.builder.SdkBuilder

fun <B, T> B.build(f: B.() -> Unit): T where B : SdkBuilder<B, T> {
    this.f()
    return this.build()
}
