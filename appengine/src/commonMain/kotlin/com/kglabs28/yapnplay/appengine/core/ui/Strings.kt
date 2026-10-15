package com.kglabs28.yapnplay.appengine.core.ui

/**
 * Every user-visible word in the app. UI code reads from here —
 * never inline a word where the user can see it.
 */
object Strings {
    const val appName = "YapNPlay"

    // Home
    const val homeTitle = "YapNPlay"
    const val homeEnterGameZone = "ENTER GAME ZONE"
    const val homePlayWithFriends = "PLAY WITH FRIENDS"
    const val homeQuickPlay = "QUICK PLAY"
    const val homeHostLobby = "HOST LOBBY"
    const val homeOpenGallery = "Component gallery"
    const val homeOpenAbout = "About"

    // Gallery
    const val galleryTitle = "Component gallery"
    const val galleryShowAll = "All"
    const val galleryDisabled = "Disabled"
    const val gallerySampleName = "Neon"
    const val gallerySampleMember = "NEON_VIPER"
    const val galleryLongMemberName = "A very long member name that must truncate"
    const val gallerySampleCode = "AB12"
    const val galleryCopied = "Room code copied"
    const val galleryReconnecting = "Reconnecting…"
    const val galleryStatusReady = "Ready"
    const val galleryStatusNotReady = "Not Ready"
    const val galleryLeaveTitle = "Leave room?"
    const val galleryLeaveBody = "Your seat will be freed for someone else."
    const val galleryLeaveConfirm = "Leave"
    const val galleryLeaveDismiss = "Stay"

    // About
    const val aboutTitle = "About"
    const val aboutRetry = "Retry"
    const val aboutLoading = "Loading…"
    fun aboutInfo(appName: String, version: String, contract: String): String =
        "$appName $version · contract $contract"

    // Common actions
    const val actionOk = "OK"
    const val actionCancel = "Cancel"
    const val actionRetry = "Retry"
    const val actionSettings = "Settings"
    const val actionChat = "Chat"
    const val actionRollDice = "Roll Dice"
    const val actionAutoplay = "Autoplay"
    const val actionInviteFriends = "Invite friends"
    const val actionLeaveRoom = "Leave room"

    // Hints
    const val hintDisplayName = "Display name"
    const val hintRoomCode = "XXXXXX"
}
