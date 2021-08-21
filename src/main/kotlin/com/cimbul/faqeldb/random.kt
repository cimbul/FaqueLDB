package com.cimbul.faqeldb

import java.util.UUID
import kotlin.random.Random

fun Random.nextUUID(): UUID {
    //                  xxxxxxxxxxxxMxxx
    val versionMask = 0xFFFFFFFFFFFF0FFFUL
    val versionBits = 0x0000000000004000UL  // version 4
    val upper = this.nextLong().toULong().and(versionMask).or(versionBits)

    //                  Nxxxxxxxxxxxxxxx
    val variantMask = 0x3FFFFFFFFFFFFFFFUL
    val variantBits = 0x8000000000000000UL  // variant 2
    val lower = this.nextLong().toULong().and(variantMask).or(variantBits)

    return UUID(upper.toLong(), lower.toLong())
}
