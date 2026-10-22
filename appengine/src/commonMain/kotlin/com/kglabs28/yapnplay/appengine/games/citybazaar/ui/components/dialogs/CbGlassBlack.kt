package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.kglabs28.yapnplay.appengine.core.designsystem.AppBrushes
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * The glassy black the table's snackbar wears (dark smoked glass, a faint
 * sheen and a thin light rim), for City Royale's property cards and lists.
 */
@Composable
fun Modifier.cbGlassBlack(shape: Shape): Modifier {
    val colors = YapNPlayTheme.colors
    return this
        .background(Brush.verticalGradient(listOf(colors.SnackGlassTop, colors.SnackGlassBottom)), shape)
        .background(AppBrushes.glassSheen, shape)
        .border(YapNPlayTheme.dimens.hairline, Color.White.copy(alpha = RIM_ALPHA), shape)
}

private const val RIM_ALPHA = 0.14f
