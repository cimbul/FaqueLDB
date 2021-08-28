package com.cimbul.faqueldb.data

import com.amazon.ion.Timestamp
import com.amazon.ionelement.api.IonElement
import com.amazon.ionelement.api.ionListOf
import com.amazon.ionelement.api.ionString
import com.amazon.ionelement.api.ionStructOf
import com.amazon.ionelement.api.ionTimestamp

data class Block(
    val address: BlockAddress,
    val transactionId: String,
    val timestamp: Timestamp,
    val previousBlockHash: Hash,
    val transactionInfo: TransactionInfo,
    val revisions: List<CommittedRevision>,
) {
    val revisionsHashTree = HashTree.fromLeaves(revisions.map { it.hash })
    val entriesHashTree = HashTree.fromLeaves(transactionInfo.hash, revisionsHashTree.hash)
    val hash = entriesHashTree.hash * previousBlockHash

    val ionElement: IonElement = ionStructOf(
        "address" to address.ionElement,
        "transactionId" to ionString(transactionId),
        "blockTimestamp" to ionTimestamp(timestamp),
        "blockHash" to hash.toIonElement(),
        "previousBlockHash" to previousBlockHash.toIonElement(),
        "entriesHash" to entriesHashTree.hash.toIonElement(),
        "entriesHashList" to ionListOf(entriesHashTree.map { it.toIonElement() }),
        "transactionInfo" to transactionInfo.ionElement,
        "revisions" to ionListOf(revisions.map { it.ionElement }),
    )
}
