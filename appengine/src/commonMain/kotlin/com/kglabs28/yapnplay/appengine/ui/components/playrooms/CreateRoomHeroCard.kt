package com.kglabs28.yapnplay.appengine.ui.components.playrooms

import com.kglabs28.yapnplay.appengine.core.designsystem.AdaptiveScale
import com.kglabs28.yapnplay.appengine.core.ui.RoomStrings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton
import com.kglabs28.yapnplay.appengine.ui.placeholders.CreateRoomGraphic

/**
 * Rooms tab hero card: "CREATE NEW / ROOM", pitch line and the green
 * CREATE ROOM button on a purple-to-navy glass card with a neon green and
 * purple rim, art on the right.
 */
@Composable
fun CreateRoomHeroCard(
    onCreateRoom: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    // A tablet's taller card gets bigger words (the phone keeps its sizes).
    val textScale = AdaptiveScale.forTablet(phone = 1f, tablet = TABLET_TEXT_SCALE)

    Row(
        modifier = modifier
            .glassPanel(
                glowColor = colors.FeatureIconPurple,
                // Neon: green along the top and left, purple along the right and bottom (design).
                rimBrush = Brush.linearGradient(listOf(colors.CtaGreenStart, colors.FeatureIconPurple)),
                rimWidth = YapNPlayTheme.dimens.neonRim,
                surfaceBrush = Brush.verticalGradient(
                    listOf(
                        colors.RoomsHeroTop.copy(alpha = 0.9f),
                        colors.RoomsHeroBottom.copy(alpha = 0.9f),
                    ),
                ),
            )
            .clickable(onClick = onCreateRoom)
            .padding(horizontal = Spacing.Md, vertical = Spacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
        ) {
            BasicText(
                text = RoomStrings.roomsCreateTitle,
                style = typography.cardTitle.copy(color = colors.TextPrimary, fontSize = typography.cardTitle.fontSize * textScale),
            )
            BasicText(
                text = RoomStrings.roomsCreateHero,
                style = typography.heroWord.copy(color = colors.GameZoneAccent, fontSize = typography.heroWord.fontSize * textScale),
            )
            Spacer(Modifier.padding(top = Spacing.Xxs))
            BasicText(
                text = RoomStrings.roomsCreateSubtitle,
                style = typography.micro.copy(color = colors.RoomsSubtitle, fontSize = typography.micro.fontSize * textScale),
            )
            Spacer(Modifier.padding(top = Spacing.Sm))
            PillButton(
                text = RoomStrings.roomsCreateButton,
                onClick = onCreateRoom,
                gradient = listOf(colors.CtaGlassGreen),
                contentColor = colors.TextPrimary,
                rimColor = colors.CtaGlassGreenRim,
                glassFill = colors.CtaGlassGreen,
                leadingIcon = Icons.Rounded.AddCircle,
            )
        }
        // The art fills the card's free side, whole (never cropped).
        // A tablet gives the art a bigger share, so it grows with the taller card.
        CreateRoomGraphic(modifier = Modifier.weight(AdaptiveScale.forTablet(phone = HERO_ART_WEIGHT, tablet = TABLET_HERO_ART_WEIGHT)).fillMaxHeight())
    }
}

/** The art's share of the card beside the text (text 1). */
private const val HERO_ART_WEIGHT = 1.7f
private const val TABLET_HERO_ART_WEIGHT = 2f

/** Text size on a tablet × the phone's. */
private const val TABLET_TEXT_SCALE = 1.25f
