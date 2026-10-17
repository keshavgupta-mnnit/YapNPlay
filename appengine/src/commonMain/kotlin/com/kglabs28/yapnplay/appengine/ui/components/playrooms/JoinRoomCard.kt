package com.kglabs28.yapnplay.appengine.ui.components.playrooms

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
import com.kglabs28.yapnplay.appengine.core.ui.Strings
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
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography

    Column(
        modifier = modifier
            .glassPanel(
                glowColor = colors.IconMuted,
                surface = colors.RoomsPanel.copy(alpha = 0.9f),
            )
            .padding(horizontal = Spacing.Md, vertical = Spacing.Xs),
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
                    text = Strings.roomsJoinTitle,
                    style = typography.sectionTitle.copy(color = colors.TextPrimary),
                )
                BasicText(
                    text = errorMessage ?: Strings.roomsJoinSubtitle(AppConstants.ROOM_CODE_LENGTH),
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
                modifier = Modifier.weight(1f),
            )
            PillButton(
                text = Strings.roomsJoinButton,
                onClick = onJoin,
                enabled = !isJoining,
                gradient = listOf(colors.CtaPurpleStart, colors.CtaPurpleEnd),
                contentColor = colors.TextPrimary,
                rimColor = colors.CtaPurpleRim,
                height = dimens.codeFieldHeight,
                modifier = Modifier.width(dimens.joinButtonWidth),
            )
        }
    }
}
