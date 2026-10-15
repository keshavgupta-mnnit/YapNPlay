package com.kglabs28.yapnplay.appengine.ui.screens.gallery

import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModel

/**
 * Gallery state: which tier to preview. Demonstrates the MVI pattern
 * on a screen with no repository.
 */
class GalleryViewModel(dispatchers: DispatcherProvider) :
    MviViewModel<GalleryScreenState, GalleryScreenIntent, GalleryScreenEffect>(GalleryScreenState(), dispatchers) {

    override suspend fun handleIntent(intent: GalleryScreenIntent) {
        when (intent) {
            GalleryScreenIntent.ShowAllTiers -> setState { copy(tierOverride = null) }
            is GalleryScreenIntent.ForceTier -> setState { copy(tierOverride = intent.tier) }
            GalleryScreenIntent.GoBack -> emitEffect(GalleryScreenEffect.Back)
        }
    }
}
