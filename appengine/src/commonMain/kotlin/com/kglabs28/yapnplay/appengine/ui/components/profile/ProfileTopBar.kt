package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassIconButton

/** The profile's top bar: back, "Profile", then frosted-white Settings and the glassy red Log out. */
@Composable
fun ProfileTopBar(
    onBack: () -> Unit,
    onSettings: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
        GlassIconButton(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = Strings.profileBack, onClick = onBack)
        BasicText(text = Strings.profileTitle, style = YapNPlayTheme.typography.title.copy(color = colors.TextPrimary))
        Spacer(Modifier.weight(1f))
        FrostedIconButton(icon = Icons.Rounded.Settings, description = Strings.profileSettings, onClick = onSettings)
        RedGlassButton(text = Strings.profileLogout, icon = Icons.AutoMirrored.Rounded.Logout, onClick = onLogout)
    }
}
