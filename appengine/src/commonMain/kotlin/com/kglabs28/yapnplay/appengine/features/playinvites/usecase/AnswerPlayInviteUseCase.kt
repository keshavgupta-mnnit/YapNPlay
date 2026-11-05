package com.kglabs28.yapnplay.appengine.features.playinvites.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.playinvites.domain.PlayInviteOffer
import com.kglabs28.yapnplay.appengine.features.playinvites.repository.PlayInvitesRepository

/** Joins (online: the room id to open) or turns down a play-now invite. */
class AnswerPlayInviteUseCase(private val repository: PlayInvitesRepository) {
    suspend operator fun invoke(offer: PlayInviteOffer, accept: Boolean): AppResult<String?> = repository.answer(offer, accept)
}
