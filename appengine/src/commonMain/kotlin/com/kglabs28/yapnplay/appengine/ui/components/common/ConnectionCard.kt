package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.core.designsystem.AppBrushes
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.home.HomeCenterLogo

/**
 * The connection card (connecting, can't reach, connection lost): the app
 * logo, a glowing spinner while trying (a Wi-Fi-off mark when offline), the
 * message, and Retry when offline.
 */
@Composable
fun ConnectionCard(title: String, body: String, onRetry: (() -> Unit)?, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val shape = RoundedCornerShape(dimens.cardRadius)
    Column(
        modifier = modifier
            .width(dimens.connectionCardWidth)
            .shadow(elevation = dimens.promptGlow, shape = shape, ambientColor = colors.Cyan, spotColor = colors.SocialZoneAccent)
            .clip(shape)
            .background(AppBrushes.dialogGeneric)
            .border(dimens.promptRim, Brush.linearGradient(listOf(colors.Cyan, colors.SocialZoneAccent, colors.GameZoneAccent)), shape)
            .padding(Spacing.Md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
    ) {
        HomeCenterLogo()
        if (onRetry == null) {
            GlowSpinner()
        } else {
            AppIcon(imageVector = Icons.Rounded.WifiOff, tint = colors.Error, size = dimens.connectionSpinner)
        }
        BasicText(text = title, style = typography.title.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center))
        BasicText(text = body, style = typography.label.copy(color = colors.TextSecondary, textAlign = TextAlign.Center))
        onRetry?.let {
            GlassPillButton(text = Strings.connectionRetry, onClick = it, leadingIcon = Icons.Rounded.Refresh, accent = colors.Cyan)
        }
    }
}

