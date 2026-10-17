package com.kglabs28.yapnplay.appengine.features.playrooms.data

import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoom
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoomIcon
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoomPreview
import com.kglabs28.yapnplay.appengine.protocol.RoomPreview
import com.kglabs28.yapnplay.appengine.protocol.RoomSummary

/** Contract type to domain. Single place: contract changes land here. */
fun RoomSummary.toDomain(): PlayRoom = PlayRoom(
    id = roomId,
    name = name,
    code = code,
    memberCount = memberCount,
    liveCount = onlineCount,
    isLeader = isLeader,
    icon = PlayRoomIcon.entries.firstOrNull { it.name.equals(icon, ignoreCase = true) } ?: PlayRoomIcon.GROUP,
)

fun RoomPreview.toDomain(): PlayRoomPreview = PlayRoomPreview(
    roomId = roomId,
    code = code,
    name = name,
    description = description.orEmpty(),
    memberCount = memberCount,
    onlineCount = onlineCount,
)
