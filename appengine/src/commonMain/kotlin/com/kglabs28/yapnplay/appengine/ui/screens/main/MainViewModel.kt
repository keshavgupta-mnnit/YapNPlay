package com.kglabs28.yapnplay.appengine.ui.screens.main

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.TextUtils
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModel
import com.kglabs28.yapnplay.appengine.features.profile.usecase.GetHomeScreenDataUseCase

/**
 * Dashboard scaffold: owns the shared top bar data and the selected
 * tab. Profile loads once from the use case; the home cards switch
 * tabs; settings and notifications emit one-off effects.
 */
class MainViewModel(
    private val getHomeScreenData: GetHomeScreenDataUseCase,
    dispatchers: DispatcherProvider,
) : MviViewModel<MainState, MainIntent, MainEffect>(MainState(), dispatchers) {

    override suspend fun handleIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.SelectTab -> setState { copy(selectedTab = intent.tab) }
            MainIntent.LoadProfile -> load()
            MainIntent.OpenSettings -> emitEffect(MainEffect.OpenSettingsDialog)
            MainIntent.OpenNotifications -> emitEffect(MainEffect.OpenNotificationsDialog)
        }
    }

    private suspend fun load() {
        val data = (getHomeScreenData() as? AppResult.Success)?.data ?: return
        setState {
            copy(
                userName = data.profile.name,
                level = data.profile.level.toString(),
                // Reaching the server means online; the WebSocket connection
                // manager (Phase 12) will drive this live later.
                online = true,
                profileLoaded = true,
                coinBalance = TextUtils.groupedThousands(data.profile.coins),
                diamondBalance = TextUtils.groupedThousands(data.profile.gems),
            )
        }
    }
}
