package org.runestar.cache.names

import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.util.concurrent.Executor

fun unhash(
        results: OutputStream,
        alphabet: String,
        prefix: String,
        suffix: String,
        targetHashes: IntSet,
        maxCombinations: Int,
        executor: Executor
) {
    val alphabetArray = alphabet.toByteArray(CHARSET).distinctArray()
    val prefixBytes = prefix.toByteArray(CHARSET)
    val suffixBytes = suffix.toByteArray(CHARSET)
    val base = alphabetArray.size

    for (startIndex in 0 until base) {
        executor.execute {
            val out = ByteArrayOutputStream()
            val curHashes = IntArray(maxCombinations)
            curHashes[0] = 0.update(prefixBytes)
            multisetPermutations(base, maxCombinations, intArrayOf(startIndex)) { indices, len ->
                val pos = len - 1
                val hash = curHashes[pos].update(alphabetArray[indices[pos]])
                if (len != maxCombinations) {
                    curHashes[len] = hash
                }
                if (hash.update(suffixBytes) in targetHashes) {
                    out.write(prefixBytes)
                    for (i in 0 until len) {
                        out.write(alphabetArray[indices[i]])
                    }
                    out.write(suffixBytes)
                    out.write('\n'.toByte())
                }
            }
            out.writeTo(results)
        }
    }
}

fun unhash(
        results: OutputStream,
        dict: Set<String>,
        prefix: String,
        suffix: String,
        separator: Char,
        targetHashes: IntSet,
        maxCombinations: Int,
        executor: Executor
) {
    val prefixBytes = prefix.toByteArray(CHARSET)
    val suffixBytes = suffix.toByteArray(CHARSET)
    val separatorByte = separator.toByte(CHARSET)
    val dictArray = dict.map { it.toByteArray(CHARSET) }.toTypedArray()
    val base = dictArray.size

    for (startIndex in 0 until base) {
        executor.execute {
            val out = ByteArrayOutputStream()
            val curHashes = IntArray(maxCombinations)
            curHashes[0] = 0.update(prefixBytes)
            multisetPermutations(base * 2, maxCombinations, intArrayOf(startIndex)) { indices, len ->
                val pos = len - 1
                val v = indices[pos]
                val lastHash = curHashes[pos]
                val hash = if (v >= base) {
                    lastHash.update(separatorByte).update(dictArray[v - base])
                } else {
                    lastHash.update(dictArray[v])
                }
                if (len != maxCombinations) {
                    curHashes[len] = hash
                }
                if (hash.update(suffixBytes) in targetHashes) {
                    out.write(prefixBytes)
                    for (i in 0 until len) {
                        val vv = indices[i]
                        if (vv >= base) {
                            out.write(separatorByte)
                            out.write(dictArray[vv - base])
                        } else {
                            out.write(dictArray[vv])
                        }
                    }
                    out.write(suffixBytes)
                    out.write('\n'.toByte())
                }
            }
            out.writeTo(results)
        }
    }
}