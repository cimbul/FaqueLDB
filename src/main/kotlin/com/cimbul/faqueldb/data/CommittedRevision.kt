package com.cimbul.faqueldb.data

import com.amazon.ionelement.api.IonElement
import com.amazon.ionelement.api.ionStructOf

data class CommittedRevision(
    val blockAddress: BlockAddress,
    val metadata: RevisionMetadata,
    override val data: IonElement
) : Revision {
    override val id get() = metadata.id
    override val version get() = metadata.version

    val hash = Hash.of(data) * metadata.hash

    val ionElement: IonElement = ionStructOf(
        "blockAddress" to blockAddress.ionElement,
        "metadata" to metadata.ionElement,
        "data" to data,
        "hash" to hash.toIonElement(),
    )
}
