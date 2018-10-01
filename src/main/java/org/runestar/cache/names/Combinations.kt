package org.runestar.cache.names

import kotlin.math.max

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