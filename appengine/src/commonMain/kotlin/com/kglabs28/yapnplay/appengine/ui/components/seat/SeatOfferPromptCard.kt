package com.kglabs28.yapnplay.appengine.ui.components.seat

import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EventSeat
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton

/** Asks a spectator to take a seat in any game; [acceptLabel] carries the seconds left. */
@Composable
fun SeatOfferPromptCard(
    title: String,
    body: String,
    acceptLabel: String,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    ModalCard(
        title = title,
        subtitle = body,
        modifier = modifier,
        actions = {
            GlassPillButton(text = TableStrings.seatDecline, onClick = onDecline, leadingIcon = Icons.Rounded.Close, height = YapNPlayTheme.dimens.ctaHeight)
            PillButton(
                text = acceptLabel,
                onClick = onAccept,
                gradient = listOf(colors.CtaPurpleStart, colors.CtaPurpleEnd),
                contentColor = colors.TextPrimary,
                rimColor = colors.CtaPurpleRim,
                leadingIcon = Icons.Rounded.EventSeat,
                trailingIcon = null,
                textStyle = YapNPlayTheme.typography.sectionTitle,
            )
        },
    )
}
