package org.runestar.cache.names

import java.util.*
import kotlin.math.max

inline fun permutations(length: Int, action: (IntArray) -> Unit) {
    val objs = IntArray(length) { it }
    val c = IntArray(length)
    action(objs)
    var plus = false
    var i = 0
    while (i < length) {
        if (c[i] < i) {
            if (i % 2 == 0) {
                objs.swap(0, i)
            } else {
                objs.swap(c[i], i)
            }
            action(objs)
            plus = !plus
            c[i]++
            i = 0
        } else {
            c[i] = 0
            i++
        }
    }
}

fun IntArray.swap(indexA: Int, indexB: Int) {
    val a = get(indexA)
    set(indexA, get(indexB))
    set(indexB, a)
}

inline fun subsets(n: Int, k: Int, b: BitSet, action: (BitSet) -> Unit) {
    b.clear()
    b.set(0, k)
    while (!b.get(n)) {
        action(b)
        val lo = b.nextSetBit(0)
        val lz = b.nextClearBit(lo)
        b.set(lz)
        b.clear(0, lz)
        b.set(0, lz - lo - 1)
    }
}

inline fun multisetPermutations(
        n: Int,
        k: Int,
        prefix: IntArray,
        action: (stack: IntArray, len: Int) -> Unit
) {
    val stack = prefix.copyOf(k)
    val prefixLength = prefix.size
    var len = max(prefixLength, 1)
    while (true) {
        action(stack, len)
        if (len != k) {
            stack[len++] = 0
        } else {
            var i: Int
            do {
                if (len == prefixLength) return
                i = stack[--len] + 1
            } while (i == n)
            stack[len++] = i
        }
    }
}