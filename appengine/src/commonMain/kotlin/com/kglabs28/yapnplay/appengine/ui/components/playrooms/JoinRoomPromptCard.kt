package com.kglabs28.yapnplay.appengine.ui.components.playrooms

import com.kglabs28.yapnplay.appengine.core.ui.RoomStrings
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton

/**
 * "Join College Crew?" — shown before joining a room from an invite link
 * or a typed code: room name, member counts, tagline, Yes / Cancel.
 */
@Composable
fun JoinRoomPromptCard(
    title: String,
    details: String,
    description: String,
    onJoin: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography

    ModalCard(
        title = title,
        subtitle = details,
        modifier = modifier,
        actions = {
            GlassPillButton(
                text = RoomStrings.roomsJoinCancel,
                onClick = onCancel,
                leadingIcon = Icons.Rounded.Close,
                height = YapNPlayTheme.dimens.ctaHeight,
            )
            PillButton(
                text = RoomStrings.roomsJoinYes,
                onClick = onJoin,
                gradient = listOf(colors.CtaGreenStart, colors.CtaGreenMid, colors.CtaGreenEnd),
                contentColor = colors.CtaGreenContent,
                leadingIcon = Icons.Rounded.GroupAdd,
                trailingIcon = null,
                textStyle = typography.sectionTitle,
            )
        },
    ) {
        if (description.isNotBlank()) {
            BasicText(text = description, style = typography.label.copy(color = colors.RoomsSubtitle))
        }
    }
}
