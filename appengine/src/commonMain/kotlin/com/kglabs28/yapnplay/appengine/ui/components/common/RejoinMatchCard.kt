package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.Composable
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings

/**
 * "Your game is still on": rejoin the match [title], or [onDecline] to give
 * up the seat for good (the same as leaving from the table). The close cross
 * ([onDismiss]) only hides the card: the seat stays the user's.
 */
@Composable
fun RejoinMatchCard(
    title: String,
    onRejoin: () -> Unit,
    onDecline: () -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = YapNPlayTheme.colors
    ModalCard(
        title = Strings.rejoinTitle,
        subtitle = Strings.rejoinBody(title),
        onClose = onDismiss,
        actions = {
            GlassPillButton(text = Strings.rejoinDecline, onClick = onDecline)
            PillButton(
                text = Strings.rejoinAction,
                onClick = onRejoin,
                gradient = listOf(colors.CtaGreenStart, colors.CtaGreenEnd),
                contentColor = colors.CtaGreenContent,
                leadingIcon = Icons.Rounded.PlayArrow,
                trailingIcon = null,
                height = YapNPlayTheme.dimens.smallButtonHeight,
                textStyle = YapNPlayTheme.typography.sectionTitle,
            )
        },
    )
}
