package org.runestar.cache.names

interface IntSet {

    companion object {

        fun of(set: Set<Int>): IntSet {
            return when (set.size) {
                1 -> One(set.single())
                2 -> {
                    val itr = set.iterator()
                    Two(itr.next(), itr.next())
                }
                3 -> {
                    val itr = set.iterator()
                    Three(itr.next(), itr.next(), itr.next())
                }
                else -> Hash(set)
            }
        }
    }

    operator fun contains(value: Int): Boolean

    private class One(private val n: Int) : IntSet {

        override fun contains(value: Int) = n == value
    }

    private class Two(private val n0: Int, private val n1: Int) : IntSet {

        override fun contains(value: Int) = value == n0 || value == n1
    }

    private class Three(private val n0: Int, private val n1: Int, private val n2: Int) : IntSet {

        override fun contains(value: Int) = value == n0 || value == n1 || value == n2
    }

    private class Hash(set: Set<Int>) : IntSet {

        private val elements = IntArray(Integer.highestOneBit(set.size * 40))

        private val size = elements.size

        private val mask = size - 1

        init {
            for (e in set) {
                require(e != 0)
                elements[probeInitial(e)] = e
            }
        }

        private fun index(value: Int): Int {
            return (value * 0x5BD1E995) and mask
        }

        private fun probeInitial(value: Int): Int {
            var idx = index(value)
            while (true) {
                when (elements[idx]) {
                    0 -> return idx
                    value -> throw IllegalStateException()
                    else -> if (++idx == size) idx = 0
                }
            }
        }

        override fun contains(value: Int): Boolean {
            var idx = index(value)
            while (true) {
                when (elements[idx]) {
                    0 -> return false
                    value -> return true
                    else -> if (++idx == size) idx = 0
                }
            }
        }
    }
}