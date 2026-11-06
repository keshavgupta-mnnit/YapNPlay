package com.kglabs28.yapnplay.appengine.games.cyberblast.domain.usecase

import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberBlastMatchRepository
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberFrameData
import kotlinx.coroutines.flow.Flow

/** The moving board, about 20 frames a second. */
class ObserveCyberFramesUseCase(private val repository: CyberBlastMatchRepository) {
    operator fun invoke(): Flow<CyberFrameData> = repository.observeFrames()
}
