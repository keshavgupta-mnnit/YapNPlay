package com.kglabs28.yapnplay.appengine.games.cyberblast.domain.usecase

import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberBlastMatchRepository
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberUpdate
import kotlinx.coroutines.flow.Flow

/** The match's snapshots (seats, votes, the board as it stood). */
class ObserveCyberMatchUseCase(private val repository: CyberBlastMatchRepository) {
    operator fun invoke(matchId: String): Flow<CyberUpdate> = repository.observeMatch(matchId)
}
