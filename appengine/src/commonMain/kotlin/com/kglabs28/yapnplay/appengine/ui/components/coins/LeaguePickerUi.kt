package com.kglabs28.yapnplay.appengine.ui.components.coins

/** The league picker before Quick Play: the balance line and the leagues (empty while loading). */
data class LeaguePickerUi(
    val balanceLabel: String,
    val leagues: List<LeagueUi>,
    val message: String? = null,
)
