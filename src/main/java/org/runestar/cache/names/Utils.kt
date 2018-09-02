package org.runestar.cache.names

import java.io.File
import java.nio.channels.FileChannel
import java.nio.file.*

typealias Hash = Int

fun Hash.update(bytes: ByteArray): Hash = bytes.fold(this) { acc, byte -> acc.update(byte) }

@Suppress("NOTHING_TO_INLINE")
inline fun Hash.update(byte: Byte): Hash = this * 31 + byte

@JvmField val CHARSET = charset("windows-1252")

@JvmField val RESULTS_FILE: Path = Paths.get("results.tsv")

fun openResultsChannel(): FileChannel {
    Files.deleteIfExists(RESULTS_FILE)
    return FileChannel.open(RESULTS_FILE, StandardOpenOption.APPEND, StandardOpenOption.CREATE)
}

fun writeStrings(strings: Iterable<*>, file: File) {
    val sb = StringBuilder()
    for (s in strings) {
        sb.appendln(s)
    }
    Files.write(file.toPath(), sb.toString().toByteArray(), StandardOpenOption.CREATE)
}

fun unknownHashes(index: Int): IntSet {
    val set = HashSet<Int>()
    File("names.tsv").forEachLine { line ->
        val split = line.split('\t')
        val idx = split.first().toInt()
        if (idx != index) return@forEachLine
        val name = split.last()
        if (!name.isEmpty()) return@forEachLine
        set.add(split[split.lastIndex - 1].toInt())
    }
    return IntSet.of(set)
}

fun unknownHashes(): IntSet {
    val set = HashSet<Int>()
    File("names.tsv").forEachLine { line ->
        val split = line.split('\t')
        val name = split.last()
        if (!name.isEmpty()) return@forEachLine
        set.add(split[split.lastIndex - 1].toInt())
    }
    return IntSet.of(set)
}

/*
   // seers_texture ?

    // #
    // the doors of dinh
    // fire in the deep
    // ice and fire
    // night of the vampyre
    // tempest
    // preservation
    // preserved
    // fossilized
    // lagoon

    // http://oldschoolrunescape.wikia.com/wiki/Unlisted_music_tracks
    // http://oldschoolrunescape.wikia.com/wiki/Massacre

    // woodenfloor_lig...
    // mirror_texture
    // 2x lava_animated_...

    //[.. model_dynamiczoom]
    //[proc,on_mobile]

    // 1787935731
 */