package com.kglabs28.yapnplay.appengine.features.playroomlobby.data

import com.kglabs28.yapnplay.appengine.features.playroomlobby.domain.LobbyMember
import com.kglabs28.yapnplay.appengine.features.playroomlobby.domain.LobbyRoom
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoomIcon
import com.kglabs28.yapnplay.appengine.protocol.Presence
import com.kglabs28.yapnplay.appengine.protocol.RoomSnapshot
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.milliseconds

/**
 * Contract snapshot to domain. The countdown deadline is server time, so
 * it is re-based on this device clock: deadline - serverTime + [now].
 */
fun RoomSnapshot.toLobbyRoom(now: Instant): LobbyRoom = LobbyRoom(
    id = room.id,
    name = room.name,
    description = room.description.orEmpty(),
    icon = PlayRoomIcon.fromKey(room.icon),
    code = room.code,
    members = room.members.map { member ->
        LobbyMember(
            userId = member.userId,
            name = member.displayName,
            online = member.presence == Presence.ONLINE,
            ready = member.ready,
            watching = member.watching,
            micOn = member.micOn,
            speakerOn = member.speakerOn,
            isFriend = member.isFriend,
            isLeader = member.userId == room.leaderId,
            isAdmin = member.userId == room.adminId,
        )
    },
    selectedGameId = room.selectedGame,
    houseRules = room.houseRules,
    botFill = room.botFill,
    countdownEndsAt = room.countdownDeadline?.let { now + (it - serverTime).milliseconds },
)
