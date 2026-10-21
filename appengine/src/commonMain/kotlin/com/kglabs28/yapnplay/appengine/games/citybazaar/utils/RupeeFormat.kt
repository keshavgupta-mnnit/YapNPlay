package com.kglabs28.yapnplay.appengine.games.citybazaar.utils

import kotlin.math.abs

/** Indian digit grouping: 25000 → "25,000", 125000 → "1,25,000". */
object RupeeFormat {
    fun digits(amount: Int): String = digits(amount.toLong())

    fun digits(amount: Long): String {
        val text = abs(amount).toString()
        if (text.length <= LAST_GROUP) return text
        val head = text.dropLast(LAST_GROUP)
        val tail = text.takeLast(LAST_GROUP)
        val pairs = head.reversed().chunked(PAIR).joinToString(",").reversed()
        return "$pairs,$tail"
    }

    private const val LAST_GROUP = 3
    private const val PAIR = 2
}
