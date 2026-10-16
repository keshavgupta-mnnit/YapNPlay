package com.kglabs28.yapnplay.appengine.ui.screens.rooms

import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModel
import com.kglabs28.yapnplay.appengine.core.ui.Strings

/**
 * Rooms tab: static copy for now, real room list later.
 */
class RoomsViewModel(
    dispatchers: DispatcherProvider,
) : MviViewModel<RoomsState, RoomsIntent, RoomsEffect>(RoomsState(), dispatchers) {

    override suspend fun handleIntent(intent: RoomsIntent) {
        when (intent) {
            RoomsIntent.Refresh -> setState {
                copy(title = Strings.roomsTabTitle, subtitle = Strings.roomsTabSubtitle)
            }
        }
    }
}
