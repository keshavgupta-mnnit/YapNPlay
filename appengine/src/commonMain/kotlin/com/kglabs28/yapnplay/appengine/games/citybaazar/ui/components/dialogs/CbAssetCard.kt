package com.kglabs28.yapnplay.appengine.games.citybaazar.ui.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/**
 * A small rate-card tile in a sideways list: the tile's group colour on
 * top, its name, a line ("Sell 1 house", "Hotel · ₹2,000") and a value.
 * Picked shows a tick and a cyan border.
 */
@Composable
fun CbAssetCard(
    name: String,
    header: Color,
    line: String,
    value: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val shape = RoundedCornerShape(dimens.cbRowRadius)
    Box(modifier = modifier.width(dimens.cbAssetCardWidth)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(colors.RoomsSmallButton)
                .border(if (selected) dimens.seatTileSelectedBorder else dimens.hairline, if (selected) colors.Cyan else colors.IconMuted, shape)
                .clickable(onClick = onClick),
            verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs),
        ) {
            Box(Modifier.fillMaxWidth().height(dimens.cbHeaderChip).background(header))
            Column(modifier = Modifier.padding(horizontal = Spacing.Xs, vertical = Spacing.Xxs)) {
                BasicText(text = name, style = typography.label.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold), maxLines = 1, overflow = TextOverflow.Ellipsis)
                BasicText(text = line, style = typography.micro.copy(color = colors.TextSecondary), maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (value.isNotEmpty()) BasicText(text = value, style = typography.label.copy(color = colors.CbGain, fontWeight = FontWeight.Bold))
            }
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
