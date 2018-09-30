package org.runestar.cache.names

import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.channels.WritableByteChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun unhashDict(
        results: WritableByteChannel,
        dict: Set<String>,
        targetHashes: IntSet,
        maxCombinations: Int
) {
    val dictArray = dict.map { it.toByteArray(CHARSET) }.toTypedArray()
    val maxWordLength = dictArray.maxBy { it.size }!!.size
    val n = dictArray.size

    val pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1)

    for (startIndex in 0 until n) {
        pool.submit {
            val curHashes = IntArray(maxCombinations)
            val writeBuf = ByteBuffer.allocate(maxWordLength * maxCombinations + 1)
            multisetPermutations(n, maxCombinations, intArrayOf(startIndex)) { indices, len ->
                val lastHash = curHashes[len - 1]
                val hash = lastHash.update(dictArray[indices[len - 1]])
                if (len != maxCombinations) {
                    curHashes[len] = hash
                }
                if (hash in targetHashes) {
                    writeBuf.clear()
                    for (i in 0 until len) {
                        writeBuf.put(dictArray[indices[i]])
                    }
                    writeBuf.put('\n'.toByte())
                    writeBuf.flip()
                    results.write(writeBuf)
                }
            }
        }
    }

    pool.shutdown()
    pool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS)
}