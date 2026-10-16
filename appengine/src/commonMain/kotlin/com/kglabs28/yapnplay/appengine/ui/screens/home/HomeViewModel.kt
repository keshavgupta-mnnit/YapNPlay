package com.kglabs28.yapnplay.appengine.ui.screens.home

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModel
import com.kglabs28.yapnplay.appengine.features.profile.usecase.GetHomeScreenData

class HomeViewModel(
    private val getHomeScreenData: GetHomeScreenData,
    dispatchers: DispatcherProvider,
) : MviViewModel<HomeState, HomeIntent, HomeEffect>(HomeState(), dispatchers) {

    override suspend fun handleIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.LoadProfile -> load()
            HomeIntent.EnterGameZone -> emitEffect(HomeEffect.NavigateToGameZone)
            HomeIntent.PlayWithFriends -> emitEffect(HomeEffect.NavigateToPlayWithFriends)
            HomeIntent.OpenSettings -> emitEffect(HomeEffect.OpenSettingsDialog)
            HomeIntent.OpenNotifications -> emitEffect(HomeEffect.OpenNotificationsDialog)
        }
    }

    private suspend fun load() {
        val data = (getHomeScreenData() as? AppResult.Success)?.data ?: return
        setState {
            copy(
                userName = data.profile.name,
                level = data.profile.level.toString(),
                levelProgressText = "${data.profile.xp} / ${data.profile.xpMax}",
                coinBalance = "$ ${data.profile.coins}",
                diamondBalance = "${data.profile.gems}",
                friends = data.friends,
            )
        }
    }
}
