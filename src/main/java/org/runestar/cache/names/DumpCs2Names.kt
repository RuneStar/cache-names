package org.runestar.cache.names

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun main(args: Array<String>) {
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
        writeStrings(lines, output.toFile())
    }

    dump(Paths.get("graphic-names.tsv"), 8)
    dump(Paths.get("font-names.tsv"), 13)
    dump(Paths.get("script-names.tsv"), 12)
}

