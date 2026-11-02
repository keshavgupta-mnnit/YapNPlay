package com.kglabs28.yapnplay.appengine.ui.components.emojigift

import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/** One emoji in the picker: its art over a coin and its price; faded when the user can't afford it. */
@Composable
fun EmojiGiftTile(offer: EmojiGiftOfferUi, enabled: Boolean, onPick: () -> Unit, size: Dp, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val shape = RoundedCornerShape(dimens.cardRadius)
    Column(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(Brush.verticalGradient(listOf(colors.ArenaGlassTop, colors.ArenaGlassBottom)))
            .border(dimens.hairline, colors.ArenaGlassRim, shape)
            // Still tappable when short of coins: the picker then says what it costs.
            .clickable(enabled = enabled, role = Role.Button, onClick = onPick)
            .alpha(if (offer.affordable) 1f else UNAFFORDABLE_ALPHA),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        EmojiGiftArt(emojiId = offer.emojiId, size = size * EmojiGiftSizing.ART_OF_TILE)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xxxs)) {
            AppIcon(imageVector = Icons.Rounded.MonetizationOn, tint = colors.Gold, size = size * EmojiGiftSizing.COIN_OF_TILE)
            BasicText(
                text = offer.price,
                style = YapNPlayTheme.typography.micro.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold),
                maxLines = 1,
            )
        }
    }
}

private const val UNAFFORDABLE_ALPHA = 0.45f
