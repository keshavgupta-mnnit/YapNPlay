package com.kglabs28.yapnplay.appengine.games.base.play

/**
 * The last [capacity] bot decisions of one match, for "why did the bot do
 * that" (the server shows them on its admin page; the app's copy keeps
 * them in memory only). Cheap: a small ring of short entries.
 */
class BotTrace(private val capacity: Int = DEFAULT_CAPACITY) {
    private val entries = ArrayDeque<BotTraceEntry>()

    fun record(decision: String, chosen: String, top: List<Pair<String, Evaluation>>, riskWeight: Double) {
        val entry = BotTraceEntry(
            decision = decision,
            chosen = chosen,
            riskWeight = riskWeight,
            options = top.map { (label, e) -> BotTraceOption(label, e.gain, e.cost, e.risk, e.note) },
        )
        entries.addLast(entry)
        while (entries.size > capacity) entries.removeFirst()
    }

    /** Newest first. */
    fun recent(): List<BotTraceEntry> = entries.reversed()

    private companion object {
        const val DEFAULT_CAPACITY = 50
    }
}
