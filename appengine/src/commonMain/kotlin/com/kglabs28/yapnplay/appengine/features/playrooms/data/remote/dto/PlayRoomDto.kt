package com.kglabs28.yapnplay.appengine.features.playrooms.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Wire shape of one room in `GET /v1/rooms/mine` and the
 * `POST /v1/rooms/join` reply. Field names are the backend contract
 * (see docs/backend-api-contract.md) — do not rename casually.
 */
@Serializable
data class PlayRoomDto(
    @SerialName("room_id") val roomId: String,
    val name: String,
    val code: String,
    @SerialName("member_count") val memberCount: Int,
    @SerialName("live_count") val liveCount: Int,
    @SerialName("is_owner") val isOwner: Boolean,
    val icon: String,
)
