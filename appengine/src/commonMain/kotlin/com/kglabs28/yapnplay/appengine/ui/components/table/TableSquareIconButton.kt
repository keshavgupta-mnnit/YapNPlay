package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/** A rounded-square smoked-glass icon button with a [tint] rim (match top bar). */
@Composable
fun TableSquareIconButton(
    icon: ImageVector,
    description: String,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val shape = RoundedCornerShape(dimens.ludoGlossRadius)
    Box(
        modifier = modifier
            .size(dimens.ludoSquareButton)
            .clip(shape)
            .background(Brush.verticalGradient(listOf(colors.LudoSmokeTop, colors.LudoSmokeBottom)))
            .drawWithContent {
                drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = GLOSS_ALPHA), Color.Transparent), 0f, size.height / 2))
                drawContent()
            }
            .border(dimens.borderThin, tint.copy(alpha = RIM_ALPHA), shape)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        AppIcon(imageVector = icon, tint = tint)
    }
}

private const val GLOSS_ALPHA = 0.25f
private const val RIM_ALPHA = 0.75f
