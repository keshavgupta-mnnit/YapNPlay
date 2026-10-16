package com.kglabs28.yapnplay.appengine.features.profile.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Wire shape of a friend entry. Field names are the backend contract
 * (see docs/backend-api-contract.md) — do not rename casually.
 */
@Serializable
data class FriendDto(
    @SerialName("user_id") val userId: String,
    @SerialName("display_name") val displayName: String,
    val online: Boolean,
)
