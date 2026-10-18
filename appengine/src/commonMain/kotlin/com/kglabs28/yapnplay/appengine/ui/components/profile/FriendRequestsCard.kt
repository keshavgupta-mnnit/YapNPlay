package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.IconCircle

/** "Friend requests": how many are waiting, and View requests (off while none). */
@Composable
fun FriendRequestsCard(
    count: Int,
    label: String,
    onView: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    Column(
        modifier = modifier
            .glassPanel(glowColor = if (count > 0) colors.LiveGreen else colors.IconMuted, surface = colors.RoomsPanel.copy(alpha = PANEL_ALPHA))
            .padding(horizontal = Spacing.Sm, vertical = Spacing.Xs),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xs, Alignment.CenterVertically),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
            IconCircle(imageVector = Icons.Rounded.GroupAdd, fill = colors.RoomsSmallButton, tint = colors.LiveGreen, iconSize = dimens.iconMedium)
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs)) {
                BasicText(text = Strings.profileRequestsTitle, style = typography.sectionTitle.copy(color = colors.TextPrimary))
                BasicText(text = label, style = typography.micro.copy(color = if (count > 0) colors.LiveGreen else colors.RoomsSubtitle))
            }
        }
        GlassPillButton(
            text = Strings.profileViewRequests,
            onClick = onView,
            enabled = count > 0,
            accent = colors.LiveGreen,
            height = dimens.codeFieldHeight,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private const val PANEL_ALPHA = 0.9f
