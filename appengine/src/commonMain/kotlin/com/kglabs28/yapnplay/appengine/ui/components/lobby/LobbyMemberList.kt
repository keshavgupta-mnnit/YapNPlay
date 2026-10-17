package com.kglabs28.yapnplay.appengine.ui.components.lobby

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.screens.playroomlobby.LobbyMemberUi

/**
 * Scrolling member list on a glass panel, with the online count at the
 * bottom. Members arrive already sorted (you, leader, admin, online,
 * offline). Tapping a member opens their options.
 */
@Composable
fun LobbyMemberList(
    members: List<LobbyMemberUi>,
    countLabel: String,
    onMemberClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors

    Column(
        modifier = modifier
            .glassPanel(glowColor = colors.IconMuted, surface = colors.RoomsPanel.copy(alpha = 0.9f))
            .padding(Spacing.Xs),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
        ) {
            items(members, key = { it.userId }) { member ->
                LobbyMemberRow(member = member, onClick = { onMemberClick(member.userId) })
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppIcon(imageVector = Icons.Rounded.Groups, tint = colors.IconMuted, size = YapNPlayTheme.dimens.iconSmall)
            BasicText(text = countLabel, style = YapNPlayTheme.typography.micro.copy(color = colors.TextSecondary))
        }
    }
}
