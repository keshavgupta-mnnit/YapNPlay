package com.kglabs28.yapnplay.appengine.ui.components.emojigift

import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.ui.components.ads.AdPill
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.common.GlowSpinner

/**
 * The emoji picker (design): "Send an emoji" over the user's coins, a
 * white close button, then the emojis three by three, each with its price.
 * The grid's width is the picker's: no empty room at its sides. A tap buys
 * that one for the seat it opened on; a note under the grid says when it
 * couldn't go (short of coins, no connection). An ad, as wide as the grid, at the bottom.
 * [tile] is each emoji's size (from the screen: EmojiGiftSizing), so the picker never covers the table.
 */
@Composable
fun EmojiGiftPicker(
    picker: EmojiGiftPickerUi,
    onPick: (emojiId: String) -> Unit,
    onClose: () -> Unit,
    tile: Dp,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val shape = RoundedCornerShape(dimens.cardRadius)
    val columns = picker.rows.maxOfOrNull { it.size } ?: DEFAULT_COLUMNS
    val gridWidth = EmojiGiftSizing.width(tile, columns, Spacing.Xs)
    Column(
        modifier = modifier
            .shadow(elevation = dimens.promptGlow, shape = shape, ambientColor = colors.ArenaDrawRim, spotColor = colors.ArenaDrawRim)
            .clip(shape)
            .background(Brush.verticalGradient(listOf(colors.ArenaDrawFillBottom, colors.ArenaGlassBottom)))
            .border(dimens.borderThin, colors.ArenaDrawRim, shape)
            .padding(Spacing.Xs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        Row(modifier = Modifier.width(gridWidth), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs)) {
            // The title over the coins, so both fit a narrow grid.
            Column(modifier = Modifier.weight(1f)) {
                ShrinkText(text = TableStrings.emojiGiftTitle, style = typography.label.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xxxs)) {
                    AppIcon(imageVector = Icons.Rounded.MonetizationOn, tint = colors.Gold, size = dimens.iconSmall)
                    BasicText(text = picker.coins, style = typography.micro.copy(color = colors.Gold, fontWeight = FontWeight.Bold), maxLines = 1)
                }
            }
            // Close: a white disc with a dark cross.
            Box(
                modifier = Modifier
                    .size(dimens.ludoMuteButton)
                    .clip(CircleShape)
                    .background(colors.TextPrimary)
                    .clickable(role = Role.Button, onClick = onClose)
                    .semantics { contentDescription = TableStrings.emojiGiftClose },
                contentAlignment = Alignment.Center,
            ) {
                AppIcon(imageVector = Icons.Rounded.Close, tint = colors.Background, size = dimens.iconSmall)
            }
        }
        if (picker.loading && picker.rows.isEmpty()) {
            Box(modifier = Modifier.width(gridWidth), contentAlignment = Alignment.Center) { GlowSpinner() }
        } else {
            picker.rows.forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
                    row.forEach { offer ->
                        EmojiGiftTile(offer = offer, enabled = !picker.sending, onPick = { onPick(offer.emojiId) }, size = tile)
                    }
                }
            }
        }
        picker.message?.let {
            BasicText(
                text = it,
                style = typography.micro.copy(color = colors.Error, textAlign = TextAlign.Center),
                modifier = Modifier.width(gridWidth),
            )
        }
        AdPill(modifier = Modifier.width(gridWidth), reserveSpace = true)
    }
}

/** The grid's width before the prices arrive (9 emojis, three by three). */
private const val DEFAULT_COLUMNS = 3
