package com.cimbul.faqueldb.data

import com.amazon.ionelement.api.IonElement
import com.amazon.ionelement.api.ionStructOf

data class CommittedRevision(
    val blockAddress: BlockAddress,
    val metadata: RevisionMetadata,
    val data: IonElement
) {
    val hash = Hash.of(data) * metadata.hash

    val ionElement: IonElement = ionStructOf(
        "blockAddress" to blockAddress.ionElement,
        "metadata" to metadata.ionElement,
        "data" to data,
        "hash" to hash.toIonElement(),
    )
}
