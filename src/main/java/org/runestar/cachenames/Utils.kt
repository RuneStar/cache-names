package org.runestar.cachenames

import java.io.OutputStream
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit

typealias Hash = Int

fun Hash.update(bytes: ByteArray): Hash = bytes.fold(this) { acc, byte -> acc.update(byte) }

fun Hash.update(byte: Byte): Hash = this * 31 + byte

@JvmField val CHARSET = charset("windows-1252")

fun Char.toByte(charset: Charset): Byte = toString().toByteArray(charset).single()

fun ByteArray.distinctArray(): ByteArray = toSet().toByteArray()

fun ExecutorService.shutdownAwait() {
    shutdown()
    awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS)
}

fun OutputStream.write(b: Byte) = write(b.toInt())

data class NameEntry(val archive: Int, val group: Int, val file: Int, val hash: Int, val name: String?)

private fun readNames(): List<NameEntry> {
    return Files.readAllLines(Paths.get("names.tsv")).map { line ->
        val split = line.split('\t')
        NameEntry(split[0].toInt(), split[1].toInt(), split[2].toInt(), split[3].toInt(), split[4].takeUnless { it.isEmpty() })
    }
}

val NAMES by lazy { readNames() }

fun unknownHashes(archive: Int) = IntSet.of(NAMES.filter { it.archive == archive && it.name == null }.mapTo(HashSet()) { it.hash })

fun writeLines(path: Path, lines: Iterable<String>) {
    Files.newBufferedWriter(path).use { writer ->
        for (line in lines) {
            writer.write(line)
            writer.write('\n'.toInt())
        }
    }
}