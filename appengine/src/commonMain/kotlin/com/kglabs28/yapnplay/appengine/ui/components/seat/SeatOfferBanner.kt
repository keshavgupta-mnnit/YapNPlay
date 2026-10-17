package com.kglabs28.yapnplay.appengine.ui.components.seat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EventSeat
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/** "Paused · Riya's seat offered to Karan · 24s": a glass strip over any game's table while a seat changes hands. */
@Composable
fun SeatOfferBanner(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    Row(
        modifier = modifier
            .glassPill(glowColor = colors.CtaPurpleRim, surface = colors.RoomsSmallButton)
            .padding(horizontal = Spacing.Md, vertical = Spacing.Xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        AppIcon(imageVector = Icons.Rounded.EventSeat, tint = colors.CtaPurpleRim, size = YapNPlayTheme.dimens.iconSmall)
        BasicText(text = text, style = YapNPlayTheme.typography.label.copy(color = colors.TextPrimary))
    }
}
