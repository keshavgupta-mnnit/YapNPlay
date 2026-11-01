package com.kglabs28.yapnplay.appengine.ui.components.emojigift

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
 * The emoji picker (design): "Send an emoji", the user's coins and a close
 * cross, then the emojis four to a row, each with its price. A tap buys
 * that one for the seat it opened on; a note under the grid says when it
 * couldn't go (short of coins, no connection).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmojiGiftPicker(
    picker: EmojiGiftPickerUi,
    onPick: (emojiId: String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val shape = RoundedCornerShape(dimens.cardRadius)
    val gridWidth = dimens.emojiGiftTile * COLUMNS + Spacing.Xs * (COLUMNS - 1)
    Column(
        modifier = modifier
            .shadow(elevation = dimens.promptGlow, shape = shape, ambientColor = colors.ArenaDrawRim, spotColor = colors.ArenaDrawRim)
            .clip(shape)
            .background(Brush.verticalGradient(listOf(colors.ArenaDrawFillBottom, colors.ArenaGlassBottom)))
            .border(dimens.borderThin, colors.ArenaDrawRim, shape)
            .padding(Spacing.Sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        Row(modifier = Modifier.width(gridWidth), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
            BasicText(
                text = TableStrings.emojiGiftTitle,
                style = typography.label.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold),
                maxLines = 1,
                modifier = Modifier.weight(1f),
            )
            AppIcon(imageVector = Icons.Rounded.MonetizationOn, tint = colors.Gold, size = dimens.iconSmall)
            BasicText(text = picker.coins, style = typography.label.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold), maxLines = 1)
            Box(
                modifier = Modifier
                    .size(dimens.ludoMuteButton)
                    .clip(CircleShape)
                    .clickable(role = Role.Button, onClick = onClose)
                    .semantics { contentDescription = TableStrings.emojiGiftClose },
                contentAlignment = Alignment.Center,
            ) {
                AppIcon(imageVector = Icons.Rounded.Close, tint = colors.TextPrimary, size = dimens.iconSmall)
            }
        }
        if (picker.loading && picker.offers.isEmpty()) {
            Box(modifier = Modifier.width(gridWidth), contentAlignment = Alignment.Center) { GlowSpinner() }
        } else {
            FlowRow(
                modifier = Modifier.width(gridWidth),
                maxItemsInEachRow = COLUMNS,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
                verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
            ) {
                picker.offers.forEach { offer ->
                    EmojiGiftTile(offer = offer, enabled = !picker.sending, onPick = { onPick(offer.emojiId) })
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
    }
}

private const val COLUMNS = 4
