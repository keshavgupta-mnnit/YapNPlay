package com.kglabs28.yapnplay.appengine.ui.placeholders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton

/**
 * Bottom banner inside "PLAY WITH FRIENDS": the pitch line and the dark
 * purple "Create Room" button, on charcoal glass (design).
 */
@Composable
fun CreateRoomBanner(
    onCreateRoom: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    Row(
        modifier = modifier
            .fillMaxWidth()
            .glassPanel(
                glowColor = colors.CreateRoomButtonRim,
                cornerRadius = dimens.panelRadius,
                surface = colors.CreateRoomBannerSurface,
            )
            .padding(horizontal = Spacing.Sm, vertical = Spacing.Xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        BasicText(
            text = Strings.homeCreateRoomBannerText,
            style = YapNPlayTheme.typography.micro.copy(color = colors.HomeTextSoft),
            maxLines = 2,
            modifier = Modifier.weight(1f),
        )
        PillButton(
            text = Strings.homeCreateRoom,
            onClick = onCreateRoom,
            gradient = listOf(colors.CreateRoomButtonStart, colors.CreateRoomButtonEnd),
            contentColor = colors.TextPrimary,
            rimColor = colors.CreateRoomButtonRim,
            height = dimens.ctaSmallHeight,
            leadingIcon = Icons.Rounded.GroupAdd,
            trailingIcon = null,
            iconSize = dimens.iconSmall,
            textStyle = YapNPlayTheme.typography.label,
        )
    }
}
