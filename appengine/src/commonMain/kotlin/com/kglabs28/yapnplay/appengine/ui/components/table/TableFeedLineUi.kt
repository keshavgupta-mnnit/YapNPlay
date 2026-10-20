package com.kglabs28.yapnplay.appengine.ui.components.table

/** One thing that happened in a game. [quiet]: only in the history, no snackbar (dice rolls, bids). */
data class TableFeedLineUi(val id: Long, val text: String, val tone: TableFeedTone, val quiet: Boolean = false, val spans: List<TableFeedSpan> = emptyList())
