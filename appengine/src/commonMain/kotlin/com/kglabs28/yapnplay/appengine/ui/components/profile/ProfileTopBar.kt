package com.kglabs28.yapnplay.appengine.ui.components.profile

import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import com.kglabs28.yapnplay.appengine.ui.components.common.BackTitleBar
import com.kglabs28.yapnplay.appengine.core.ui.ProfileStrings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** The profile's top bar: back, "Profile", then frosted-white Settings and the glassy red Exit (closes the app, after asking). */
@Composable
fun ProfileTopBar(
    onBack: () -> Unit,
    onSettings: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackTitleBar(title = ProfileStrings.profileTitle, backDescription = ProfileStrings.profileBack, onBack = onBack, modifier = modifier) {
        FrostedIconButton(icon = Icons.Rounded.Settings, description = ProfileStrings.profileSettings, onClick = onSettings)
        FrostedIconButton(icon = Icons.AutoMirrored.Rounded.ExitToApp, description = ProfileStrings.profileExit, onClick = onExit, tint = YapNPlayTheme.colors.Error)
    }
}
