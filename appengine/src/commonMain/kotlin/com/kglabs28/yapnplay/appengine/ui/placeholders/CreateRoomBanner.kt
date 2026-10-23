package com.kglabs28.yapnplay.appengine.ui.placeholders

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton

/**
 * The panel at the bottom of "PLAY WITH FRIENDS": the pitch line over the
 * purple "Create Room" button, on frosted white glass.
 */
@Composable
fun CreateRoomBanner(
    onCreateRoom: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    Column(
        modifier = modifier
            .fillMaxWidth()
            .glassPanel(
                glowColor = colors.CreateRoomFrostRim,
                cornerRadius = dimens.panelRadius,
                surface = colors.CreateRoomFrost,
            )
            .padding(horizontal = Spacing.Sm, vertical = Spacing.Xs),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        BasicText(
            text = Strings.homeCreateRoomBannerText,
            style = YapNPlayTheme.typography.homeBannerText.copy(color = colors.TextPrimary),
            maxLines = 2,
        )
        PillButton(
            text = Strings.homeCreateRoom,
            onClick = onCreateRoom,
            gradient = listOf(colors.CreateRoomButtonStart, colors.CreateRoomButtonEnd),
            contentColor = colors.TextPrimary,
            rimColor = colors.CreateRoomButtonRim,
            height = dimens.homeBannerButtonHeight,
            solid = true,
            leadingIcon = Icons.Rounded.GroupAdd,
            trailingIcon = null,
            iconSize = dimens.homeBarIcon,
            textStyle = YapNPlayTheme.typography.homeBannerButton,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
