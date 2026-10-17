package com.kglabs28.yapnplay.appengine.ui.components.moderation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.common.OnOffSwitch

/** In-match settings shared by every game: sound effects, music, and report a player. */
@Composable
fun MatchSettingsCard(
    effectsOn: Boolean,
    musicOn: Boolean,
    onToggleEffects: () -> Unit,
    onToggleMusic: () -> Unit,
    onReport: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalCard(title = Strings.ludoSettings, onClose = onClose, modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
            SettingRow(Strings.ludoSoundEffects, effectsOn, onToggleEffects)
            SettingRow(Strings.ludoMusic, musicOn, onToggleMusic)
            GlassPillButton(
                text = Strings.ludoReport,
                onClick = onReport,
                leadingIcon = Icons.Rounded.Flag,
                accent = YapNPlayTheme.colors.Error,
                contentColor = YapNPlayTheme.colors.Error,
                height = YapNPlayTheme.dimens.ctaHeight,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun SettingRow(label: String, on: Boolean, onToggle: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        BasicText(
            text = label,
            style = YapNPlayTheme.typography.sectionTitle.copy(color = YapNPlayTheme.colors.TextPrimary),
            modifier = Modifier.weight(1f),
        )
        OnOffSwitch(on = on, onToggle = onToggle)
    }
}
