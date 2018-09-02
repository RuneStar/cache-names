package org.runestar.cache.names

interface IntSet {

    companion object {

        fun of(set: Set<Int>): IntSet {
            return if (set.size == 1) {
                One(set.single())
            } else {
                Hash(set)
            }
        }
    }

    operator fun contains(value: Int): Boolean

    private class One(private val n: Int) : IntSet {

        override fun contains(value: Int) = n == value
    }

    private class Hash(set: Set<Int>) : IntSet {

        private val elements = IntArray(Integer.highestOneBit(set.size * 3) shl 1)

        private val size = elements.size

        private val mask = size - 1

        init {
            for (e in set) {
                elements[probe(e)] = e
            }
        }

        private fun index(value: Int): Int {
            return (value * -0x5354d5b3 * 0x5CC7DF53) and mask
        }

        private fun probe(value: Int): Int {
            var idx = index(value)
            while (true) {
                val e = elements[idx]
                when {
                    e == 0 -> return idx
                    ++idx == size -> idx = 0
                }
            }
        }

        override fun contains(value: Int): Boolean {
            var idx = index(value)
            while (true) {
                val e = elements[idx]
                when {
                    e == 0 -> return false
                    value == e -> return true
                    ++idx == size -> idx = 0
                }
            }
        }
    }
}