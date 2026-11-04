package com.kglabs28.yapnplay.appengine.games.doodleking.domain

/** One line of the turn's feed (see [DoodleFeedKind]). */
data class DoodleFeedLine(val kind: DoodleFeedKind, val seat: Int, val text: String?, val points: Int)

enum class DoodleFeedKind { GUESS, CLOSE, CORRECT, CHAT, PASSED, NO_DRAWING, SKIPPED, TIME_UP }
