package com.kglabs28.yapnplay.appengine.games.cyberblast.domain.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberBlastMatchRepository
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberCommand

/** The user's steering, bombs and table requests. */
class SendCyberCommandUseCase(private val repository: CyberBlastMatchRepository) {
    suspend operator fun invoke(matchId: String, command: CyberCommand): AppResult<Unit> = repository.send(matchId, command)
}
