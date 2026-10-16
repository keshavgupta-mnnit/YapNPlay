package com.kglabs28.yapnplay.appengine.ui.screens.games

import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModel
import com.kglabs28.yapnplay.appengine.core.ui.Strings

/**
 * Games tab: static copy for now, real game list later.
 */
class GamesViewModel(
    dispatchers: DispatcherProvider,
) : MviViewModel<GamesState, GamesIntent, GamesEffect>(GamesState(), dispatchers) {

    override suspend fun handleIntent(intent: GamesIntent) {
        when (intent) {
            GamesIntent.Refresh -> setState {
                copy(title = Strings.gamesTabTitle, subtitle = Strings.gamesTabSubtitle)
            }
        }
    }
}
