package com.kglabs28.yapnplay.appengine.ui.screens.gallery

import com.kglabs28.yapnplay.appengine.core.designsystem.LayoutTier
import com.kglabs28.yapnplay.appengine.core.mvi.UiEffect
import com.kglabs28.yapnplay.appengine.core.mvi.UiIntent
import com.kglabs28.yapnplay.appengine.core.mvi.UiState

/** Gallery screen contract: state, intents and one-off effects. */
data class GalleryScreenState(
    /** Null follows the real window tier; set to force S/M/L previews. */
    val tierOverride: LayoutTier? = null,
) : UiState

sealed interface GalleryScreenIntent : UiIntent {
    data object ShowAllTiers : GalleryScreenIntent
    data class ForceTier(val tier: LayoutTier) : GalleryScreenIntent
    data object GoBack : GalleryScreenIntent
}

sealed interface GalleryScreenEffect : UiEffect {
    data object Back : GalleryScreenEffect
}
