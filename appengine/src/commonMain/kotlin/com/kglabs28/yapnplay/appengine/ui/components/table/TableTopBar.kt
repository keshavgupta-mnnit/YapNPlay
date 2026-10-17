package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/**
 * Match top bar (design): the game name in brushed metal on a teal-rimmed
 * card with the room below it, a frosted tray of everyone at the table
 * and watching (ring green while talking, red with the mic off) with the
 * spectator count (room matches only; Quick Play has no spectators), the
 * match clock (time since it began), then an optional switch (City
 * Bazaar's 2D / 3D), settings and exit.
 */
@Composable
fun TableTopBar(
    title: String,
    subtitle: String,
    audience: List<TableAvatarUi>,
    showAudience: Boolean,
    spectatorCount: String,
    spectatorLabel: String,
    elapsed: String,
    onSettings: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
    /** A short switch label before settings ("3D" / "2D"); null hides it. */
    switchLabel: String? = null,
    onSwitch: () -> Unit = {},
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
            // Title, tray, clock and buttons all as tall as the square buttons.
            .height(dimens.ludoSquareButton),
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
            TableMetalTitle(text = title)
            BasicText(
                text = subtitle,
                style = typography.micro.copy(color = colors.TextSecondary),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (showAudience) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .tableFrostedGlass(tint = colors.IconMuted, shape = shape, radius = radius, glow = dimens.zero)
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
                    audience.forEach { person -> TableAudienceMember(person) }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs)) {
                        AppIcon(imageVector = Icons.Rounded.Groups, tint = colors.TextPrimary, size = dimens.iconSmall)
                        BasicText(text = spectatorCount, style = typography.title.copy(color = colors.TextPrimary))
                    }
                    BasicText(text = spectatorLabel, style = typography.micro.copy(color = colors.TextSecondary))
                }
            }
        } else {
            Spacer(Modifier.weight(1f))
        }
        TableMatchClock(elapsed = elapsed, modifier = Modifier.fillMaxHeight())
        switchLabel?.let { TableTextSquareButton(text = it, onClick = onSwitch) }
        TableSquareIconButton(icon = Icons.Rounded.Settings, description = Strings.ludoSettings, tint = colors.LudoTeal, onClick = onSettings)
        TableSquareIconButton(icon = Icons.AutoMirrored.Rounded.Logout, description = Strings.ludoExit, tint = colors.Error, onClick = onExit)
    }
}
