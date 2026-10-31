package com.kglabs28.yapnplay.appengine.ui.components.common

import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import kotlinx.coroutines.delay
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.fadeOut
import androidx.compose.animation.fadeIn
import androidx.compose.animation.AnimatedVisibility
import com.kglabs28.yapnplay.appengine.appmanager.helpers.AppManagerHelper
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.kglabs28.yapnplay.appengine.appmanager.AppManager
import com.kglabs28.yapnplay.appengine.appmanager.UpdateNeed
import com.kglabs28.yapnplay.appengine.appmanager.rememberAppUpdater
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import org.koin.compose.koinInject

/**
 * Updates, from AppManager (see UpdateRule). Below `update_min_version`
 * the app is covered by "Update required" with no way past it; when
 * Google Play has a newer build (and `update_prompt_enabled`, on by
 * default) a "New version available" bar shows at the bottom for a few seconds, once per launch. Both
 * buttons open Google Play's update. The force rule works offline too:
 * the last fetched values are kept by [AppManager].
 */
@Composable
fun UpdateGate(
    modifier: Modifier = Modifier,
    appManager: AppManager = koinInject(),
    appConfig: AppManagerHelper = koinInject(),
    content: @Composable () -> Unit,
) {
    val updater = rememberAppUpdater()
    // Re-read the rule whenever Remote Config values arrive.
    val revision by appManager.revision.collectAsState()
    val rule = remember(revision) { appConfig.updateRule() }
    // Asked once per launch: does Google Play hold a newer build?
    var storeHasUpdate by remember { mutableStateOf(false) }
    LaunchedEffect(updater) { storeHasUpdate = updater.isUpdateAvailable() }
    val need = rule.need(updater.versionName, storeHasUpdate)
    var optionalDismissed by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        content()
        if (need == UpdateNeed.REQUIRED) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(YapNPlayTheme.colors.Background.copy(alpha = SCRIM_ALPHA))
                    .pointerInput(Unit) { awaitEachGesture { awaitFirstDown(requireUnconsumed = false).consume() } },
                contentAlignment = Alignment.Center,
            ) {
                ModalCard(
                    title = Strings.updateRequiredTitle,
                    subtitle = rule.message.ifBlank { Strings.updateRequiredBody },
                    onClose = null,
                    actions = {
                        GlassPillButton(text = Strings.updateNow, onClick = updater::startUpdate, leadingIcon = Icons.Rounded.SystemUpdate)
                    },
                )
            }
            // A required update cannot be backed out of.
            BlockBackPress(enabled = true, onBack = {})
        }
        // An optional update: a bar at the bottom for a few seconds, with Update; it never stops play.
        val showOptional = need == UpdateNeed.OPTIONAL && !optionalDismissed
        LaunchedEffect(showOptional) {
            if (showOptional) {
                delay(UPDATE_BAR_MS)
                optionalDismissed = true
            }
        }
        AnimatedVisibility(
            visible = showOptional,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = Spacing.Md),
        ) {
            UpdateBar(message = rule.message.ifBlank { Strings.updateAvailableTitle }, onUpdate = updater::startUpdate, onClose = { optionalDismissed = true })
        }
    }
}

/** "New version available" with Update (to Google Play) and a close cross, in a glass pill. */
@Composable
private fun UpdateBar(message: String, onUpdate: () -> Unit, onClose: () -> Unit) {
    val colors = YapNPlayTheme.colors
    Row(
        modifier = Modifier
            .glassPill(glowColor = colors.Cyan, surface = colors.RoomsPanel)
            .padding(start = Spacing.Md, end = Spacing.Xs, top = Spacing.Xs, bottom = Spacing.Xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
    ) {
        AppIcon(imageVector = Icons.Rounded.SystemUpdate, tint = colors.Cyan, size = YapNPlayTheme.dimens.iconSmall)
        BasicText(text = message, style = YapNPlayTheme.typography.label.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold))
        GlassPillButton(text = Strings.updateNow, onClick = onUpdate, leadingIcon = Icons.Rounded.SystemUpdate)
        GlassIconButton(imageVector = Icons.Rounded.Close, contentDescription = Strings.updateLater, onClick = onClose, size = YapNPlayTheme.dimens.smallButtonHeight)
    }
}

private const val SCRIM_ALPHA = 0.9f

/** How long the optional-update bar stays. */
private const val UPDATE_BAR_MS = 8_000L
