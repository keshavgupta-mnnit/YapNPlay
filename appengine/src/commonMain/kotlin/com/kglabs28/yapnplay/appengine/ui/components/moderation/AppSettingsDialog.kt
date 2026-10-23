package com.kglabs28.yapnplay.appengine.ui.components.moderation

import androidx.compose.runtime.Composable
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockingOverlay

/** The app's settings dialog while [settings] is set: sound, music and Match History. */
@Composable
fun AppSettingsDialog(
    settings: AppSettingsUi?,
    onToggleEffects: () -> Unit,
    onToggleMusic: () -> Unit,
    onHistory: () -> Unit,
    onClose: () -> Unit,
) {
    BlockingOverlay(visible = settings != null, onDismiss = onClose) {
        if (settings != null) MatchSettingsCard(
            effectsOn = settings.effectsOn,
            musicOn = settings.musicOn,
            onToggleEffects = onToggleEffects,
            onToggleMusic = onToggleMusic,
            onClose = onClose,
            onHistory = onHistory,
        )
    }
}
