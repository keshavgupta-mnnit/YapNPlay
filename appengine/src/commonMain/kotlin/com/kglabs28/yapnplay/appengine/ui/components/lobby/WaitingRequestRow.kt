package com.kglabs28.yapnplay.appengine.ui.components.lobby

import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import com.kglabs28.yapnplay.appengine.core.ui.RoomStrings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.HowToReg
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton

/** One person on the admin's Waiting List: their name, Decline and Let in. */
@Composable
fun WaitingRequestRow(
    name: String,
    onApprove: () -> Unit,
    onDecline: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        ShrinkText(
            text = name,
            style = YapNPlayTheme.typography.label.copy(color = colors.TextPrimary),
            modifier = Modifier.weight(1f),
        )
        GlassPillButton(
            text = RoomStrings.lobbyDeclineRequest,
            onClick = onDecline,
            leadingIcon = Icons.Rounded.Close,
            accent = colors.Error,
            contentColor = colors.Error,
        )
        PillButton(
            text = RoomStrings.lobbyLetIn,
            onClick = onApprove,
            gradient = listOf(colors.CtaGreenStart, colors.CtaGreenEnd),
            contentColor = colors.CtaGreenContent,
            leadingIcon = Icons.Rounded.HowToReg,
            trailingIcon = null,
            height = YapNPlayTheme.dimens.smallButtonHeight,
            textStyle = YapNPlayTheme.typography.sectionTitle,
        )
    }
}
