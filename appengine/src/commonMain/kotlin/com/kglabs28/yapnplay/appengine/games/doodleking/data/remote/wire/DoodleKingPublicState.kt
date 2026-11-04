package com.kglabs28.yapnplay.appengine.games.doodleking.data.remote.wire

import kotlinx.serialization.Serializable

/** Doodle King's `MatchSnapshot.publicState` (the backend's shape, as this viewer may see it). */
@Serializable
data class DoodleKingPublicState(
    val phase: String,
    val round: Int,
    val rounds: Int,
    val level: String,
    val drawerSeat: Int,
    val phaseEndsAt: Long,
    val drawSeconds: Int,
    val word: String? = null,
    val options: List<String> = emptyList(),
    val hint: String = "",
    val seats: List<DoodleSeatState> = emptyList(),
    val feed: List<DoodleFeedEntry> = emptyList(),
    val drawingId: Int = 0,
    val strokeCount: Int = 0,
)

@Serializable
data class DoodleSeatState(
    val seat: Int,
    val score: Int,
    val guessed: Boolean = false,
    val turnPoints: Int = 0,
    val removed: Boolean = false,
)

@Serializable
data class DoodleFeedEntry(
    val kind: String,
    val seat: Int,
    val text: String? = null,
    val points: Int = 0,
)
