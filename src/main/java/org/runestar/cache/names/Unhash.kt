package org.runestar.cache.names

import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.channels.WritableByteChannel
import java.util.concurrent.Executors

fun unhash(
        results: WritableByteChannel,
        alphabet: String,
        prefix: String,
        suffix: String,
        targetHashes: IntSet,
        maxCombinations: Int
) {
    val alphabetArray = alphabet.toByteArray(CHARSET).distinctArray()
    val prefixBytes = prefix.toByteArray(CHARSET)
    val suffixBytes = suffix.toByteArray(CHARSET)
    val n = alphabetArray.size

    val pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1)

    for (startIndex in 0 until n) {
        pool.submit {
            val out = ByteArrayOutputStream()
            val curHashes = IntArray(maxCombinations)
            curHashes[0] = 0.update(prefixBytes)
            multisetPermutations(n, maxCombinations, intArrayOf(startIndex)) { indices, len ->
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
            results.write(out.toByteArray())
        }
    }

    pool.shutdownAwait()
}

fun unhash(
        results: WritableByteChannel,
        dict: Set<String>,
        prefix: String,
        suffix: String,
        separator: Char,
        targetHashes: IntSet,
        maxCombinations: Int
) {
    val prefixBytes = prefix.toByteArray(CHARSET)
    val suffixBytes = suffix.toByteArray(CHARSET)
    val separatorByte = separator.toByte(CHARSET)
    val dictArray = dict.map { it.toByteArray(CHARSET) }.toTypedArray()
    val n = dictArray.size

    val pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1)

    for (startIndex in 0 until n) {
        pool.submit {
            val out = ByteArrayOutputStream()
            val curHashes = IntArray(maxCombinations)
            curHashes[0] = 0.update(prefixBytes)
            multisetPermutations(n * 2, maxCombinations, intArrayOf(startIndex)) { indices, len ->
                val pos = len - 1
                val v = indices[pos]
                val lastHash = curHashes[pos]
                val hash = if (v >= n) {
                    lastHash.update(separatorByte).update(dictArray[v - n])
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
                        if (vv >= n) {
                            out.write(separatorByte)
                            out.write(dictArray[vv - n])
                        } else {
                            out.write(dictArray[vv])
                        }
                    }
                    out.write(suffixBytes)
                    out.write('\n'.toByte())
                }
            }
            results.write(out.toByteArray())
        }
    }

    pool.shutdownAwait()
}