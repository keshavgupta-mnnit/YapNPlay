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

    // Party Play
    const val partyLobbyHub = "Lobby Hub"
    const val partyPlay = "PARTY PLAY"
    const val partyCreateNewRoom = "CREATE NEW ROOM"
    const val partyEnterCode = "ENTER 6-DIGIT CODE"
    const val partyJoin = "JOIN"
    const val partyOnlineFriends = "ONLINE FRIENDS"
    const val partySeeAll = "SEE ALL"
    const val partyOnline = "ONLINE"
    const val partyParty = "PARTY"
    const val partyOn = "ON"

    // Lobby
    const val lobbyRoomPin = "ROOM PIN"
    const val lobbyCopy = "COPY"
    const val lobbyPlayersInRoom = "Player(s) in room"
    const val lobbyReady = "READY"
    const val lobbyInvite = "INVITE"
    const val lobbyRoomSettings = "ROOM SETTINGS"
    const val lobbyGameMode = "Game Mode"
    const val lobbyLudoClassic = "Ludo Classic"
    const val lobbyChange = "Change"
    const val lobbyPublicPrivate = "Public / Private"
    const val lobbyVoiceChat = "Voice Chat"
    const val lobbyMaxPlayers = "Max Player"
    const val lobbyGameStartsIn = "Game starts in"
    const val lobbyStartMatch = "START MATCH"
    const val lobbyWaiting = "WAITING"
    const val lobbyYou = "YOU"
    const val lobbyEditName = "EDIT NAME"
    const val lobbyLevel = "Level"
    fun lobbyPlayers(have: Int, total: Int): String = "Players $have/$total"

    // Royal Ludo board
    const val boardTitle = "ROYAL LUDO"
    const val boardTurnTimer = "Turn-timer"
    const val boardMatchHistory = "Match History"
}
