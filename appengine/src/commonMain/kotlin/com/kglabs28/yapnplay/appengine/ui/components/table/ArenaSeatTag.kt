package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * A seat's status tag ("In Jail", "Knocked out", "2nd", "Away"): a small
 * pill in [tint], placed under the seat's header ([ArenaSeatHeader] of an
 * [avatar]-sized avatar) right of the avatar, a hair below the name pill,
 * so it never covers the avatar. It takes no room, so the seat stays put
 * when it appears. Shared by every table.
 */
@Composable
fun ArenaSeatTag(text: String, tint: Color, avatar: Dp, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    // A long word ("ELIMINATED") on a narrow seat shrinks to fit rather than being cut.
    ShrinkText(
        text = text,
        style = YapNPlayTheme.typography.micro.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold),
        modifier = modifier
            // Drawn under the seat but takes no height, so a tag coming or going never moves the seat.
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, 0) { placeable.place(0, 0) }
            }
            // The header is as tall as the avatar and the pill is centred in it: rise to the pill's bottom, then the gap.
            .padding(start = avatar + Spacing.Xxs)
            .offset(y = Spacing.Xxxs - avatar * (1f - SEAT_PILL_SHARE) / 2)
            .clip(YapNPlayTheme.shapes.pill)
            .background(lerp(tint, colors.Background, TAG_DARKEN))
            .border(YapNPlayTheme.dimens.hairline, tint, YapNPlayTheme.shapes.pill)
            .padding(horizontal = Spacing.Xs, vertical = Spacing.Xxxs),
    )
}

/** A seat's name pill is this tall (× the avatar). */
internal const val SEAT_PILL_SHARE = 0.84f
private const val TAG_DARKEN = 0.45f
