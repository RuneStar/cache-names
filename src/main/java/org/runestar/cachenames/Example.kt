package org.runestar.cachenames

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.Duration
import java.time.Instant
import java.util.TreeSet
import java.util.concurrent.Executors

fun main() {
    val start = Instant.now()
    val results = Files.newOutputStream(
            Paths.get("results.tsv"),
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING,
            StandardOpenOption.WRITE
    )
    val pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 2)

    val hashes = unknownHashes(12)

    val dict = TreeSet<String>()

    // songs
    NAMES.mapNotNull { n -> n.name?.takeIf { n.archive == 6 }?.replace(' ', '_') }.forEach { name ->

    }

    // graphics
    NAMES.mapNotNull { n -> n.name?.takeIf { n.archive == 8 }?.substringBeforeLast(',') }.forEach { name ->

    }

    // scripts
    NAMES.mapNotNull { n -> n.name?.takeIf { n.archive == 12 && it.toIntOrNull() == null }?.substringAfter(',')?.dropLast(1) }.forEach { name ->

    }

    // anims
    Files.readAllLines(Paths.get("dict", "anims.txt")).forEach { name ->

    }

    // common words
    dict.addAll(Files.readAllLines(Paths.get("dict", "google-10000-english-no-swears.txt")).take(500))

    println(dict)
    println(dict.size)

    unhash(results, dict, "[proc,", "]", '_', hashes, 3, pool)

    pool.shutdownAwait()
    results.close()
    println(Duration.between(start, Instant.now()))
    writeLines(Paths.get("results.tsv"), Files.readAllLines(Paths.get("results.tsv")).sortedBy { it.length }.map { "$it\t\t${it.hashCode()}" })
}