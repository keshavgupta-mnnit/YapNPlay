package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings

/**
 * The designs' balance box: "Current Balance ₹… | Property Cost -₹…" side by
 * side (a glowing divider between), "Remaining Balance: ₹…" under them.
 */
@Composable
fun CbBalanceStrip(
    cash: String,
    changeLabel: String,
    change: String,
    remaining: String,
    modifier: Modifier = Modifier,
    changeColor: Color = YapNPlayTheme.colors.CbLoss,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val shape = RoundedCornerShape(dimens.cardRadius)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = BOX_ALPHA), shape)
            .border(dimens.borderThin, Brush.horizontalGradient(listOf(colors.Cyan.copy(alpha = RIM_ALPHA), colors.SocialZoneAccent.copy(alpha = RIM_ALPHA))), shape)
            .padding(horizontal = Spacing.Xs, vertical = Spacing.Xxs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs),
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min), verticalAlignment = Alignment.CenterVertically) {
            Figure(label = Strings.cbCurrentBalanceLabel, value = cash, color = colors.Cyan, modifier = Modifier.weight(1f))
            Box(Modifier.width(dimens.hairline * 2).fillMaxHeight().background(Brush.verticalGradient(listOf(Color.Transparent, colors.TextSecondary, Color.Transparent))))
            Figure(label = changeLabel, value = change, color = changeColor, modifier = Modifier.weight(1f))
        }
        BasicText(
            text = buildAnnotatedString {
                append(Strings.cbRemainingBalance(remaining).removeSuffix(remaining))
                withStyle(SpanStyle(color = colors.CbGain, fontWeight = FontWeight.Bold)) { append(remaining) }
            },
            style = typography.micro.copy(color = colors.TextSecondary, textAlign = TextAlign.Center),
        )
    }
}

@Composable
private fun Figure(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        BasicText(text = label, style = YapNPlayTheme.typography.micro.copy(color = YapNPlayTheme.colors.TextSecondary))
        BasicText(text = value, style = YapNPlayTheme.typography.label.copy(color = color, fontWeight = FontWeight.Bold))
    }
}

private const val BOX_ALPHA = 0.45f
private const val RIM_ALPHA = 0.6f
