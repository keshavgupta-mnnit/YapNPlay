package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings

/** "DAILY BONUS": a glowing gold coin with "+100", a line, and COLLECT. */
@Composable
fun DailyBonusDialog(amount: String, onCollect: () -> Unit, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val shape = RoundedCornerShape(dimens.cardRadius)
    Column(
        modifier = modifier
            .width(dimens.dailyBonusWidth)
            .shadow(elevation = dimens.promptGlow, shape = shape, ambientColor = colors.Gold, spotColor = colors.Gold)
            .clip(shape)
            .background(colors.Background.copy(alpha = BASE))
            .background(Brush.verticalGradient(listOf(colors.Gold.copy(alpha = GLOW_TOP), Color.Transparent)))
            .border(dimens.promptRim, Brush.linearGradient(listOf(colors.Gold, colors.Warning, colors.Gold)), shape)
            .padding(Spacing.Md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
    ) {
        BasicText(text = Strings.dailyBonusTitle, style = typography.headline.copy(color = colors.Gold, fontWeight = FontWeight.Black, shadow = Shadow(color = colors.Gold, blurRadius = GLOW)))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
            Box(
                modifier = Modifier
                    .size(dimens.dailyBonusCoin)
                    .shadow(elevation = dimens.promptGlow, shape = CircleShape, ambientColor = colors.Gold, spotColor = colors.Gold)
                    .background(Brush.radialGradient(listOf(colors.Gold, colors.Warning)), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                AppIcon(imageVector = Icons.Rounded.MonetizationOn, tint = Color.White, size = dimens.dailyBonusCoin * COIN_ICON)
            }
            BasicText(text = amount, style = typography.headline.copy(color = Color.White, fontWeight = FontWeight.Black, fontSize = typography.headline.fontSize * AMOUNT_SCALE))
        }
        BasicText(text = Strings.dailyBonusBody, style = typography.label.copy(color = colors.TextSecondary, textAlign = TextAlign.Center))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimens.neonButtonHeight)
                .shadow(elevation = dimens.neonButtonGlow, shape = CircleShape, ambientColor = colors.Gold, spotColor = colors.Gold)
                .clip(CircleShape)
                .background(Brush.horizontalGradient(listOf(colors.Gold, colors.Warning)))
                .clickable(role = Role.Button, onClick = onCollect),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            BasicText(text = Strings.dailyBonusCollect, style = typography.title.copy(color = colors.TextOnCollectButton, fontWeight = FontWeight.Black))
        }
    }
}

private const val BASE = 0.92f
private const val GLOW_TOP = 0.25f
private const val GLOW = 16f
private const val COIN_ICON = 0.7f
private const val AMOUNT_SCALE = 1.6f
