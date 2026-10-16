package com.kglabs28.yapnplay.appengine.ui.screens.rooms

import com.kglabs28.yapnplay.appengine.core.mvi.UiEffect
import com.kglabs28.yapnplay.appengine.core.mvi.UiIntent
import com.kglabs28.yapnplay.appengine.core.mvi.UiState
import com.kglabs28.yapnplay.appengine.core.ui.Strings

/**
 * Rooms tab. Copy lives in [Strings]; the view model only re-asserts
 * it until real room-list data arrives.
 */
data class RoomsState(
    val title: String = Strings.roomsTabTitle,
    val subtitle: String = Strings.roomsTabSubtitle,
) : UiState

sealed interface RoomsIntent : UiIntent {
    data object Refresh : RoomsIntent
}

sealed interface RoomsEffect : UiEffect
