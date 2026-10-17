package com.kglabs28.yapnplay.appengine.features.playrooms.domain

/**
 * What happened after "Yes, join": already a member (opens the room), or
 * a join request now waiting on the room admin (the admin's Waiting List).
 */
sealed interface JoinOutcome {
    data class Joined(val room: PlayRoom) : JoinOutcome
    data class PendingApproval(val roomName: String) : JoinOutcome
}
