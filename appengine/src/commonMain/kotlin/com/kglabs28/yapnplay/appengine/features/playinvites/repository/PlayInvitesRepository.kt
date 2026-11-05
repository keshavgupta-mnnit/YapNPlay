package com.kglabs28.yapnplay.appengine.features.playinvites.repository

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.playinvites.domain.PlayInviteOffer
import kotlinx.coroutines.flow.Flow

/** Play-now invites waiting for the user's answer, online and nearby. */
interface PlayInvitesRepository {
    /** The invite to ask about now (the newest), or null. */
    val offer: Flow<PlayInviteOffer?>

    /**
     * Answers [offer]. Accepting an online invite joins at once: the room id to
     * open. A nearby one joins once the host lets the phone in (null here).
     */
    suspend fun answer(offer: PlayInviteOffer, accept: Boolean): AppResult<String?>
}
