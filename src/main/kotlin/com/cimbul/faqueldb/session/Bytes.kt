package com.cimbul.faqueldb.session

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

/** Wrapper to avoid having to override `equals()`/`hashCode()` in data classes. */
data class Bytes
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    constructor(@JsonValue val bytes: ByteArray)
{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Bytes
        if (!bytes.contentEquals(other.bytes)) return false
        return true
    }

    override fun hashCode(): Int {
        return bytes.contentHashCode()
    }
}
