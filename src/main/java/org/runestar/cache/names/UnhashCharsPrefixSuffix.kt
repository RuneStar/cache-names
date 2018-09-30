package org.runestar.cache.names

import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun unhashChars(
        resultsFile: Path,
        alphabet: String,
        prefix: String,
        suffix: Char,
        targetHashes: IntSet,
        maxCombinations: Int
) {
    val channel = FileChannel.open(resultsFile, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)

    val alphabetArray = alphabet.toByteArray(CHARSET).toSet().toByteArray()
    val prefixBytes = prefix.toByteArray(CHARSET)
    val suffixByte = suffix.toString().toByteArray(CHARSET).first()
    val n = alphabetArray.size

    val pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1)

    for (startIndex in 0 until n) {
        pool.submit {
            val writeBuf = ByteBuffer.allocate(maxCombinations + 1 + prefixBytes.size + 1)
            writeBuf.put(prefixBytes)
            val curHashes = IntArray(maxCombinations)
            curHashes[0] = 0.update(prefixBytes)
            multisetPermutations(n, maxCombinations, intArrayOf(startIndex)) { indices, len ->
                val lastHash = curHashes[len - 1]
                val hash = lastHash.update(alphabetArray[indices[len - 1]])
                if (len != maxCombinations) {
                    curHashes[len] = hash
                }
                if (hash.update(suffixByte) in targetHashes) {
                    writeBuf.clear().position(prefixBytes.size)
                    for (i in 0 until len) {
                        writeBuf.put(alphabetArray[indices[i]])
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