package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.runtime.Composable
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
import com.kglabs28.yapnplay.appengine.appmanager.UpdateRule
import com.kglabs28.yapnplay.appengine.appmanager.rememberAppUpdater
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import org.koin.compose.koinInject

/**
 * Force update from Remote Config (`update_config`, see [UpdateRule]).
 * Below the minimum version the app is covered by "Update required" with
 * no way past it; below the latest version a dismissable "New version"
 * card shows once per launch. Both buttons open Google Play's update.
 * Works offline too: the last fetched rule is kept by [AppManager].
 */
@Composable
fun UpdateGate(
    modifier: Modifier = Modifier,
    appManager: AppManager = koinInject(),
    content: @Composable () -> Unit,
) {
    val updater = rememberAppUpdater()
    // Re-read the rule whenever Remote Config values arrive.
    val revision by appManager.revision.collectAsState()
    val rule = remember(revision) { UpdateRule.parse(appManager.updateConfig()) }
    val need = rule.need(updater.versionName)
    var optionalDismissed by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        content()
        val showRequired = need == UpdateNeed.REQUIRED
        val showOptional = need == UpdateNeed.OPTIONAL && !optionalDismissed
        if (showRequired || showOptional) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(YapNPlayTheme.colors.Background.copy(alpha = SCRIM_ALPHA))
                    .pointerInput(Unit) { awaitEachGesture { awaitFirstDown(requireUnconsumed = false).consume() } },
                contentAlignment = Alignment.Center,
            ) {
                val body = rule.message.ifBlank { if (showRequired) Strings.updateRequiredBody else Strings.updateAvailableBody }
                ModalCard(
                    title = if (showRequired) Strings.updateRequiredTitle else Strings.updateAvailableTitle,
                    subtitle = body,
                    onClose = if (showRequired) null else ({ optionalDismissed = true }),
                    actions = {
                        if (!showRequired) GlassPillButton(text = Strings.updateLater, onClick = { optionalDismissed = true })
                        GlassPillButton(text = Strings.updateNow, onClick = updater::startUpdate, leadingIcon = Icons.Rounded.SystemUpdate)
                    },
                )
            }
            // A required update cannot be backed out of.
            BlockBackPress(enabled = true, onBack = { if (!showRequired) optionalDismissed = true })
        }
    }
}

private const val SCRIM_ALPHA = 0.9f
