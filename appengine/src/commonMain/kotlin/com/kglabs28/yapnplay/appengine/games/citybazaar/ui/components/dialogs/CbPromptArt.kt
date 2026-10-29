package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs

import com.kglabs28.yapnplay.appengine.core.ui.CityBazaarStrings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.HomeWork
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/**
 * The prompts' picture zone (as in the designs): a tilted name card in the
 * city's colour group, a glowing picture for that city, and for an upgrade
 * a glowing house. With [oldPrice] (an auction) the list price shows
 * crossed out before [price].
 */
@Composable
fun CbPromptArt(tile: Int, name: String, price: String, tint: Color, withHouse: Boolean, modifier: Modifier = Modifier, oldPrice: String? = null) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val shape = RoundedCornerShape(dimens.cardRadius)
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Md)) {
        // The city's card: its colour group, a house mark, the name and price.
        Column(
            modifier = Modifier
                .rotate(TILT)
                .width(dimens.promptNameCardWidth)
                .height(dimens.promptNameCardHeight)
                // A drawn glow: an elevation shadow showed through the see-through fill as a dark box.
                .cbRimGlow(tint, dimens.cardRadius)
                .clip(shape)
                .background(Brush.verticalGradient(listOf(tint, tint.copy(alpha = CARD_BOTTOM))))
                .border(dimens.promptRim, Brush.verticalGradient(listOf(Color.White.copy(alpha = RIM_SHINE), tint)), shape)
                .padding(Spacing.Xs),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            AppIcon(imageVector = Icons.Rounded.Home, tint = Color.White, size = dimens.iconSmall)
            BasicText(
                text = name.uppercase(),
                style = typography.label.copy(color = Color.White, fontWeight = FontWeight.Black, textAlign = TextAlign.Center, shadow = Shadow(color = Color.Black, blurRadius = TEXT_SHADOW)),
                maxLines = 2,
            )
            BasicText(
                text = buildAnnotatedString {
                    oldPrice?.let { withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) { append(it) }; append(" ") }
                    append(CityBazaarStrings.cbPriceTag(price))
                },
                style = typography.micro.copy(color = Color.White.copy(alpha = SUB_ALPHA), textAlign = TextAlign.Center),
            )
        }
        // The city's picture in a glowing disc (with a house when building).
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(dimens.promptArtIcon + Spacing.Md)
                    .shadow(elevation = dimens.promptGlow, shape = CircleShape, ambientColor = tint, spotColor = tint)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(tint.copy(alpha = DISC_CENTER), colors.Background)))
                    .border(dimens.borderThin, tint, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                AppIcon(imageVector = cityArtIcon(tile), tint = Color.White, size = dimens.promptArtIcon)
            }
            if (withHouse) {
                AppIcon(
                    imageVector = Icons.Rounded.HomeWork,
                    tint = colors.CbGain,
                    size = dimens.promptHouseIcon,
                    modifier = Modifier.align(Alignment.BottomEnd),
                )
            }
        }
    }
}

private const val TILT = -6f
private const val CARD_BOTTOM = 0.6f
private const val RIM_SHINE = 0.7f
private const val SUB_ALPHA = 0.85f
private const val TEXT_SHADOW = 8f
private const val DISC_CENTER = 0.55f
