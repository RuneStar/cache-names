package org.runestar.cache.names

import java.io.File

fun main(args: Array<String>) {
    val uniqueNameHashes = File("name-hashes.tsv").readLines().mapTo(HashSet()) { it.split('\t').last().toInt() }

    val individualNames = File("individual-names.tsv").readLines().toSortedSet()
    writeStrings(individualNames, File("individual-names.tsv"))

    val knownNames = ArrayList<String>(individualNames)

    fun String.add() {
        if (hashCode() in uniqueNameHashes) {
            knownNames.add(this)
        }
    }

    for (x in 0..255) {
        for (y in 0..255) {
            "m${x}_$y".add()
            "l${x}_$y".add()
        }
        "emotes,$x".add()
        "emotes_locked,$x".add()
        "tabs,$x".add()
        "orb_xp,$x".add()
        "reset,$x".add()
        "options_radio_buttons,$x".add()
        "zeah_book,$x".add()
        "magicon,$x".add()
        "magicon2,$x".add()
        "combaticons,$x".add()
        "combaticons2,$x".add()
        "combaticons3,$x".add()
        "hitmark,$x".add()
        "peng_emotes,$x".add()
        "staticons,$x".add()
        "staticons2,$x".add()
        "barbassault_icons,$x".add()
        "orb_icon,$x".add()
        "options_icons,$x".add()
        "options_slider,$x".add()
        "ge_icons,$x".add()
        "warning_icons,$x".add()
        "close_buttons,$x".add()
        "side_icons,$x".add()
        "steelborder,$x".add()
        "steelborder2,$x".add()
        "arrow,$x".add()
        "magicoff,$x".add()
        "magicoff2,$x".add()
        "miscgraphics,$x".add()
        "miscgraphics2,$x".add()
        "miscgraphics3,$x".add()
        "prayeroff,$x".add()
        "combatboxes,$x".add()
        "prayeron,$x".add()
        "mapfunction,$x".add()
        "sworddecor,$x".add()
        "wornicons,$x".add()
        "clickcross,$x".add()
        "worldmap_icon,$x".add()
        "bankbuttons,$x".add()
        "scrollbar_v2,$x".add()
        "scrollbar_dragger_v2,$x".add()
        "fast_forward,$x".add()
        "thumbs,$x".add()
        "checkbox,$x".add()
        "open_buttons,$x".add()
        "banktabs,$x".add()
        "options_boxes,$x".add()
    }

    val knownNamesMap = knownNames.associateBy { it.hashCode() }

    val fullNames = File("name-hashes.tsv").readLines().map { line ->
        val hash = line.split('\t').last().toInt()
        val name = knownNamesMap.getOrDefault(hash, "")
        "$line\t$name"
    }
    writeStrings(fullNames, File("names.tsv"))
}