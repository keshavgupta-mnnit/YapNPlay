package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/** A big glowing pill (the designs' NO / YES): dark glass, a neon rim and glowing text in [color]. */
@Composable
fun CbNeonButton(text: String, color: Color, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, icon: ImageVector? = null) {
    val dimens = YapNPlayTheme.dimens
    Row(
        modifier = modifier
            .height(dimens.neonButtonHeight)
            .graphicsLayer { alpha = if (enabled) 1f else DISABLED_ALPHA }
            .shadow(elevation = dimens.neonButtonGlow, shape = CircleShape, ambientColor = color, spotColor = color)
            .clip(CircleShape)
            .background(Brush.verticalGradient(listOf(color.copy(alpha = FILL_TOP), color.copy(alpha = FILL_BOTTOM))))
            .border(dimens.promptRim, Brush.verticalGradient(listOf(Color.White.copy(alpha = RIM_SHINE), color)), CircleShape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs, Alignment.CenterHorizontally),
    ) {
        BasicText(
            text = text,
            style = YapNPlayTheme.typography.title.copy(color = Color.White, fontWeight = FontWeight.Black, shadow = Shadow(color = color, blurRadius = TEXT_GLOW)),
        )
        icon?.let { AppIcon(imageVector = it, tint = Color.White, size = dimens.iconMedium) }
    }
}

private const val DISABLED_ALPHA = 0.4f
private const val FILL_TOP = 0.55f
private const val FILL_BOTTOM = 0.25f
private const val RIM_SHINE = 0.6f
private const val TEXT_GLOW = 16f
