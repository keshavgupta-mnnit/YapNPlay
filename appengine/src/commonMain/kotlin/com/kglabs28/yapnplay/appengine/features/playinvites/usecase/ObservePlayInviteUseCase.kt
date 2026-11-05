package com.kglabs28.yapnplay.appengine.features.playinvites.usecase

import com.kglabs28.yapnplay.appengine.features.playinvites.domain.PlayInviteOffer
import com.kglabs28.yapnplay.appengine.features.playinvites.repository.PlayInvitesRepository
import kotlinx.coroutines.flow.Flow

/** The play-now invite to ask about (online friend or phone nearby), or null. */
class ObservePlayInviteUseCase(private val repository: PlayInvitesRepository) {
    operator fun invoke(): Flow<PlayInviteOffer?> = repository.offer
}
