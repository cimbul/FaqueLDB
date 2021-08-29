package com.cimbul.faqueldb.data

import com.amazon.ionelement.api.IonElement

data class UncommittedRevision(
    override val id: String,
    override val version: Long,
    override val data: IonElement,
) : Revision {
    fun committed(commit: CommitInfo): CommittedRevision {
        val metadata = RevisionMetadata(id, version, commit.time, commit.id)
        return CommittedRevision(commit.blockAddress, metadata, data)
    }
}
