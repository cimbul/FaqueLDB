package com.cimbul.faqueldb.data

sealed class HashTree: Iterable<Hash> {
    abstract val hash: Hash
    abstract fun asSequence(): Sequence<Hash>

    override fun hashCode(): Int = hash.hashCode()
    override fun equals(other: Any?): Boolean = other is HashTree && this.hash == other.hash
    override fun iterator(): Iterator<Hash> = asSequence().iterator()

    companion object {
        fun fromLeaves(vararg leaves: Hash): HashTree = fromLeaves(leaves.asIterable())

        fun fromLeaves(leaves: Iterable<Hash>): HashTree {
            var nodes = leaves.map { HashTreeLeaf(it) }.toList<HashTree>()
            while (nodes.size > 1) {
                nodes = nodes
                    .windowed(size = 2, step = 2, partialWindows = true)
                    .map { pair -> if (pair.size == 1) pair[0] else HashTreeNode(pair[0], pair[1]) }
            }
            return if (nodes.isEmpty()) HashTreeLeaf(Hash.zero) else nodes.single()
        }
    }

    private class HashTreeLeaf(override val hash: Hash) : HashTree() {
        override fun asSequence(): Sequence<Hash> = sequenceOf(hash)
    }

    private class HashTreeNode(val left: HashTree, val right: HashTree) : HashTree() {
        override val hash = left.hash * right.hash
        override fun asSequence(): Sequence<Hash> = left.asSequence() + right.asSequence()
    }
}
