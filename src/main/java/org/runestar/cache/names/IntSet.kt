package org.runestar.cache.names

import java.lang.IllegalArgumentException

interface IntSet {

    companion object {

        fun of(set: Set<Int>): IntSet {
            val array = set.toIntArray()
            return when (array.size) {
                0 -> throw IllegalArgumentException()
                1 -> One(array[0])
                2 -> Two(array[0], array[1])
                3 -> Three(array[0], array[1], array[2])
                4 -> Four(array[0], array[1], array[2], array[3])
                else -> Hash(array)
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

    private class Four(private val n0: Int, private val n1: Int, private val n2: Int, private val n3: Int) : IntSet {
        override fun contains(value: Int) = value == n0 || value == n1 || value == n2 || value == n3
    }

    private class Hash(ns: IntArray) : IntSet {

        private val elements = IntArray((ns.size * 40).takeHighestOneBit())

        private val mask = elements.size - 1

        init {
            for (e in ns) {
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
                    else -> idx = (idx + 1) and mask
                }
            }
        }

        override fun contains(value: Int): Boolean {
            var idx = index(value)
            while (true) {
                when (elements[idx]) {
                    0 -> return false
                    value -> return true
                    else -> idx = (idx + 1) and mask
                }
            }
        }
    }
}