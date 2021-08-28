package com.cimbul.faqueldb.data

import com.amazon.ionelement.api.IonElement
import com.amazon.ionelement.api.ionBlob
import com.amazon.ionhash.MessageDigestIonHasherProvider
import com.cimbul.faqueldb.hash
import java.security.MessageDigest
import java.util.Base64

class Hash(inputBytes: ByteArray) : Comparable<Hash> {
    init {
        require(inputBytes.size == byteLength) {
            "Invalid hash length. Expected $byteLength, got $bytes.size"
        }
    }

    private val bytes = inputBytes.copyOf()

    fun toByteArray() = bytes.copyOf()
    fun toIonElement(): IonElement = ionBlob(bytes) // Note that ionBlob already copies the array

    /** Concatenate the hashes in sorted order, then hash the result */
    operator fun times(other: Hash): Hash {
        val concatenatedBytes = if (this < other)
            this.bytes + other.bytes
        else
            other.bytes + this.bytes
        return of(concatenatedBytes)
    }

    /** Compare the bytes lexicographically */
    override fun compareTo(other: Hash): Int {
        var i = 0
        var result = 0
        while (result == 0 && i < byteLength) {
            result = this.bytes[i].compareTo(other.bytes[i])
            i += 1
        }
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Hash
        if (!bytes.contentEquals(other.bytes)) return false
        return true
    }

    override fun hashCode(): Int {
        return bytes.contentHashCode()
    }

    override fun toString(): String {
        val encoded = Base64.getEncoder().encodeToString(bytes)
        return "Hash(\"$encoded\")"
    }

    companion object {
        const val algorithm = "SHA-256"
        const val byteLength = 256 / 8
        private val hasherProvider = MessageDigestIonHasherProvider(algorithm)

        val zero = Hash(ByteArray(byteLength))

        fun of(data: ByteArray) =
            Hash(MessageDigest.getInstance(algorithm).digest(data))

        fun of(element: IonElement) = Hash(element.hash(hasherProvider))
    }
}
