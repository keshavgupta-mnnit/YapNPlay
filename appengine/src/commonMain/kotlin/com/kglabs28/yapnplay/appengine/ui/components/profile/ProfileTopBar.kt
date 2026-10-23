package com.kglabs28.yapnplay.appengine.ui.components.profile

import com.kglabs28.yapnplay.appengine.ui.components.common.BackTitleBar
import com.kglabs28.yapnplay.appengine.core.ui.ProfileStrings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** The profile's top bar: back, "Profile", then frosted-white Settings and the glassy red Log out. */
@Composable
fun ProfileTopBar(
    onBack: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackTitleBar(title = ProfileStrings.profileTitle, backDescription = ProfileStrings.profileBack, onBack = onBack, modifier = modifier) {
        FrostedIconButton(icon = Icons.Rounded.Settings, description = ProfileStrings.profileSettings, onClick = onSettings)
    }
}
