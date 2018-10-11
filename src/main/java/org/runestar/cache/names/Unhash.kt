package org.runestar.cache.names

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
    val writeBufCapacity = prefixBytes.size + maxCombinations + suffixBytes.size + 1

    val pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1)

    for (startIndex in 0 until n) {
        pool.submit {
            val curHashes = IntArray(maxCombinations)
            curHashes[0] = 0.update(prefixBytes)
            val writeBuf = ByteBuffer.allocate(writeBufCapacity)
            writeBuf.put(prefixBytes)
            multisetPermutations(n, maxCombinations, intArrayOf(startIndex)) { indices, len ->
                val lastHash = curHashes[len - 1]
                val hash = lastHash.update(alphabetArray[indices[len - 1]])
                if (len != maxCombinations) {
                    curHashes[len] = hash
                }
                if (hash.update(suffixBytes) in targetHashes) {
                    writeBuf.clear().position(prefixBytes.size)
                    for (i in 0 until len) {
                        writeBuf.put(alphabetArray[indices[i]])
                    }
                    writeBuf.put(suffixBytes).put('\n'.toByte())
                    writeBuf.flip()
                    results.write(writeBuf)
                }
            }
        }
    }

    pool.shutdown()
    pool.awaitTermination()
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
    val maxWordLength = dictArray.maxBy { it.size }!!.size
    val n = dictArray.size
    val writeBufCapacity = prefixBytes.size + ((maxWordLength + 1) * maxCombinations - 1) + suffixBytes.size + 1

    val pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1)

    for (startIndex in 0 until n) {
        pool.submit {
            val curHashes = IntArray(maxCombinations)
            curHashes[0] = 0.update(prefixBytes)
            val writeBuf = ByteBuffer.allocate(writeBufCapacity)
            writeBuf.put(prefixBytes)
            multisetPermutations(n * 2, maxCombinations, intArrayOf(startIndex)) { indices, len ->
                val v = indices[len - 1]
                val lastHash = curHashes[len - 1]
                val hash = if (v >= n) {
                    lastHash.update(separatorByte).update(dictArray[v - n])
                } else {
                    lastHash.update(dictArray[v])
                }
                if (len != maxCombinations) {
                    curHashes[len] = hash
                }
                if (hash.update(suffixBytes) in targetHashes) {
                    writeBuf.clear().position(prefixBytes.size)
                    for (i in 0 until len) {
                        val vv = indices[i]
                        if (vv >= n) {
                            writeBuf.put(separatorByte).put(dictArray[vv - n])
                        } else {
                            writeBuf.put(dictArray[vv])
                        }
                    }
                    writeBuf.put(suffixBytes).put('\n'.toByte())
                    writeBuf.flip()
                    results.write(writeBuf)
                }
            }
        }
    }

    pool.shutdown()
    pool.awaitTermination()
}