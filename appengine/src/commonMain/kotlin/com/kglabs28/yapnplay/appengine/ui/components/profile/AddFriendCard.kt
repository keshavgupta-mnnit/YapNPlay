package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PersonAdd
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
 * "Add a friend": like the room-code card. Title, then a hint line that
 * becomes the result ("Request sent", "No one has that ID"…), then the
 * ID field and the purple ADD button.
 */
@Composable
fun AddFriendCard(
    code: String,
    message: String?,
    isError: Boolean,
    sending: Boolean,
    onCodeChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val ready = code.length == AppConstants.USER_CODE_LENGTH && !sending
    Column(
        modifier = modifier
            .glassPanel(glowColor = colors.IconMuted, surface = colors.RoomsPanel.copy(alpha = PANEL_ALPHA))
            .padding(horizontal = Spacing.Sm, vertical = Spacing.Xs),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xs, Alignment.CenterVertically),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
            IconCircle(imageVector = Icons.Rounded.PersonAdd, fill = colors.RoomsTicket, iconSize = dimens.iconMedium)
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs)) {
                BasicText(text = Strings.profileAddTitle, style = typography.sectionTitle.copy(color = colors.TextPrimary))
                BasicText(
                    text = message ?: Strings.profileAddHint(AppConstants.USER_CODE_LENGTH),
                    style = typography.micro.copy(color = when {
                        message == null -> colors.RoomsSubtitle
                        isError -> colors.Error
                        else -> colors.LiveGreen
                    }),
                    maxLines = 2,
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
            FriendCodeField(
                code = code,
                isError = isError && message != null,
                onCodeChange = onCodeChange,
                onDone = { if (ready) onSend() },
                modifier = Modifier.weight(1f),
            )
            PillButton(
                text = Strings.profileAddButton,
                onClick = onSend,
                enabled = ready,
                gradient = listOf(colors.CtaPurpleStart, colors.CtaPurpleEnd),
                contentColor = colors.TextPrimary,
                rimColor = colors.CtaPurpleRim,
                height = dimens.codeFieldHeight,
                trailingIcon = null,
                modifier = Modifier.width(dimens.joinButtonWidth),
            )
        }
    }
}

private const val PANEL_ALPHA = 0.9f
