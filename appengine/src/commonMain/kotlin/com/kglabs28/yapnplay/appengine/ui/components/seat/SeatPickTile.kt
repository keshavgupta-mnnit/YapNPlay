package com.kglabs28.yapnplay.appengine.ui.components.seat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/** A spectator (or the bot) in a seat picker: avatar and name; picked shows a tick and a cyan border. */
@Composable
fun SeatPickTile(
    label: String,
    isBot: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val shape = RoundedCornerShape(dimens.rowRadius)
    val avatarShape = RoundedCornerShape(dimens.ludoAvatarRadius)
    Box(modifier = modifier.width(dimens.seatTileWidth)) {
        Column(
            modifier = Modifier
                .clip(shape)
                .background(colors.RoomsSmallButton)
                .border(if (selected) dimens.seatTileSelectedBorder else dimens.hairline, if (selected) colors.Cyan else colors.IconMuted, shape)
                .clickable(onClick = onClick)
                .padding(Spacing.Xs),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
        ) {
            Box(
                modifier = Modifier
                    .size(dimens.seatTileAvatar)
                    .clip(avatarShape)
                    .background(Brush.verticalGradient(listOf(colors.CtaPurpleEnd, colors.Background)))
                    .border(dimens.hairline, if (selected) colors.Cyan else colors.IconMuted, avatarShape),
                contentAlignment = Alignment.Center,
            ) {
                AppIcon(
                    imageVector = if (isBot) Icons.Rounded.SmartToy else Icons.Rounded.Person,
                    tint = if (selected) colors.Cyan else colors.IconMuted,
                    size = dimens.seatTileAvatar / 2,
                )
            }
            BasicText(
                text = label,
                style = YapNPlayTheme.typography.label.copy(color = if (selected) colors.TextPrimary else colors.TextSecondary),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (selected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = dimens.seatTileTick / 3, y = -dimens.seatTileTick / 3)
                    .size(dimens.seatTileTick)
                    .clip(CircleShape)
                    .background(colors.Cyan),
                contentAlignment = Alignment.Center,
            ) {
                AppIcon(imageVector = Icons.Rounded.Check, tint = colors.Background, size = dimens.seatTileTick * TICK_SHARE)
            }
        }
    }
}

private const val TICK_SHARE = 0.7f
