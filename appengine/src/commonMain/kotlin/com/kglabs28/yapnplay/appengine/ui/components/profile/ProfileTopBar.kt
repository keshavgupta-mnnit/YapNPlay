package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassIconButton

/** The profile's top bar: back, and the screen's title. */
@Composable
fun ProfileTopBar(title: String, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
        GlassIconButton(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = Strings.profileBack, onClick = onBack)
        BasicText(text = title, style = YapNPlayTheme.typography.title.copy(color = YapNPlayTheme.colors.TextPrimary))
    }
}
