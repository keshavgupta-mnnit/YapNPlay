package com.kglabs28.yapnplay.appengine.ui.components.seat

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.SeatPickOptionUi
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.common.CutCornerButton

/**
 * The seat dialog shared by every game (design: "Seat Inactive"): amber
 * warning title with the time left at the top right, the [message], a
 * sideways list of [options] (spectators, and the bot when leaving) where
 * the pick shows a tick and a cyan border, then two buttons. Used by the
 * leader (Offer to Bot / Invite to Seat), the idle player (I'm Here /
 * Spectate) and on exit (Cancel / Exit).
 */
@Composable
fun SeatPickerDialog(
    title: String,
    message: String,
    options: List<SeatPickOptionUi>,
    selectedIndex: Int?,
    onSelect: (Int) -> Unit,
    secondaryLabel: String,
    onSecondary: () -> Unit,
    primaryLabel: String,
    onPrimary: () -> Unit,
    modifier: Modifier = Modifier,
    countdown: String? = null,
    primaryEnabled: Boolean = true,
    /** Short lines under the message (a vote's tally, who it waits for). */
    footer: List<String> = emptyList(),
    /** False hides the two buttons (a vote the user can't take part in). */
    showActions: Boolean = true,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    Column(
        modifier = modifier
            .width(dimens.seatDialogWidth)
            // Frosted like the member cards, over the dimmed table.
            .glassPanel(
                glowColor = colors.Warning,
                surfaceBrush = Brush.verticalGradient(listOf(colors.MemberCardTop, colors.MemberCardBottom)),
            )
            .padding(Spacing.Lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
            ) {
                AppIcon(imageVector = Icons.Rounded.Warning, tint = colors.Warning, size = dimens.iconMedium)
                BasicText(text = title, style = typography.headline.copy(color = colors.Warning))
            }
            countdown?.let { SeatTimerChip(text = it, modifier = Modifier.align(Alignment.CenterEnd)) }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimens.hairline)
                .background(colors.Warning.copy(alpha = DIVIDER_ALPHA)),
        )
        BasicText(text = message, style = typography.body.copy(color = colors.TextSecondary, textAlign = TextAlign.Center))
        if (options.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = Spacing.Xs),
                horizontalArrangement = Arrangement.spacedBy(Spacing.Sm, Alignment.CenterHorizontally),
            ) {
                options.forEachIndexed { index, option ->
                    SeatPickTile(
                        label = option.label,
                        isBot = option.isBot,
                        selected = index == selectedIndex,
                        onClick = { onSelect(index) },
                    )
                }
            }
        }
        footer.forEach { line -> BasicText(text = line, style = typography.label.copy(color = colors.Cyan, textAlign = TextAlign.Center)) }
        if (showActions) Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.Sm, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // The START MATCH design: dark glass for the second choice, glowing gold for the main one.
            CutCornerButton(text = secondaryLabel, onClick = onSecondary, lit = false)
            CutCornerButton(text = primaryLabel, onClick = onPrimary, enabled = primaryEnabled)
        }
    }
}

private const val DIVIDER_ALPHA = 0.35f
