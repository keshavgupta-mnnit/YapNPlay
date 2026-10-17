package com.kglabs28.yapnplay.appengine.games.citybazaar.domain

/** What the player can do at the table. */
sealed interface CityBazaarCommand {
    data object Roll : CityBazaarCommand
    data object Buy : CityBazaarCommand
    data object Decline : CityBazaarCommand
    data class Build(val tile: Int) : CityBazaarCommand
    data object SkipBuild : CityBazaarCommand
    data class SellBuilding(val tile: Int) : CityBazaarCommand
    data class SellTile(val tile: Int) : CityBazaarCommand
    /** Put a bare city up for auction among the other players. */
    data class StartAuction(val tile: Int) : CityBazaarCommand
    /** Bid the next step in the running auction. */
    data object Bid : CityBazaarCommand
    data object PayJailFine : CityBazaarCommand
    /** Stay in jail on half rent until the next turn. */
    data object StayInJail : CityBazaarCommand
    data object DeclareBankruptcy : CityBazaarCommand
    data class SetAutoplay(val enabled: Boolean) : CityBazaarCommand
    data class SetMic(val on: Boolean) : CityBazaarCommand
    /** The app went to the background (true) or came back (false). */
    data class SetAway(val away: Boolean) : CityBazaarCommand
    /** Leave the match; the bot plays the seat. */
    data object Leave : CityBazaarCommand
}
