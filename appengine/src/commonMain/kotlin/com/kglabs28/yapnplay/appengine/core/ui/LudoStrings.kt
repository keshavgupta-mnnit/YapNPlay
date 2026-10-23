package com.kglabs28.yapnplay.appengine.core.ui

import com.kglabs28.yapnplay.appengine.ui.components.moderation.GameRuleUi
/** Royal Ludo: table, board, results. */
object LudoStrings {
    /** The rules shown from the settings menu. */
    /** How Royal Ludo ranks players when it ends early. */
    const val ludoEndRanking = "Players are ranked by how far their pieces got."

    val ludoRules: List<GameRuleUi> = listOf(
        GameRuleUi("Goal", "Bring all four of your pieces round the board and into the centre first."),
        GameRuleUi("Leaving base", "Only a 6 brings a piece out of base onto your start square."),
        GameRuleUi("Moving", "Move one piece forward by the number you roll. With only one possible move, it is played for you."),
        GameRuleUi("Extra turns", "Rolling a 6, capturing a piece, or bringing a piece home gives you another roll."),
        GameRuleUi("Three sixes", "A third 6 in a row cancels the whole turn: every piece goes back to where it was, and the turn passes."),
        GameRuleUi("Capture", "Land on a lone opposing piece outside a safe square to send it back to base."),
        GameRuleUi("Safe squares", "Start squares and star squares are safe: nobody can be captured there."),
        GameRuleUi("Pairs", "Two or more of your pieces on one square protect each other: they cannot be captured."),
        GameRuleUi("Exact finish", "A piece needs the exact number to reach the centre. Too big a roll cannot move it."),
        GameRuleUi("Out of time", "If your time runs out, the table rolls and moves for you."),
        GameRuleUi(TableStrings.endEarlyRuleTitle, TableStrings.endEarlyRule(ludoEndRanking)),
    )


    const val ludoTitle = "ROYAL LUDO"
    const val ludoRollDice = "Roll Dice"
    const val ludoSpectators = "spectators"
    const val ludoSettings = "Settings"
    const val ludoExit = "Leave match"
    const val ludoLeaveBody = "If you leave, you're out of this match: your pieces leave the board and your entry is lost."
    const val ludoResultTitle = "Match over"
    const val ludoDone = "Done"
    const val ludoSoundEffects = "Sound effects"
    const val ludoMusic = "Music"
    const val ludoReport = "Report a player"
    const val ludoReportTitle = "Report voice chat"
    const val ludoReportBody = "Was someone abusive or offensive on voice chat? Choose who."
    fun ludoReportWarning(required: Int) =
        "Everyone else here will be asked to confirm. If fewer than $required agree, the incident is recorded against you."
    const val ludoReportSend = "Report"
    const val ludoReportPick = "Choose a person"
    fun ludoReportOption(name: String, role: String) = "$name · $role"
    const val ludoReportNeedPeople = "Not enough people here to confirm a report."
    const val ludoRolePlayer = "Player"
    const val ludoRoleSpectator = "Spectator"
    const val ludoVoteTitle = "Can you confirm?"
    fun ludoVoteBody(reporter: String, reported: String) = "$reporter reported $reported for voice chat (abuse or similar). Did $reported do this?"
    const val ludoVoteAgree = "Agree"
    const val ludoVoteDisagree = "Disagree"
    fun ludoVoteSecondsLeft(seconds: Int) = "Closes in ${seconds}s"
    const val ludoReportUpheldTitle = "Report confirmed"
    fun ludoReportUpheldBody(reported: String, tally: String) = "$tally. The incident is recorded against $reported."
    const val ludoReportRejectedTitle = "Report not confirmed"
    fun ludoReportRejectedBody(tally: String, required: Int) = "$tally ($required agreements needed), so the incident is recorded against you."
    fun ludoReportTally(agreed: Int, disagreed: Int, noResponse: Int) = "$agreed agreed, $disagreed disagreed, $noResponse didn't answer"
    const val ludoPenaltyTitle = "You're on a break"
    fun ludoPenaltyBody(points: Int, minutes: Int) = "$points points were taken. You can play again in $minutes min."
    const val ludoWatchAd = "Watch an ad to play now"
    const val ludoWatchingAd = "Playing ad…"
    const val ludoOk = "OK"
    fun ludoMuteFor(name: String) = "Mute $name for me"
    fun ludoMove(steps: Int) = "Move $steps"
    const val ludoAway = "Away"
    fun ludoClock(minutes: Long, seconds: Long) = "$minutes:${seconds.toString().padStart(2, '0')}"
    fun ludoPlace(place: Int) = TableStrings.placeLabel(place)
    val ludoPlaceTitles = listOf("🔥 Chaos King", "⚡ Speedy", "🎯 Hunter", "🐢 Slowpoke", "🎲 Lucky", "👀 Spectator")

}
