package com.kglabs28.yapnplay.appengine.games.base.play

/** One option the bot weighed: its gain, cost, risk and a short note. */
data class BotTraceOption(
    val option: String,
    val gain: Double,
    val cost: Double,
    val risk: Double,
    val note: String,
)
