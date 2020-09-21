package org.runestar.cachenames

import java.nio.file.Path

fun main() {
    fun dump(output: Path, archive: Int) {
        writeLines(output, NAMES.filter { it.archive == archive && it.name != null }.map { "${it.group}\t${it.name}" })
    }

    dump(Path.of("graphic-names.tsv"), 8)
    dump(Path.of("script-names.tsv"), 12)
}

