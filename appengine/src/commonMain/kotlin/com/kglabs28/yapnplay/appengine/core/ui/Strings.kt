package com.kglabs28.yapnplay.appengine.core.ui

/**
 * Every user-visible word in the app. UI code reads from here —
 * never inline a word where the user can see it.
 */
object Strings {
    const val appName = "YapNPlay"

    const val homeTitle = "YapNPlay"
    const val homeUserName = "Keshav"
    const val homeOnlineStatus = "Online"
    const val homeLevelText = "Level"
    const val homeLevelValue = "12"
    const val homeLevelExp = "2450 / 3000"
    const val homeLogoTitle = "YapNPlay"
    const val homeLogoMark = "🎮"
    const val homeLogoPart1 = "Yap"
    const val homeLogoPart2 = "NPlay"
    const val homeLogoTagline = "Talk More. Play Your Way."
    fun topBarLevel(level: String) = "Lv $level"
    fun topBarXp(xp: String, next: String) = "$xp/$next XP"
    fun dailyBonus(coins: String) = "+$coins"
    const val dailyBonusTitle = "DAILY BONUS"
    const val dailyBonusBody = "Welcome back! Here are your coins for today. Come back tomorrow for more."
    const val dailyBonusCollect = "COLLECT"
    const val homeCoinBalance = "$ 4,250 +"
    const val homeDiamondBalance = "120 +"
    const val homeEnterGameZoneTitlePart1 = "ENTER "
    const val homeEnterGameZoneTitlePart2 = "GAME ZONE"
    const val homeEnterGameZoneSubtitle = "Jump into a game, meet new people, and create unforgettable moments!"
    const val homeFeaturePopularGames = "Popular Games"
    const val homeFeaturePopularGamesSub = "Ludo, UNO & More"
    const val homeFeatureHouseRules = "Custom House Rules"
    const val homeFeatureHouseRulesSub = "Play Your Way"
    const val homeFeatureRealPeople = "Real People"
    const val homeFeatureRealPeopleSub = "Chat, Laugh, Repeat"
    const val homeQuickPlay = "QUICK PLAY"
    const val homeHostLobby = "HOST LOBBY"
    const val homeNavHome = "Home"
    const val homeNavGames = "Games"
    const val homeNavRooms = "Rooms"

    const val connectionConnecting = "Connecting…"
    const val connectionConnectingBody = "Getting your games and friends ready."
    const val connectionOffline = "Can't reach YapNPlay"
    const val connectionOfflineBody = "Check your internet connection. We'll keep trying."
    const val connectionLost = "Connection lost"
    const val connectionLostBody = "Reconnecting… your game will carry on when you're back."
    const val connectionRetry = "Retry"
    const val updateRequiredTitle = "Update required"
    const val updateRequiredBody = "This version of YapNPlay is no longer supported. Update to keep playing."
    const val updateAvailableTitle = "New version available"
    const val updateAvailableBody = "A newer YapNPlay is ready with fixes and improvements."
    const val updateNow = "Update"
    const val updateLater = "Later"
    const val rejoinTitle = "Your game is still on"
    fun rejoinBody(title: String) = "You still have a seat in $title. Jump back in?"
    const val rejoinAction = "Rejoin"
    const val rejoinNotNow = "Not now"
    const val micPermissionTitle = "Microphone access needed"
    const val micPermissionBody = "Allow YapNPlay to use your microphone so your friends can hear you. You can turn it on in Settings."
    const val micPermissionOpenSettings = "Open Settings"

    const val gameZoneGridView = "Grid View"
    const val gameZoneCarouselView = "Carousel"
    const val gameZoneSearching = "Finding players…"
    const val gameZoneCpuFillHint = "Empty seats fill with CPU players when time runs out"
    const val gameZoneCancel = "Cancel"
    const val gameZoneNoMatch = "No match found. Try again."
    const val gameZoneNoGames = "No games available right now"
    const val gameZonePreviousGame = "Previous game"
    const val gameZoneNextGame = "Next game"

    fun gameZoneOnline(count: String) = "$count Online"
    fun gameZonePlayersFound(found: Int, needed: Int) = "$found / $needed players"
    fun gameZoneTimeLeft(seconds: Long) = "${seconds / 60}:${(seconds % 60).toString().padStart(2, '0')}"
    fun gameZoneMatchFound(cpuPlayers: Int) =
        if (cpuPlayers > 0) "Match found! $cpuPlayers CPU joined" else "Match found!"
    fun gameTaglines(taglines: List<String>) = taglines.joinToString("  •  ")

    const val topBarNotifications = "Notifications"
    const val notificationsEmpty = "No notifications yet"

    /** "Just now", "5m ago", "3h ago", "2d ago". */
    fun timeAgo(minutes: Long): String = when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        minutes < 60 * 24 -> "${minutes / 60}h ago"
        else -> "${minutes / (60 * 24)}d ago"
    }
    const val topBarSettings = "Settings"
    const val topBarProfile = "Profile"
    const val homePlayWithFriendsTitlePart1 = "PLAY WITH "
    const val homePlayWithFriendsTitlePart2 = "FRIENDS"
    const val homePlayWithFriendsSubtitle = "Your crew. Your rooms. Your rules. Create or join a room and keep the fun going!"
    const val homeInviteFriends = "Invite\nFriends"
    const val homeCreateRoomBannerText = "Create a private room, set your own house rules, and invite your friends to join the fun!"
    const val homeCreateRoom = "Create Room"
    const val homeOnline = "ONLINE"
    const val homeOpenGallery = "Component gallery"
    const val homeOpenAbout = "About"

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

    const val aboutTitle = "About"
    const val aboutRetry = "Retry"
    const val aboutLoading = "Loading…"
    fun aboutInfo(appName: String, version: String, contract: String): String =
        "$appName $version · contract $contract"

    const val actionOk = "OK"
    const val actionCancel = "Cancel"
    const val actionRetry = "Retry"
    const val actionSettings = "Settings"
    const val actionChat = "Chat"
    const val actionRollDice = "Roll Dice"
    const val actionAutoplay = "Autoplay"
    const val actionInviteFriends = "Invite friends"
    const val actionLeaveRoom = "Leave room"

    const val hintDisplayName = "Display name"
    const val hintRoomCode = "XXXXXX"

    const val keyboardShift = "Shift"
    const val keyboardDelete = "Delete"
    const val keyboardSpace = "space"
    const val keyboardNumbers = "123"
    const val keyboardLetters = "ABC"
    const val keyboardDone = "Done"
    const val keyboardClose = "Close keyboard"

    /** The × at the end of a text field that empties it. */
    const val clearField = "Clear"
    val monthsShort = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    fun shortDate(day: Int, month: Int, year: Int) = "$day ${monthsShort[month - 1]} $year"


}
