package com.kglabs28.yapnplay.appengine.features.profile.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Wire shape of a user profile. Field names are the backend contract
 * (see docs/backend-api-contract.md) — do not rename casually.
 */
@Serializable
data class ProfileDto(
    @SerialName("user_id") val userId: String,
    @SerialName("display_name") val displayName: String,
    val level: Int,
    val xp: Int,
    @SerialName("xp_max") val xpMax: Int,
    val online: Boolean,
    val coins: Int,
    val gems: Int,
)
