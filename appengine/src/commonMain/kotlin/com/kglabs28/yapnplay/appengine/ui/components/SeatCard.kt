package com.kglabs28.yapnplay.appengine.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * One seat on the match screen: 64dp card in the seat colour,
 * dimmed when inactive. Click selects it when [onClick] is set.
 */
@Composable
fun SeatCard(
    modifier: Modifier = Modifier,
    seatColor: Color = YapNPlayTheme.colors.Panel,
    active: Boolean = true,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            role = Role.Button,
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        )
    } else {
        Modifier
    }
    Box(
        modifier = modifier
            .size(dimens.seatCardSize)
            .clip(YapNPlayTheme.shapes.button)
            .background(if (active) seatColor.copy(alpha = 0.25f) else colors.Raised)
            .border(
                dimens.borderThin,
                if (active) seatColor else colors.TextDisabled.copy(alpha = 0.4f),
                YapNPlayTheme.shapes.button,
            )
            .then(clickableModifier),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
