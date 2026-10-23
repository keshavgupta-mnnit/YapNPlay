package com.kglabs28.yapnplay.appengine.ui.placeholders

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import com.kglabs28.yapnplay.appengine.core.designsystem.scaledDp
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton

/**
 * The panel at the bottom of "PLAY WITH FRIENDS": the pitch line in dark
 * text on frosted white glass, the glassy dark red "Create Room" button at
 * its end, level with the line's second row.
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
                glowColor = colors.CreateRoomFrostRim,
                cornerRadius = dimens.panelRadius,
                surface = colors.CreateRoomFrost,
            )
            .padding(horizontal = Spacing.Sm, vertical = Spacing.Xs),
        // The button sits level with the text's second line, at the far end.
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        BasicText(
            text = Strings.homeCreateRoomBannerText,
            style = YapNPlayTheme.typography.homeBannerText.copy(color = colors.CreateRoomPanelText, fontWeight = FontWeight.SemiBold),
            maxLines = 2,
            modifier = Modifier.weight(1f),
        )
        PillButton(
            text = Strings.homeCreateRoom,
            onClick = onCreateRoom,
            gradient = listOf(colors.CtaGlassRed),
            contentColor = colors.TextPrimary,
            rimColor = colors.CtaGlassRedRim,
            glassFill = colors.CtaGlassRed,
            height = dimens.homeBannerButtonHeight,
            leadingIcon = Icons.Rounded.GroupAdd,
            trailingIcon = null,
            iconSize = dimens.homeBarIcon,
            textStyle = YapNPlayTheme.typography.homeBannerButton,
        )
    }
}
