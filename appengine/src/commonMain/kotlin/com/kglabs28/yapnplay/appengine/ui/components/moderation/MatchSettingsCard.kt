package com.kglabs28.yapnplay.appengine.ui.components.moderation

import androidx.compose.ui.text.style.TextAlign
import org.koin.compose.koinInject
import com.kglabs28.yapnplay.appengine.core.common.AppVersionProvider
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import androidx.compose.material.icons.rounded.SupportAgent
import com.kglabs28.yapnplay.appengine.core.ui.FeedbackStrings
import com.kglabs28.yapnplay.appengine.core.ui.HistoryStrings
import androidx.compose.material.icons.rounded.History
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.SportsScore
import com.kglabs28.yapnplay.appengine.core.ui.LudoStrings
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
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.common.OnOffSwitch

/** Settings shared by every game and Home: sound effects, music, the game's rules, report a player, and (Home) match history. */
@Composable
fun MatchSettingsCard(
    effectsOn: Boolean,
    musicOn: Boolean,
    onToggleEffects: () -> Unit,
    onToggleMusic: () -> Unit,
    /** Null hides Report (games without voice reports). */
    onReport: (() -> Unit)? = null,
    /** Ask everyone to end the game (a vote); null hides it. */
    onEndGame: (() -> Unit)? = null,
    endGameLabel: String = "",
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    /** The game's rules; empty hides Game Rules. */
    rules: List<GameRuleUi> = emptyList(),
    /** Opens the user's match history; null hides it (only Home has it). */
    onHistory: (() -> Unit)? = null,
    /** Opens Feedback & support with the form ([compose]) or the sent messages; null hides it (Home and Profile only). */
    onFeedback: ((compose: Boolean) -> Unit)? = null,
    /** Opens the app on Google Play to rate it (with [onFeedback]). */
    onRateApp: () -> Unit = {},
    /** The phone's keyboard instead of the app's; null hides the switch (Home and Profile only). */
    systemKeyboard: Boolean? = null,
    onToggleKeyboard: () -> Unit = {},
    versions: AppVersionProvider = koinInject(),
) {
    // Game Rules and Feedback & support show in this card's place; closing them comes back here.
    var showingRules by remember { mutableStateOf(false) }
    var showingFeedback by remember { mutableStateOf(false) }
    if (showingRules) {
        GameRulesCard(rules = rules, onClose = { showingRules = false }, modifier = modifier)
        return
    }
    if (showingFeedback && onFeedback != null) {
        FeedbackOptionsCard(
            onShare = { onFeedback(true) },
            onHistory = { onFeedback(false) },
            onRate = onRateApp,
            onClose = { showingFeedback = false },
            modifier = modifier,
        )
        return
    }
    ModalCard(title = LudoStrings.ludoSettings, onClose = onClose, modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
            SettingRow(LudoStrings.ludoSoundEffects, effectsOn, onToggleEffects)
            SettingRow(LudoStrings.ludoMusic, musicOn, onToggleMusic)
            systemKeyboard?.let { SettingRow(Strings.settingsPhoneKeyboard, it, onToggleKeyboard) }
            onHistory?.let {
                GlassPillButton(
                    text = HistoryStrings.historyTitle,
                    onClick = it,
                    leadingIcon = Icons.Rounded.History,
                    accent = YapNPlayTheme.colors.Gold,
                    contentColor = YapNPlayTheme.colors.Gold,
                    height = YapNPlayTheme.dimens.ctaHeight,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            onFeedback?.let {
                GlassPillButton(
                    text = FeedbackStrings.feedbackSettingsButton,
                    onClick = { showingFeedback = true },
                    leadingIcon = Icons.Rounded.SupportAgent,
                    accent = YapNPlayTheme.colors.Cyan,
                    contentColor = YapNPlayTheme.colors.Cyan,
                    height = YapNPlayTheme.dimens.ctaHeight,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (rules.isNotEmpty()) {
                GlassPillButton(
                    text = TableStrings.gameRules,
                    onClick = { showingRules = true },
                    leadingIcon = Icons.AutoMirrored.Rounded.MenuBook,
                    accent = YapNPlayTheme.colors.Cyan,
                    contentColor = YapNPlayTheme.colors.Cyan,
                    height = YapNPlayTheme.dimens.ctaHeight,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            onEndGame?.let {
                GlassPillButton(
                    text = endGameLabel,
                    onClick = it,
                    leadingIcon = Icons.Rounded.SportsScore,
                    accent = YapNPlayTheme.colors.Warning,
                    contentColor = YapNPlayTheme.colors.Warning,
                    height = YapNPlayTheme.dimens.ctaHeight,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            onReport?.let {
                GlassPillButton(
                    text = LudoStrings.ludoReport,
                    onClick = it,
                    leadingIcon = Icons.Rounded.Flag,
                    accent = YapNPlayTheme.colors.Error,
                    contentColor = YapNPlayTheme.colors.Error,
                    height = YapNPlayTheme.dimens.ctaHeight,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            // The installed version, centred at the foot of every settings dialog.
            BasicText(
                text = Strings.appVersion(versions.versionName()),
                style = YapNPlayTheme.typography.label.copy(color = YapNPlayTheme.colors.TextSecondary, textAlign = TextAlign.Center),
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
