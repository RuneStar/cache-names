package org.runestar.cache.names

interface IntSet {

    companion object {

        fun of(set: Set<Int>): IntSet {
            return if (set.size == 1) {
                One(set)
            } else {
                Hash(set)
            }
        }
    }

    operator fun contains(value: Int): Boolean

    private class One(set: Set<Int>) : IntSet {

        private val n = set.single()

        override fun contains(value: Int) = n == value
    }

    private class Hash(set: Set<Int>) : IntSet {

        private val elements = IntArray(Integer.highestOneBit(set.size * 7))

        private val size = elements.size

        private val mask = size - 1

        init {
            for (e in set) {
                check(e != 0)
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