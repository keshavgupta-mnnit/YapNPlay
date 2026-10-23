package com.kglabs28.yapnplay.appengine.ui.components.home

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
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val slant = dimens.homeCardSlant

    Column(
        modifier = modifier
            .fillMaxSize()
            .glassPanel(
                shape = SlantedCardShape(dimens.cardRadius, slant, SlantedSide.END),
                glowColor = colors.GameZoneAccent,
                surfaceBrush = Brush.verticalGradient(listOf(colors.HomeCardFrostTop, colors.HomeCardFrostBottom)),
            )
            .clickable(onClick = onEnterGameZone)
            // Clear of the leaning edge.
            .padding(end = Spacing.Md + slant, start = Spacing.Md, top = Spacing.Sm, bottom = Spacing.Sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        ZoneCardHeader(
            icon = Icons.Rounded.SportsEsports,
            iconFill = colors.GameZoneIconFill,
            title = Strings.homeEnterGameZoneTitlePart1,
            titleAccent = Strings.homeEnterGameZoneTitlePart2,
            accentColor = colors.GameZoneAccent,
            subtitle = Strings.homeEnterGameZoneSubtitle,
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            GameZoneGraphic()
        }

        GameZoneFeaturePillarsRow()

        PillButton(
            text = Strings.homeQuickPlay,
            onClick = onEnterGameZone,
            gradient = listOf(colors.CtaGreenStart, colors.CtaGreenMid, colors.CtaGreenEnd),
            contentColor = colors.CtaGreenContent,
            leadingIcon = Icons.Rounded.Bolt,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(AppConstants.HOME_CTA_WIDTH_FRACTION),
        )
    }
}
