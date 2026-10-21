package com.kglabs28.yapnplay.appengine.ui.components.table

/** A highlighted part of a line, [start] until [end]; [colour] is the player's colour index (PLAYER only). */
data class TableFeedSpan(val start: Int, val end: Int, val kind: TableFeedSpanKind, val colour: Int = -1)
