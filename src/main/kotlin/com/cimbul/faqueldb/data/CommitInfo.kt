package com.cimbul.faqueldb.data

import com.amazon.ion.Timestamp

data class CommitInfo(
    val id: String,
    val time: Timestamp,
    val blockAddress: BlockAddress,
)
