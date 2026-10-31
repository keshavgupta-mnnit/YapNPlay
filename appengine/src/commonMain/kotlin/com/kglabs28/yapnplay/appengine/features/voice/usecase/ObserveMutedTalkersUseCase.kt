package com.kglabs28.yapnplay.appengine.features.voice.usecase

import com.kglabs28.yapnplay.appengine.core.network.MatchChannel
import kotlinx.coroutines.flow.Flow

/** Who at a match table is talking with their mic off (a grey glow on them). */
class ObserveMutedTalkersUseCase(private val match: MatchChannel) {
    operator fun invoke(matchId: String): Flow<Set<String>> = match.mutedTalkers(matchId)
}
