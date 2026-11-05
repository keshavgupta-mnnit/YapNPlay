package com.kglabs28.yapnplay.appengine.ui.components.lobby

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import com.kglabs28.yapnplay.appengine.core.ui.NearbyStrings
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import com.kglabs28.yapnplay.appengine.ui.components.profile.PresetAvatar
import com.kglabs28.yapnplay.appengine.ui.screens.playroomlobby.InviteRowUi
import com.kglabs28.yapnplay.appengine.ui.screens.playroomlobby.InviteTone

/** One person to invite to the party: avatar, name, what they are doing, and Add + at the end. */
@Composable
fun InvitePlayerRow(
    row: InviteRowUi,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val toneColor: Color = when (row.tone) {
        InviteTone.FREE -> colors.LiveGreen
        InviteTone.BUSY -> colors.Gold
        InviteTone.AWAY -> colors.TextSecondary
        InviteTone.DONE -> colors.PlayFriendsAccent
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(dimens.lobbyMemberRow)
            .glassPanel(glowColor = colors.IconMuted, shape = RoundedCornerShape(dimens.rowRadius), surface = colors.RoomsPanel)
            .padding(horizontal = Spacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
    ) {
        PresetAvatar(avatarId = row.avatarId, size = dimens.friendAvatar, rimColor = toneColor)
        Column(modifier = Modifier.weight(1f)) {
            ShrinkText(text = row.name, style = typography.label.copy(color = colors.TextPrimary))
            ShrinkText(text = row.status, style = typography.caption.copy(color = toneColor))
        }
        GlassPillButton(
            text = NearbyStrings.addButton,
            onClick = onAdd,
            leadingIcon = Icons.Rounded.Add,
            accent = colors.PlayFriendsCta,
            enabled = row.canAdd,
        )
    }
}
