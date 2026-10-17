package com.kglabs28.yapnplay.appengine.protocol

import kotlinx.serialization.Serializable

// Moderation: voice-chat reports decided by the people present, shared by
// every game and room. Client → server: ReportPlayer, CastReportVote.
// Server → client: ReportVoteRequested, ReportResolved, PenaltyIssued.

/** Report [reportedUserId] for voice chat in [contextId] (a match or room id). */
@Serializable
data class ReportPlayer(val contextId: String, val reportedUserId: String, val reason: String)

/** Ask everyone present except the two people involved to confirm, before [expiresAt]. */
@Serializable
data class ReportVoteRequested(
    val reportId: String,
    val reporterName: String,
    val reportedName: String,
    /** Epoch millis; after this the prompt closes and counts as no vote. */
    val expiresAt: Long,
)

/** Agree that the reported person did it (true) or not (false). */
@Serializable
data class CastReportVote(val reportId: String, val agree: Boolean)

/** The vote is over: [upheld] true records the incident against the reported person, false against the reporter. */
@Serializable
data class ReportResolved(val reportId: String, val upheld: Boolean, val reportedName: String, val agreed: Int, val required: Int)

/** An incident was recorded against this user: points taken and play blocked until [blockedUntil] (epoch millis). */
@Serializable
data class PenaltyIssued(val pointsDeducted: Int, val blockedUntil: Long)
