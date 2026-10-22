package com.kglabs28.yapnplay.appengine.core.ui

/** Every game table: seats, players, history, leaderboard. */
object TableStrings {
    const val matchGoneTitle = "Game ended"
    const val matchGoneBody = "The game server restarted, so this game couldn't carry on. Start a new one from the Game Zone."

    const val seatBot = "CPU"
    const val seatLeaveBody = "Pick who plays on for you: a CPU, or a spectator (they get 30s to accept while the match waits)."
    const val seatOffered = "Offered"
    const val seatIdle = "Idle"
    const val seatInactiveTitle = "Seat Inactive"
    fun seatAbsenceBody(turns: Int, roomMatch: Boolean) = "A CPU played your last $turns turns. " +
        if (roomMatch) "Stay, or pick who takes your seat and watch as a spectator. With no answer, the leader decides." else "Stay, or let a CPU take your seat and watch."
    const val seatOpenTitle = "Seat Open"
    fun seatLeaderBody(leaver: String) = "$leaver's seat needs a new player. Invite a spectator, or give it to a CPU."
    const val seatImHere = "I'm Here"
    const val seatSpectate = "Spectate"
    const val seatSpectatorLeaveBody = "You're watching as a spectator."
    const val leaveMatchTitle = "Leave the match?"
    const val leaveMatchBody = "A CPU will play your seat for the rest of the match."
    const val leaveQuickPlayBody = "If you leave, you're out of this match: your pieces, cards or properties leave the game and your entry is lost."
    const val seatCancel = "Cancel"
    const val seatExit = "Exit"
    const val seatOfferToBot = "Give to CPU"
    const val seatInviteToSeat = "Invite to Seat"
    fun seatCountdown(seconds: Long) = "${seconds / SECONDS_PER_MINUTE}:${(seconds % SECONDS_PER_MINUTE).toString().padStart(2, '0')}"
    fun seatCheckingAbsence(name: String, seconds: Long) = "Checking if $name is still here · ${seconds}s"
    fun seatOpen(name: String, seconds: Long) = "$name left · seat open ${seconds}s"
    fun seatOfferedTo(leaver: String, taker: String, seconds: Long) = "Paused · $leaver's seat offered to $taker · ${seconds}s"
    fun seatLeaderChoosing(leader: String, leaver: String, seconds: Long) = "Paused · $leader is choosing who gets $leaver's seat · ${seconds}s"
    fun seatPromptTitle(leaver: String) = "Play on for $leaver?"
    fun seatPromptBody(leaver: String) = "$leaver left and offered you their seat. You continue from their position; joining late gives reduced rewards."
    fun seatInviteTitle(name: String) = "Take $name's seat?"
    const val seatInviteBody = "Play on from where they left off. Joining late gives reduced rewards."
    fun seatTake(seconds: Long) = "Take seat · ${seconds}s"
    const val seatDecline = "Decline"

    const val leaderboardTitle = "LEADERBOARD"
    fun leaderboardExp(exp: Int) = "+$exp EXP"
    const val leaderboardShare = "SHARE THIS"
    const val leaderboardMatchDuration = "MATCH DURATION: "
    const val leaderboardExpGained = "EXP GAINED: "
    fun leaderboardPts(points: Int) = "+$points PTS"
    fun leaderboardCoins(coins: String) = "+$coins coins"
    const val leaderboardNoCoins = "0 coins"
    fun leaderboardLost(coins: String) = "-$coins coins"
    /** "+300": an amount gained, in the leaderboard's bottom bar. */
    fun leaderboardGain(amount: String) = "+$amount"
    /** The table's subtitle: its parts (title, round, pot) joined with a dot. */
    fun tableSubtitle(parts: List<String>) = parts.joinToString(" · ")
    fun leaderboardShareWithLink(text: String, link: String) = "$text\n$link"
    const val leaderboardPlayAgain = "PLAY AGAIN"
    fun leaderboardShareText(game: String, place: String) = "I just finished $place in $game on YapNPlay! Play and talk with me there."
    const val tableHistory = "Game history"
    const val tableHistoryTitle = "Game History"
    const val tableHistoryEmpty = "Nothing has happened yet."
    const val playerAddFriendTitle = "Add Friend"
    fun playerAddFriendQuestion(name: String) = "Send $name a friend request?"
    const val playerAddFriendBody = "They can accept it from their Profile."
    const val playerAddFriendConfirm = "Send Request"
    const val playerRemoveFriendTitle = "Remove Friend"
    const val playerFriendFailed = "Couldn't do that. Check your connection and try again."

    private const val SECONDS_PER_MINUTE = 60L
}
