package com.kglabs28.yapnplay.appengine.ui.components.playrooms

import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import com.kglabs28.yapnplay.appengine.core.ui.RoomStrings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ConfirmationNumber
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import com.kglabs28.yapnplay.appengine.ui.components.common.IconCircle
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton

/**
 * "Already have a room code?" card: ticket badge, title, hint line,
 * then the code field and the purple JOIN button side by side. An
 * [errorMessage] replaces the hint line in the error color.
 */
@Composable
fun JoinRoomCard(
    code: String,
    isJoining: Boolean,
    errorMessage: String?,
    keypadOpen: Boolean,
    onOpenKeypad: () -> Unit,
    onJoin: () -> Unit,
    modifier: Modifier = Modifier,
    onClearCode: () -> Unit = {},
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography

    Row(
        modifier = modifier
            .glassPanel(
                glowColor = colors.IconMuted,
                surface = colors.RoomsPanel.copy(alpha = 0.9f),
            )
            .padding(Spacing.Xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
    ) {
        // The "ROOM CODE" label on its own dark block, the card's full height.
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .clip(YapNPlayTheme.shapes.panel)
                .background(colors.Background.copy(alpha = LABEL_BLOCK_ALPHA))
                .padding(horizontal = Spacing.Md),
            contentAlignment = Alignment.Center,
        ) {
            BasicText(text = RoomStrings.roomsCodeLabel, style = typography.sectionTitle.copy(color = colors.TextSecondary), maxLines = 1)
        }
        Column(
            modifier = Modifier.weight(1f).padding(end = Spacing.Xs),
            verticalArrangement = Arrangement.spacedBy(Spacing.Xs, Alignment.CenterVertically),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
            ) {
                IconCircle(
                    imageVector = Icons.Rounded.ConfirmationNumber,
                    fill = colors.RoomsTicket,
                    iconSize = dimens.iconMedium,
                )
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs)) {
                    BasicText(
                        text = RoomStrings.roomsJoinTitle,
                        style = typography.sectionTitle.copy(color = colors.TextPrimary),
                    )
                    BasicText(
                        text = errorMessage ?: RoomStrings.roomsJoinSubtitle(AppConstants.ROOM_CODE_LENGTH),
                        style = typography.micro.copy(
                            color = if (errorMessage != null) colors.Error else colors.RoomsSubtitle,
                        ),
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
            ) {
                RoomCodeField(
                    code = code,
                    isError = errorMessage != null,
                    active = keypadOpen,
                    onActivate = onOpenKeypad,
                    onClear = onClearCode,
                    modifier = Modifier.weight(1f),
                )
                PillButton(
                    text = RoomStrings.roomsJoinButton,
                    onClick = onJoin,
                    enabled = !isJoining,
                    gradient = listOf(colors.JoinButtonTop, colors.JoinButtonBottom),
                    contentColor = colors.TextPrimary,
                    rimColor = colors.JoinButtonRim,
                    height = dimens.codeFieldHeight,
                    modifier = Modifier.width(dimens.joinButtonWidth),
                )
            }
        }
    }
}

/** How dark the ROOM CODE label block is. */
private const val LABEL_BLOCK_ALPHA = 0.45f
