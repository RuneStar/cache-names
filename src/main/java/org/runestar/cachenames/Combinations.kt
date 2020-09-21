package org.runestar.cachenames

import kotlin.math.max

inline fun multisetPermutations(
        base: Int,
        maxLength: Int,
        prefix: IntArray,
        action: (stack: IntArray, len: Int) -> Unit
) {
    val stack = prefix.copyOf(maxLength)
    val prefixLength = prefix.size
    var len = max(prefixLength, 1)
    while (true) {
        action(stack, len)
        if (len != maxLength) {
            stack[len++] = 0
        } else {
            var i: Int
            do {
                if (len == prefixLength) return
                i = stack[--len] + 1
            } while (i == base)
            stack[len++] = i
        }
    }
}