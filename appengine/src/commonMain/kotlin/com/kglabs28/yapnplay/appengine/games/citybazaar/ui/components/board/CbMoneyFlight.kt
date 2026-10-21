package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CurrencyRupee
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbMoneyFlightUi
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Money popups off: a gold ₹ chip with the amount arcs from [from] (the
 * payer's card, or the bank) to [to] (the receiver), then fades. Positions
 * are in the parent's coordinates.
 */
@Composable
fun CbMoneyFlight(flight: CbMoneyFlightUi, from: Offset, to: Offset, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val arc = with(LocalDensity.current) { dimens.moneyFlightArc.toPx() }
    val progress = remember(flight.id) { Animatable(0f) }
    var size by remember { mutableStateOf(IntSize.Zero) }
    LaunchedEffect(flight.id) { progress.animateTo(1f, tween(AppConstants.CB_MONEY_FLIGHT_MS.toInt(), easing = FastOutSlowInEasing)) }
    val p = progress.value
    val at = lerp(from, to, p) - Offset(size.width / 2f, size.height / 2f + arc * sin(PI * p).toFloat())
    Row(
        modifier = modifier
            .offset { IntOffset(at.x.roundToInt(), at.y.roundToInt()) }
            .onSizeChanged { size = it }
            .graphicsLayer {
                alpha = if (p > FADE_FROM) (1f - p) / (1f - FADE_FROM) else 1f
                val grow = 1f + GROW * sin(PI * p).toFloat()
                scaleX = grow
                scaleY = grow
            }
            .shadow(elevation = dimens.bankButtonGlow, shape = CircleShape, ambientColor = colors.Gold, spotColor = colors.Gold)
            .background(colors.Background, CircleShape)
            .border(dimens.borderThin, colors.Gold, CircleShape)
            .padding(horizontal = Spacing.Sm, vertical = Spacing.Xxs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        AppIcon(imageVector = Icons.Rounded.CurrencyRupee, tint = colors.Gold, size = dimens.iconSmall)
        BasicText(text = flight.amount, style = YapNPlayTheme.typography.label.copy(color = colors.Gold, fontWeight = FontWeight.Bold))
    }
}

private const val FADE_FROM = 0.85f
private const val GROW = 0.35f
