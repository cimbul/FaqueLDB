package com.cimbul.faqueldb.data

import com.amazon.ionelement.api.ElementType
import com.amazon.ionelement.api.ionInt
import com.amazon.ionelement.api.ionListOf
import com.amazon.ionelement.api.ionNull
import com.amazon.ionelement.api.ionString
import com.amazon.ionelement.api.ionStructOf
import com.amazon.ionelement.api.ionSymbol
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.nio.ByteBuffer

class HashTest : DescribeSpec({
    fun byteArrayOf(a: ULong, b: ULong, c: ULong, d: ULong): ByteArray {
        return ByteBuffer.allocate(32)
            .putLong(a.toLong())
            .putLong(b.toLong())
            .putLong(c.toLong())
            .putLong(d.toLong())
            .array()
    }

    describe("constructor") {
        it("should accept a zero-initialized array of the right length") {
            Hash(ByteArray(32))
        }

        it("should throw an exception if the input is not the right length") {
            shouldThrow<IllegalArgumentException> {
                Hash(ByteArray(31))
            }
        }

        it("should be resistant to mutation of the input") {
            val bytesA = byteArrayOf(
                0xcdc76e5c_9914fb92_UL, 0x81a1c7e2_84d73e67_UL,
                0xf1809a48_a497200e_UL, 0x046d39cc_c7112cd0_UL,
            )
            val bytesB = byteArrayOf(
                0xcdc76e5c_9914fb92_UL, 0x81a1c7e2_84d73e67_UL,
                0xf1809a48_a497200e_UL, 0x046d39cc_c7112cd0_UL,
            )
            val bytesC = byteArrayOf(
                0xcdc76e5c_9914fb92_UL, 0x81a1c7e2_84493e67_UL,
                0xf1809a48_a497200e_UL, 0x046d39cc_c7112cd0_UL,
            )

            val hashA = Hash(bytesA)
            val hashB = Hash(bytesB)
            val hashC = Hash(bytesC)

            hashA shouldBe hashB
            hashA shouldNotBe hashC

            bytesA[13] = 0x49

            hashA shouldBe hashB
            hashA shouldNotBe hashC
        }
    }

    describe("toByteArray()") {
        it("should produce the value supplied to the constructor") {
            val bytes = byteArrayOf(
                0x50e72a0e_26442fe2_UL, 0x552dc393_8ac58658_UL,
                0x228c0cbf_b1d2ca87_UL, 0x2ae43526_6fcd055e_UL,
            )
            val hash = Hash(bytes)

            hash.toByteArray() shouldBe byteArrayOf(
                0x50e72a0e_26442fe2_UL, 0x552dc393_8ac58658_UL,
                0x228c0cbf_b1d2ca87_UL, 0x2ae43526_6fcd055e_UL,
            )
        }

        it("should not allow the hash to be mutated") {
            val bytesA = byteArrayOf(
                0xcdc76e5c_9914fb92_UL, 0x81a1c7e2_84d73e67_UL,
                0xf1809a48_a497200e_UL, 0x046d39cc_c7112cd0_UL,
            )
            val bytesB = byteArrayOf(
                0xcdc76e5c_9914fb92_UL, 0x81a1c7e2_84d73e67_UL,
                0xf1809a48_a497200e_UL, 0x046d39cc_c7112cd0_UL,
            )
            val bytesC = byteArrayOf(
                0xcdc76e5c_9914fb92_UL, 0x81a1c7e2_84493e67_UL,
                0xf1809a48_a497200e_UL, 0x046d39cc_c7112cd0_UL,
            )

            val hashA = Hash(bytesA)
            val hashB = Hash(bytesB)
            val hashC = Hash(bytesC)

            hashA shouldBe hashB
            hashA shouldNotBe hashC

            hashA.toByteArray()[13] = 0x49

            hashA shouldBe hashB
            hashA shouldNotBe hashC
        }
    }

    describe("of(ByteArray)") {
        it("should produce the expected value for an empty input") {
            val expectedBytes = byteArrayOf(
                0xe3b0c442_98fc1c14_UL, 0x9afbf4c8_996fb924_UL,
                0x27ae41e4_649b934c_UL, 0xa495991b_7852b855_UL,
            )

            Hash.of(ByteArray(0)) shouldBe Hash(expectedBytes)
        }

        it("should produce the expected value for the ASCII string 'abc'") {
            val expectedBytes = byteArrayOf(
                0xba7816bf_8f01cfea_UL, 0x414140de_5dae2223_UL,
                0xb00361a3_96177a9c_UL, 0xb410ff61_f20015ad_UL,
            )

            Hash.of(byteArrayOf(0x61, 0x62, 0x63)) shouldBe Hash(expectedBytes)
        }
    }

    describe("of(IonElement)") {
        it("should produce the expected value for the null.null element") {
            Hash.of(ionNull()) shouldBe Hash.of(byteArrayOf(0x0b, 0x0f, 0x0e))
        }

        it("should produce identical hashes for identical elements") {
            Hash.of(ionInt(1)) shouldBe Hash.of(ionInt(1))
            Hash.of(ionNull(ElementType.BOOL)) shouldBe Hash.of(ionNull(ElementType.BOOL))
            Hash.of(ionSymbol("foo")) shouldBe Hash.of(ionSymbol("foo"))
            Hash.of(ionListOf(ionInt(3), ionString("foo"))) shouldBe
                Hash.of(ionListOf(ionInt(3), ionString("foo")))
            Hash.of(ionStructOf("foo" to ionInt(42), "bar" to ionString("quux"))) shouldBe
                Hash.of(ionStructOf("bar" to ionString("quux"), "foo" to ionInt(42)))
        }

        it("should produce different hashes for non-identical elements") {
            Hash.of(ionInt(1)) shouldNotBe Hash.of(ionInt(2))
            Hash.of(ionNull(ElementType.BOOL)) shouldNotBe Hash.of(ionNull(ElementType.INT))
            Hash.of(ionSymbol("foo")) shouldNotBe Hash.of(ionSymbol("foo."))
            Hash.of(ionListOf(ionInt(3), ionString("foo"))) shouldNotBe
                Hash.of(ionListOf(ionInt(3), ionString("foo!")))
            Hash.of(ionStructOf("foo" to ionInt(42), "bar" to ionString("quux"))) shouldNotBe
                Hash.of(ionStructOf("foo" to ionInt(42), "baz" to ionString("quux")))
            Hash.of(ionStructOf("foo" to ionInt(42), "bar" to ionString("quux"))) shouldNotBe
                Hash.of(ionStructOf("foo" to ionInt(24), "baz" to ionString("quux")))
        }
    }
})
