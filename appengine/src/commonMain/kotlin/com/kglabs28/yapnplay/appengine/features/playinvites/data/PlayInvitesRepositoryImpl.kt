package com.kglabs28.yapnplay.appengine.features.playinvites.data

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.network.RealtimeGateway
import com.kglabs28.yapnplay.appengine.features.nearby.repository.NearbyRepository
import com.kglabs28.yapnplay.appengine.features.playinvites.domain.PlayInviteOffer
import com.kglabs28.yapnplay.appengine.features.playinvites.repository.PlayInvitesRepository
import com.kglabs28.yapnplay.appengine.features.playrooms.repository.PlayRoomsRepository
import com.kglabs28.yapnplay.appengine.protocol.PlayInvite
import com.kglabs28.yapnplay.appengine.protocol.transport.Channel
import com.kglabs28.yapnplay.appengine.protocol.transport.MessageType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Online play-now invites arrive as `PlayInvite` pushes on the ROOM
 * channel; nearby ones from the Nearby session. A nearby invite wins (the
 * person is right there); otherwise the newest online one is asked.
 */
class PlayInvitesRepositoryImpl(
    private val realtime: RealtimeGateway,
    private val rooms: PlayRoomsRepository,
    private val nearby: NearbyRepository,
    scope: CoroutineScope,
) : PlayInvitesRepository {
    private val online = MutableStateFlow<PlayInviteOffer.Online?>(null)

    init {
        scope.launch {
            realtime.stream(Channel.ROOM)
                .filter { it.type == MessageType.PLAY_INVITE }
                .map { realtime.decode(it, PlayInvite.serializer()) }
                .collect { invite ->
                    online.value = PlayInviteOffer.Online(invite.inviteId, invite.roomId, invite.roomName, invite.from.displayName, invite.from.avatarId)
                }
        }
    }

    override val offer: Flow<PlayInviteOffer?> = combine(online, nearby.state) { remote, local ->
        local.incoming?.let { PlayInviteOffer.Nearby(it.roomName, it.from.name, it.from.avatarId) } ?: remote
    }

    override suspend fun answer(offer: PlayInviteOffer, accept: Boolean): AppResult<String?> = when (offer) {
        is PlayInviteOffer.Online -> {
            online.value = null
            if (accept) {
                when (val result = rooms.acceptInvite(offer.inviteId)) {
                    is AppResult.Success -> AppResult.Success(result.data.id)
                    is AppResult.Failure -> result
                }
            } else {
                when (val result = rooms.declineInvite(offer.inviteId)) {
                    is AppResult.Success -> AppResult.Success(null)
                    is AppResult.Failure -> result
                }
            }
        }
        is PlayInviteOffer.Nearby -> {
            if (accept) nearby.acceptInvite() else nearby.declineInvite()
            AppResult.Success(null)
        }
    }
}
