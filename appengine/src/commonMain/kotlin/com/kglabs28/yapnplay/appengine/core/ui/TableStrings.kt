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
    /** "1st", "2nd", "3rd", "4th"… — every game's place label. */
    fun placeLabel(place: Int) = when (place) {
        1 -> "1st"
        2 -> "2nd"
        3 -> "3rd"
        else -> "${place}th"
    }
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
    const val gameRules = "Game Rules"
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

    // End the game early (a vote), at every table.
    const val endGameButton = "End game (vote)"
    const val endVoteTitle = "END THE GAME?"
    fun endVoteBody(proposer: String, ranking: String) = "$proposer wants to end the game now. $ranking It ends if more than half of the players agree."
    fun endVoteTally(yes: Int, total: Int) = "Votes to end: $yes of $total"
    const val endVoteEnd = "End game"
    const val endVoteKeep = "Keep playing"
    fun endVoteYouVoted(agree: Boolean) = if (agree) "You voted to end. Waiting for the others..." else "You voted to keep playing. Waiting for the others..."
    const val endVoteWatching = "The players are deciding whether to end the game..."
    fun endVoteRejected(yes: Int, voters: Int) = "The game goes on: only $yes of $voters voted to end"
    fun endVoted(yes: Int, voters: Int) = "The game was ended by vote ($yes of $voters)"
    const val endVoteRejectedShort = "Not enough votes: the game goes on"
    /** The Game Rules entry, the same at every table but for how [ranking] players. */
    fun endEarlyRule(ranking: String) = "Any player can ask to end the game from Settings. Everyone still playing votes; it ends if more than half agree, and a missing vote counts as no. $ranking A player can ask again after a few minutes."
    const val endEarlyRuleTitle = "Ending early"

    private const val SECONDS_PER_MINUTE = 60L
}
