package com.kglabs28.yapnplay.appengine.ui.components.common

import com.kglabs28.yapnplay.appengine.ui.components.profile.FrostedIconButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/** A full screen's top bar: back, the [title], then any [trailing] buttons at the end. */
@Composable
fun BackTitleBar(
    title: String,
    backDescription: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    trailing: (@Composable RowScope.() -> Unit)? = null,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
        // The same white frosted button as the bar's settings gear.
        FrostedIconButton(icon = Icons.AutoMirrored.Rounded.ArrowBack, description = backDescription, onClick = onBack)
        BasicText(text = title, style = YapNPlayTheme.typography.title.copy(color = YapNPlayTheme.colors.TextPrimary, fontWeight = FontWeight.Bold))
        Spacer(Modifier.weight(1f))
        trailing?.invoke(this)
    }
}
