package com.kglabs28.yapnplay.appengine.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * Neon ring around an avatar. Lit in the accent colour while speaking,
 * dim otherwise. Wraps caller avatar content.
 */
@Composable
fun SpeakingRing(
    speaking: Boolean,
    modifier: Modifier = Modifier,
    accent: Color = YapNPlayTheme.colors.Cyan,
    content: @Composable () -> Unit,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val ring = if (speaking) accent else colors.TextDisabled.copy(alpha = 0.4f)
    Box(
        modifier = modifier
            .clip(YapNPlayTheme.shapes.avatar)
            .border(dimens.avatarRing, ring, YapNPlayTheme.shapes.avatar)
            .padding(dimens.avatarRing),
    ) {
        content()
    }
}
