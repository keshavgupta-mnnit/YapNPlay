package com.kglabs28.yapnplay.appengine.games.cyberblast.domain.usecase

import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberBlastMatchRepository
import kotlinx.coroutines.flow.Flow

/** Who at the table is talking right now. */
class ObserveCyberSpeakingUseCase(private val repository: CyberBlastMatchRepository) {
    operator fun invoke(matchId: String): Flow<Set<String>> = repository.observeSpeaking(matchId)
}
