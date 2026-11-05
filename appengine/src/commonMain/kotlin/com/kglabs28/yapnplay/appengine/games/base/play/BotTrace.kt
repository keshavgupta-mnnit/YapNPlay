package com.kglabs28.yapnplay.appengine.games.base.play

import com.kglabs28.yapnplay.appengine.core.common.AppLog

/**
 * The last [capacity] bot decisions of one match, for "why did the bot do
 * that" on the admin page; each is also logged at debug level. Cheap: a
 * small ring of short strings, nothing is saved.
 */
class BotTrace(private val capacity: Int = DEFAULT_CAPACITY) {
    private val entries = ArrayDeque<BotTraceEntry>()

    @Synchronized
    fun record(decision: String, chosen: String, top: List<Pair<String, Evaluation>>, riskWeight: Double) {
        val entry = BotTraceEntry(
            decision = decision,
            chosen = chosen,
            riskWeight = riskWeight,
            options = top.map { (label, e) -> BotTraceOption(label, e.gain, e.cost, e.risk, e.note) },
        )
        AppLog.d(LOG_TAG, "Bot $decision: $chosen (${entry.options})")
        entries.addLast(entry)
        while (entries.size > capacity) entries.removeFirst()
    }

    /** Newest first. */
    @Synchronized
    fun recent(): List<BotTraceEntry> = entries.reversed()

    private companion object {
        const val DEFAULT_CAPACITY = 50
        const val LOG_TAG = "BotTrace"
    }
}

data class BotTraceEntry(
    val decision: String,
    val chosen: String,
    val riskWeight: Double,
    val options: List<BotTraceOption>,
)

data class BotTraceOption(
    val option: String,
    val gain: Double,
    val cost: Double,
    val risk: Double,
    val note: String,
)
