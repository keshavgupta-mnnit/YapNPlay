package com.kglabs28.yapnplay.appengine.games.citybaazar.domain

/** What the player can do at the table. */
sealed interface CityBaazarCommand {
    data object Roll : CityBaazarCommand
    data object Buy : CityBaazarCommand
    data object Decline : CityBaazarCommand
    data class Build(val tile: Int) : CityBaazarCommand
    data object SkipBuild : CityBaazarCommand
    data class SellBuilding(val tile: Int) : CityBaazarCommand
    data class SellTile(val tile: Int) : CityBaazarCommand
    data object PayJailFine : CityBaazarCommand
    data object UseJailPass : CityBaazarCommand
    data object DeclareBankruptcy : CityBaazarCommand
    data class SetAutoplay(val enabled: Boolean) : CityBaazarCommand
    data class SetMic(val on: Boolean) : CityBaazarCommand
    /** The app went to the background (true) or came back (false). */
    data class SetAway(val away: Boolean) : CityBaazarCommand
    /** Leave the match; the bot plays the seat. */
    data object Leave : CityBaazarCommand
}
