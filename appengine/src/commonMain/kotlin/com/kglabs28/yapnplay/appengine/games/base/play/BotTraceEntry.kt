package com.kglabs28.yapnplay.appengine.games.base.play

/** One recorded bot decision: what was decided, the pick, and the top options. */
data class BotTraceEntry(
    val decision: String,
    val chosen: String,
    val riskWeight: Double,
    val options: List<BotTraceOption>,
)
