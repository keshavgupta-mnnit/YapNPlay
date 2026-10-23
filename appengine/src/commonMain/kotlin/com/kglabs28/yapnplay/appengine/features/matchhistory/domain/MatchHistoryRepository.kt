package com.kglabs28.yapnplay.appengine.features.matchhistory.domain

import com.kglabs28.yapnplay.appengine.core.common.AppResult

/** The user's latest finished matches, newest first. */
interface MatchHistoryRepository {
    suspend fun history(): AppResult<List<PastMatch>>
}
