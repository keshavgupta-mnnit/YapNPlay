package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.glass.ludoFrostedGlass
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoAvatarUi
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.PingQuality
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/**
 * Match top bar (design): the game name in brushed metal on a teal-rimmed
 * card with the room below it, a frosted tray of everyone at the table
 * and watching (ring green while talking, red with the mic off) with the
 * live spectator count, the live ping (teal good, yellow fair, red poor),
 * then settings and exit.
 */
@Composable
fun LudoMatchTopBar(
    title: String,
    subtitle: String,
    audience: List<LudoAvatarUi>,
    spectatorCount: String,
    spectatorLabel: String,
    pingLabel: String,
    pingQuality: PingQuality,
    onSettings: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val radius = dimens.ludoGlossRadius
    val shape = RoundedCornerShape(radius)
    val smoke = Brush.verticalGradient(listOf(colors.LudoSmokeTop, colors.LudoSmokeBottom))

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(dimens.ludoTopBarHeight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .clip(shape)
                .background(smoke)
                .border(dimens.borderThin, colors.LudoTeal, shape)
                .padding(horizontal = Spacing.Sm),
            verticalArrangement = Arrangement.Center,
        ) {
            LudoMetalTitle(text = title)
            BasicText(
                text = subtitle,
                style = typography.micro.copy(color = colors.TextSecondary),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .ludoFrostedGlass(tint = colors.IconMuted, shape = shape, radius = radius, glow = dimens.zero)
                .padding(horizontal = Spacing.Sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                audience.forEach { person -> LudoAudienceMember(person) }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs)) {
                    AppIcon(imageVector = Icons.Rounded.Groups, tint = colors.TextPrimary, size = dimens.iconSmall)
                    BasicText(text = spectatorCount, style = typography.title.copy(color = colors.TextPrimary))
                }
                BasicText(text = spectatorLabel, style = typography.micro.copy(color = colors.TextSecondary))
            }
        }
        val pingTint = pingQuality.tint()
        BasicText(
            text = pingLabel,
            style = typography.label.copy(color = pingTint),
            modifier = Modifier
                .clip(shape)
                .background(smoke)
                .border(dimens.borderThin, colors.LudoTeal, shape)
                .padding(horizontal = Spacing.Sm, vertical = Spacing.Xs),
        )
        LudoSquareIconButton(icon = Icons.Rounded.Settings, description = Strings.ludoSettings, tint = colors.LudoTeal, onClick = onSettings)
        LudoSquareIconButton(icon = Icons.AutoMirrored.Rounded.Logout, description = Strings.ludoExit, tint = colors.Error, onClick = onExit)
    }
}
