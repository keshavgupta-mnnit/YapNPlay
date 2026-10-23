package com.kglabs28.yapnplay.appengine.ui.components.home

import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.graphics.Brush
import com.kglabs28.yapnplay.appengine.core.designsystem.SlantedSide
import com.kglabs28.yapnplay.appengine.core.designsystem.SlantedCardShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.ZoneCardHeader
import com.kglabs28.yapnplay.appengine.ui.placeholders.GameZoneFeaturePillarsRow
import com.kglabs28.yapnplay.appengine.ui.placeholders.GameZoneGraphic

/**
 * Left home card ("ENTER GAME ZONE"): header, game art, feature
 * pillars and the QUICK PLAY button. The whole card is tappable.
 */
@Composable
fun EnterGameZoneCard(
    onEnterGameZone: () -> Unit,
    /** How far its right edge leans (in from the bottom right to the top right). */
    slant: Dp,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    val shape = SlantedCardShape(dimens.cardRadius, slant, SlantedSide.END, dimens.homeCardInnerRadius)
    // The glass takes the taps, clipped to the leaning shape, so a press lights the card's own outline.
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            Modifier
                .matchParentSize()
                .glassPanel(
                    shape = shape,
                    glowColor = colors.GameZoneAccent,
                    surfaceBrush = Brush.verticalGradient(listOf(colors.HomeCardFrostTop, colors.HomeCardFrostBottom)),
                )
                .clip(shape)
                .clickable(onClick = onEnterGameZone),
        )
        GameZoneContent(onEnterGameZone = onEnterGameZone, slant = slant)
    }
}

@Composable
private fun GameZoneContent(onEnterGameZone: () -> Unit, slant: Dp) {
    val colors = YapNPlayTheme.colors
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.Md, vertical = Spacing.Sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        ZoneCardHeader(
            icon = Icons.Rounded.SportsEsports,
            iconFill = colors.HomeIconFrost,
            title = Strings.homeEnterGameZoneTitlePart1,
            titleAccent = Strings.homeEnterGameZoneTitlePart2,
            accentColor = colors.GameZoneAccent,
            subtitle = Strings.homeEnterGameZoneSubtitle,
            // Near the top the card is narrower by the whole lean.
            modifier = Modifier.padding(end = slant),
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                // Halfway down the card is narrower by half the lean.
                .padding(end = slant / 2),
            contentAlignment = Alignment.Center,
        ) {
            GameZoneGraphic()
        }

        GameZoneFeaturePillarsRow()

        PillButton(
            text = Strings.homeQuickPlay,
            onClick = onEnterGameZone,
            gradient = listOf(colors.CtaGlassGreen),
            contentColor = colors.TextPrimary,
            rimColor = colors.CtaGlassGreenRim,
            glassFill = colors.CtaGlassGreen,
            leadingIcon = Icons.Rounded.Bolt,
            height = YapNPlayTheme.dimens.homeCtaHeight,
            iconSize = YapNPlayTheme.dimens.homeBarIcon,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(AppConstants.HOME_CTA_WIDTH_FRACTION),
        )
    }
}
