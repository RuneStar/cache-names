package org.runestar.cache.names

import java.nio.ByteBuffer
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun unhashChars(
        alphabet: String,
        prefix: String,
        targetHashes: IntSet,
        maxCombinations: Int
) {
    val channel = openResultsChannel()

    val alphabetArray = alphabet.toByteArray(CHARSET).toSet().toByteArray()
    val prefixBytes = prefix.toByteArray(CHARSET)
    val n = alphabetArray.size

    val pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1)

    for (startIndex in 0 until n) {
        pool.submit {
            val writeBuf = ByteBuffer.allocate(maxCombinations + 1 + prefixBytes.size)
            writeBuf.put(prefixBytes)
            val curHashes = IntArray(maxCombinations)
            curHashes[0] = 0.update(prefixBytes)
            multisetPermutations(n, maxCombinations, intArrayOf(startIndex)) { indices, len ->
                val lastHash = curHashes[len - 1]
                val hash = lastHash.update(alphabetArray[indices[len - 1]])
                if (len != maxCombinations) {
                    curHashes[len] = hash
                }
                if (hash in targetHashes) {
                    writeBuf.clear().position(prefixBytes.size)
                    for (i in 0 until len) {
                        writeBuf.put(alphabetArray[indices[i]])
                    }
                    writeBuf.put('\n'.toByte())
                    writeBuf.flip()
                    channel.write(writeBuf)
                }
            }
        }
    }

    pool.shutdown()
    pool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS)
    channel.close()
}