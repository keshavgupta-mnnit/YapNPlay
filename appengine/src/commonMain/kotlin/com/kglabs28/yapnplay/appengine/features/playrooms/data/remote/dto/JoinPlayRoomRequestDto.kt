package com.kglabs28.yapnplay.appengine.features.playrooms.data.remote.dto

import kotlinx.serialization.Serializable

/** Body of `POST /v1/rooms/join`. */
@Serializable
data class JoinPlayRoomRequestDto(
    val code: String,
)
