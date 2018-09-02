package org.runestar.cache.names

import org.eclipse.collections.impl.factory.primitive.IntSets
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun unhashDict(
        dict: Set<String>,
        prefix: String,
        suffix: Char,
        targetHashes: Set<Int>,
        maxCombinations: Int
) {
    Files.deleteIfExists(RESULTS_FILE)
    val channel = FileChannel.open(RESULTS_FILE, StandardOpenOption.APPEND, StandardOpenOption.CREATE)

    val targetHashesPrimitive = IntSets.immutable.of(*targetHashes.toIntArray())
    val prefixBytes = prefix.toByteArray(CHARSET)
    val suffixByte = suffix.toString().toByteArray(CHARSET).first()
    val dictArray = dict.map { it.toByteArray(CHARSET) }.toTypedArray()
    val maxWordLength = dictArray.maxBy { it.size }!!.size
    val n = dictArray.size

    val pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1)

    for (startIndex in 0 until n) {
        pool.submit {
            val curHashes = IntArray(maxCombinations)
            curHashes[0] = 0.update(prefixBytes)
            val writeBuf = ByteBuffer.allocate(maxWordLength * maxCombinations + 2 + prefixBytes.size)
            writeBuf.put(prefixBytes)
            multisetPermutations(n, maxCombinations, intArrayOf(startIndex)) { indices, len ->
                val lastHash = curHashes[len - 1]
                val hash = lastHash.update(dictArray[indices[len - 1]])
                if (len != maxCombinations) {
                    curHashes[len] = hash
                }
                if (hash.update(suffixByte) in targetHashesPrimitive) {
                    writeBuf.clear().position(prefixBytes.size)
                    for (i in 0 until len) {
                        writeBuf.put(dictArray[indices[i]])
                    }
                    writeBuf.put(suffixByte).put('\n'.toByte())
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