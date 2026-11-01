package com.kglabs28.yapnplay.appengine.ui.components.table

import com.kglabs28.yapnplay.appengine.ui.placeholders.HomeLogoArtwork
import com.kglabs28.yapnplay.appengine.ui.components.common.StableDigitsText
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.LudoStrings
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/**
 * The card-table top bar (design), every element a glass pill:
 *  - the app's logo, larger than the bar, then the title pill: the
 *    game's name and the room under it, stretching over the free width;
 *  - the spectator pill, only while someone watches (room matches), as
 *    tall as the title pill and only as wide as its faces: who is watching
 *    and how many; a long press on one mutes them for the user;
 *  - the connection (signal bars and ping) and the match clock in one
 *    pill, then the settings and exit squares.
 *
 * Shared by MONO, Royal Ludo and City Royale (which adds its 2D / 3D
 * switch and history).
 */
@Composable
fun ArenaTopBar(
    title: String,
    subtitle: String,
    audience: List<TableAvatarUi>,
    showAudience: Boolean,
    /** How many watch, by the group icon (the icon says who they are: no word, less room). */
    spectatorCount: String,
    elapsed: String,
    onSettings: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
    /** A short switch before settings ("3D" / "2D"); null hides it. */
    switchLabel: String? = null,
    onSwitch: () -> Unit = {},
    /** Opens the game's history; null hides it. */
    onHistory: (() -> Unit)? = null,
    /** Mutes or unmutes a spectator (by user id) for the user only, from a long press on them; null: no long press. */
    onToggleHearSpectator: ((String) -> Unit)? = null,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val pill = RoundedCornerShape(percent = 50)
    // Nobody watching: no pill, the title takes the room.
    val watched = showAudience && audience.isNotEmpty()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(dimens.arenaLogoSize),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
    ) {
        // The app's logo stands on its own, bigger than the bar (the same on every game's table).
        HomeLogoArtwork(modifier = Modifier.size(dimens.arenaLogoSize).clip(RoundedCornerShape(dimens.arenaLogoSize * LOGO_RADIUS)))
        // Title: name, room.
        Row(
            modifier = Modifier
                .height(dimens.arenaBarHeight)
                // Weighted, so the clock, the squares and the spectator pill are measured first and never squeezed.
                .weight(1f)
                .clip(pill)
                .background(Brush.verticalGradient(listOf(colors.ArenaTitleTop, colors.ArenaTitleBottom)))
                .border(dimens.borderThin, colors.ArenaTitleRim.copy(alpha = RIM_ALPHA), pill)
                .padding(start = Spacing.Lg, end = Spacing.Lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
        ) {
            // The wide bar reads in one line: the game's name, big and bold, at the start; the mode and round
            // as one line at the end. In a room match the pill is shorter: both shrink to fit rather than being cut.
            ShrinkText(
                text = title,
                style = typography.title.copy(color = colors.TextPrimary, fontWeight = FontWeight.Black, fontSize = typography.title.fontSize * TITLE_GROW),
                modifier = Modifier.weight(1f),
            )
            ShrinkText(
                text = subtitle,
                style = typography.label.copy(
                    color = colors.TextSecondary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = typography.label.fontSize * SUBTITLE_GROW,
                    textAlign = TextAlign.End,
                ),
                // Half the bar, its text at the right end: shrinks to fit, so a long "Room · Round 12 / 30" never hides the name.
                modifier = Modifier.weight(1f),
            )
        }

        // Who is watching.
        if (watched) {
            Row(
                modifier = Modifier
                    // As wide as its faces (up to a few, then they scroll): no empty glass.
                    .height(dimens.arenaBarHeight)
                    .arenaGlass(pill)
                    .padding(horizontal = Spacing.Sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
            ) {
                Row(
                    modifier = Modifier
                        .widthIn(max = dimens.arenaBarHeight * AUDIENCE_MAX)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    audience.forEach { person ->
                        TableAudienceMember(person, onToggleHear = onToggleHearSpectator?.let { toggle -> { toggle(person.userId) } })
                    }
                }
                AppIcon(imageVector = Icons.Rounded.Groups, tint = colors.ArenaDrawRim, size = dimens.iconSmall)
                BasicText(text = spectatorCount, style = typography.label.copy(color = colors.TextPrimary), maxLines = 1)
            }
        }

        // The match clock.
        Row(
            modifier = Modifier
                .height(dimens.arenaBarHeight)
                .arenaGlass(pill)
                .padding(horizontal = Spacing.Md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
        ) {
            // The connection first: a lag reads as the network's, not the app's.
            ConnectionBadge()
            AppIcon(imageVector = Icons.Rounded.Schedule, tint = colors.TextPrimary, size = dimens.iconSmall)
            StableDigitsText(text = elapsed, style = typography.title.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold))
        }
        switchLabel?.let { label ->
            Box(
                modifier = Modifier
                    .size(dimens.arenaBarHeight)
                    .arenaGlass(RoundedCornerShape(dimens.arenaBarHeight * SQUARE_RADIUS))
                    .clickable(role = Role.Button, onClick = onSwitch),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(text = label, style = typography.label.copy(color = colors.Cyan, fontWeight = FontWeight.Black), maxLines = 1)
            }
        }
        onHistory?.let { ArenaSquareButton(icon = Icons.Rounded.History, description = TableStrings.tableHistory, tint = colors.Cyan, onClick = it) }
        ArenaSquareButton(icon = Icons.Rounded.Settings, description = LudoStrings.ludoSettings, tint = colors.Cyan, onClick = onSettings)
        ArenaSquareButton(icon = Icons.AutoMirrored.Rounded.Logout, description = LudoStrings.ludoExit, tint = colors.Error, rim = colors.Error, onClick = onExit)
    }
}

/** A square glass button in the top bar. */
@Composable
fun ArenaSquareButton(
    icon: ImageVector,
    description: String,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    rim: Color = YapNPlayTheme.colors.ArenaGlassRim,
) {
    val dimens = YapNPlayTheme.dimens
    val shape = RoundedCornerShape(dimens.arenaBarHeight * SQUARE_RADIUS)
    Box(
        modifier = modifier
            .size(dimens.arenaBarHeight)
            .arenaGlass(shape, rim)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        AppIcon(imageVector = icon, tint = tint, size = dimens.iconMedium)
    }
}

/** The arena's dark violet glass, with a fine rim. */
@Composable
fun Modifier.arenaGlass(
    shape: androidx.compose.ui.graphics.Shape,
    rim: Color = YapNPlayTheme.colors.ArenaGlassRim,
): Modifier {
    val colors = YapNPlayTheme.colors
    return this
        .clip(shape)
        .background(Brush.verticalGradient(listOf(colors.ArenaGlassTop, colors.ArenaGlassBottom)))
        .border(YapNPlayTheme.dimens.hairline, rim, shape)
}

private const val AUDIENCE_MAX = 4f
private const val SQUARE_RADIUS = 0.28f
private const val RIM_ALPHA = 0.7f
private const val LOGO_RADIUS = 0.24f
/** The bar's game name and its mode / round line, a size up from their styles. */
private const val TITLE_GROW = 1.25f
private const val SUBTITLE_GROW = 1.15f
