package org.runestar.cache.names

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun main() {
    val namesFile = Paths.get("names.tsv")

    fun dump(
            output: Path,
            index: Int
    ) {
        val lines = ArrayList<String>()
        Files.lines(namesFile).forEach { line ->
            val split = line.split('\t')
            val x = split[0].toInt()
            if (x != index) return@forEach
            val s = split.last()
            if (s.isEmpty()) return@forEach
            val id = split[1].toInt()
            lines.add("$id\t$s")
        }
        Files.write(output, lines)
    }

    dump(Paths.get("graphic-names.tsv"), 8)
    dump(Paths.get("script-names.tsv"), 12)
}

