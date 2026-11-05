package com.kglabs28.yapnplay.appengine.core.ui

/** Offline Nearby play and the play-now invites (online friends and phones nearby). */
object NearbyStrings {
    const val defaultName = "Player"
    fun partyName(host: String) = "$host's Party"

    // ── Add Friends in a Quick Play party ──
    const val addTitleOnline = "Add friends"
    const val addTitleNearby = "Add players nearby"
    const val addEmptyOnline = "None of your friends are online right now."
    const val addEmptyNearby = "Looking for phones around you… Keep them close, with YapNPlay open."
    const val addButton = "Add"
    const val statusOnline = "Online"
    const val statusInRoom = "In a room"
    const val statusPlaying = "Playing a game"
    const val statusOffline = "Offline"
    const val statusNearby = "Nearby"
    const val statusInviting = "Inviting…"
    const val statusInvited = "Invited"
    const val statusJoined = "In this party"
    const val statusDeclined = "Said no"
    const val addFailed = "Couldn't send the invite. Try again."

    // ── The invite popup ──
    const val inviteTitle = "Play now?"
    fun inviteBody(from: String, room: String) = "$from invited you to $room."
    fun inviteBodyNearby(from: String) = "$from, right next to you, invited you to play offline."
    const val inviteAccept = "JOIN"
    const val inviteDecline = "Not now"
    const val inviteFailed = "That invite is no longer open."

    // ── Problems ──
    const val readyTitle = "GET READY FOR "
    const val readyTitleAccent = "NEARBY"
    fun readyProgress(done: Int, total: Int) = if (done == total) "All set! Jump in" else "$done of $total ready · tap a cross to fix it"
    const val checkStart = "Start Nearby"
    const val checkStartRetry = "It didn't start. Tap to try again."
    const val checkPermission = "Nearby devices permission"
    const val checkBluetooth = "Bluetooth on"
    const val checkWifi = "Wi-Fi on"
    const val checkLocation = "Location on"
    const val readyEnterLobby = "Enter Lobby"
    const val problemJoinTitle = "Couldn't join"
    const val problemJoinBody = "That party couldn't be reached or is full."
    const val problemHostLeftTitle = "Party ended"
    const val problemHostLeftBody = "The host ended the party or went out of range."
    const val problemVersionTitle = "Update needed"
    const val problemVersionBody = "Both phones need the same YapNPlay version to play together."
    const val permissionTitle = "Nearby permission needed"
    const val permissionBody = "YapNPlay needs Nearby devices (Bluetooth and Wi-Fi) to find phones around you. Allow it in Settings."
    const val openSettings = "Open Settings"

    // ── Offline ──
    const val needsInternet = "Needs internet"
    const val offlineBanner = "You're offline: only Nearby play works right now"
}
