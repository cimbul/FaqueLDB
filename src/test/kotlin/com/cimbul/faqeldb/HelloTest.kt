package com.cimbul.faqeldb

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class HelloTest : DescribeSpec({
    describe("greet") {
        it("should say hello") {
            Hello.greet("world") shouldBe "Hello, world!"
        }
    }
})
