package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match

import com.kglabs28.yapnplay.appengine.ui.components.table.TableFeedLineUi

/**
 * The table history of each City Royale match, kept for the app's life,
 * so coming back to a match (a rejoin, the screen left and reopened) shows
 * everything that happened, not an empty list. Newest first.
 */
class CbHistoryCache {
    private val byMatch = mutableMapOf<String, List<TableFeedLineUi>>()

    fun get(matchId: String): List<TableFeedLineUi> = byMatch[matchId].orEmpty()

    fun put(matchId: String, lines: List<TableFeedLineUi>) {
        // One match's history at a time is plenty: an older one is dropped.
        byMatch.keys.retainAll { it == matchId }
        byMatch[matchId] = lines
    }
}
