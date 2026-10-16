package com.kglabs28.yapnplay.appengine.features.playrooms.data

import com.kglabs28.yapnplay.appengine.features.playrooms.data.remote.dto.PlayRoomDto
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoom
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoomIcon

/** Wire DTO to domain. Single place: backend shape changes land here. */
fun PlayRoomDto.toDomain(): PlayRoom = PlayRoom(
    id = roomId,
    name = name,
    code = code,
    memberCount = memberCount,
    liveCount = liveCount,
    isOwner = isOwner,
    icon = PlayRoomIcon.entries.firstOrNull { it.name.equals(icon, ignoreCase = true) } ?: PlayRoomIcon.GROUP,
)
