package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.WorkspacePremium
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
import androidx.compose.ui.text.style.TextOverflow
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.LudoStrings
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/**
 * The card-table top bar (design), every element a glass pill:
 *  - the game's logo, larger than the bar, then the title pill: the
 *    game's name and the room under it. With no spectators (Quick Play) it
 *    stretches over the free width;
 *  - the spectator pill (room matches): who is watching and how many;
 *  - the match clock, then the settings and exit squares.
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
    spectatorLabel: String,
    elapsed: String,
    onSettings: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
    /** The game's logo in the title pill's circle; null shows a crown. */
    logo: (@Composable (Modifier) -> Unit)? = null,
    /** A short switch before settings ("3D" / "2D"); null hides it. */
    switchLabel: String? = null,
    onSwitch: () -> Unit = {},
    /** Opens the game's history; null hides it. */
    onHistory: (() -> Unit)? = null,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val pill = RoundedCornerShape(percent = 50)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(if (logo != null) dimens.arenaLogoSize else dimens.arenaBarHeight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
    ) {
        // The game's logo stands on its own, bigger than the bar.
        logo?.invoke(Modifier.size(dimens.arenaLogoSize).clip(RoundedCornerShape(dimens.arenaLogoSize * LOGO_RADIUS)))
        // Title: name, room.
        Row(
            modifier = Modifier
                .height(dimens.arenaBarHeight)
                .then(if (showAudience) Modifier.widthIn(max = dimens.arenaBarHeight * TITLE_MAX) else Modifier.weight(1f))
                .clip(pill)
                .background(Brush.verticalGradient(listOf(colors.ArenaTitleTop, colors.ArenaTitleBottom)))
                .border(dimens.borderThin, colors.ArenaTitleRim.copy(alpha = RIM_ALPHA), pill)
                .padding(start = if (logo != null) Spacing.Lg else Spacing.Xxs, end = Spacing.Lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
        ) {
            if (logo == null) {
                Box(
                    modifier = Modifier
                        .size(dimens.arenaBarHeight - Spacing.Xs)
                        .clip(CircleShape)
                        .background(colors.Background.copy(alpha = CROWN_BACK_ALPHA)),
                    contentAlignment = Alignment.Center,
                ) {
                    AppIcon(imageVector = Icons.Rounded.WorkspacePremium, tint = colors.ArenaCrown, size = dimens.iconMedium)
                }
            }
            Column(verticalArrangement = Arrangement.Center) {
                BasicText(
                    text = title,
                    style = typography.title.copy(color = colors.TextPrimary, fontWeight = FontWeight.Black),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                BasicText(
                    text = subtitle,
                    style = typography.micro.copy(color = colors.TextSecondary),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        // Who is watching.
        if (showAudience) {
            Row(
                modifier = Modifier
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
                    audience.forEach { person -> TableAudienceMember(person) }
                }
                AppIcon(imageVector = Icons.Rounded.Groups, tint = colors.ArenaDrawRim, size = dimens.iconSmall)
                BasicText(text = spectatorLabel, style = typography.label.copy(color = colors.TextPrimary), maxLines = 1)
            }
            Spacer(Modifier.weight(1f))
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
            AppIcon(imageVector = Icons.Rounded.Schedule, tint = colors.TextPrimary, size = dimens.iconSmall)
            BasicText(text = elapsed, style = typography.title.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold), maxLines = 1)
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

private const val TITLE_MAX = 6f
private const val AUDIENCE_MAX = 4f
private const val SQUARE_RADIUS = 0.28f
private const val RIM_ALPHA = 0.7f
private const val CROWN_BACK_ALPHA = 0.55f
private const val LOGO_RADIUS = 0.24f
