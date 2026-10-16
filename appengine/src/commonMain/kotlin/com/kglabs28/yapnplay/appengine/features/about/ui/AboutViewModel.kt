package com.kglabs28.yapnplay.appengine.features.about.ui

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModel
import com.kglabs28.yapnplay.appengine.features.about.domain.GetAppInfoUseCase

/**
 * Sample MVI view model: intent → use case → state, back as an effect.
 */
class AboutViewModel(
    private val getAppInfo: GetAppInfoUseCase,
    dispatchers: DispatcherProvider,
) : MviViewModel<AboutScreenState, AboutScreenIntent, AboutScreenEffect>(AboutScreenState(), dispatchers) {

    override suspend fun handleIntent(intent: AboutScreenIntent) {
        when (intent) {
            AboutScreenIntent.Load, AboutScreenIntent.Retry -> load()
            AboutScreenIntent.GoBack -> emitEffect(AboutScreenEffect.Back)
        }
    }

    private suspend fun load() {
        setState { copy(loading = true, error = null) }
        when (val result = getAppInfo()) {
            is AppResult.Success -> setState { copy(loading = false, info = result.data) }
            is AppResult.Failure -> setState { copy(loading = false, error = result.error.toString()) }
        }
    }
}
