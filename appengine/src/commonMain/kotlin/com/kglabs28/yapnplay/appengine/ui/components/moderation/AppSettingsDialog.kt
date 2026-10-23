package com.kglabs28.yapnplay.appengine.ui.components.moderation

import androidx.compose.ui.platform.LocalUriHandler
import org.koin.compose.koinInject
import com.kglabs28.yapnplay.appengine.appmanager.helpers.AppManagerHelper
import androidx.compose.runtime.Composable
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockingOverlay

/** The app's settings dialog while [settings] is set: sound, music, Match History and Feedback & support. */
@Composable
fun AppSettingsDialog(
    settings: AppSettingsUi?,
    onToggleEffects: () -> Unit,
    onToggleMusic: () -> Unit,
    onToggleKeyboard: () -> Unit,
    onHistory: () -> Unit,
    onFeedback: (compose: Boolean) -> Unit,
    onClose: () -> Unit,
    appConfig: AppManagerHelper = koinInject(),
) {
    val uriHandler = LocalUriHandler.current
    BlockingOverlay(visible = settings != null, onDismiss = onClose) {
        if (settings != null) MatchSettingsCard(
            effectsOn = settings.effectsOn,
            musicOn = settings.musicOn,
            onToggleEffects = onToggleEffects,
            onToggleMusic = onToggleMusic,
            onClose = onClose,
            onHistory = onHistory,
            onFeedback = onFeedback,
            onRateApp = { runCatching { uriHandler.openUri(appConfig.playStoreUrl()) } },
            systemKeyboard = settings.systemKeyboard,
            onToggleKeyboard = onToggleKeyboard,
        )
    }
}
