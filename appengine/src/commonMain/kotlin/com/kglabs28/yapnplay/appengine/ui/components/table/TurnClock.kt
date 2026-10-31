package com.kglabs28.yapnplay.appengine.ui.components.table

/** How much of a turn is left, from its deadline: the seat's timer ring draws this. */
object TurnClock {
    /** 1 → 0 as [nowMillis] runs to [endsAtMillis] over a [totalMs] turn. */
    fun fraction(endsAtMillis: Long, totalMs: Long, nowMillis: Long): Float =
        if (totalMs <= 0) 0f else ((endsAtMillis - nowMillis).toFloat() / totalMs).coerceIn(0f, 1f)

    /** Milliseconds left until [endsAtMillis]. */
    fun left(endsAtMillis: Long, nowMillis: Long): Long = (endsAtMillis - nowMillis).coerceAtLeast(0)
}
