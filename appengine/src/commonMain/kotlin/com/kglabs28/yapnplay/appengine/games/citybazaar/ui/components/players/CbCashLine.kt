package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.players

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.RupeeFormat

/** A player's balance on their card; it counts to a new value, green going up, red going down. */
@Composable
fun CbCashLine(cash: Int, compact: Boolean, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    val shown by animateIntAsState(cash, tween(AppConstants.CB_CASH_COUNT_MS))
    val colour = when {
        shown < cash -> colors.CbGain
        shown > cash -> colors.CbLoss
        else -> colors.CbCash
    }
    val style = if (compact) typography.micro else typography.label
    BasicText(text = Strings.cbRupees(RupeeFormat.digits(shown)), style = style.copy(color = colour, fontWeight = FontWeight.Bold), maxLines = 1, modifier = modifier)
}
