package com.kglabs28.yapnplay.appengine.ui.components.playinvite

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.NearbyStrings
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton
import com.kglabs28.yapnplay.appengine.ui.components.profile.PresetAvatar
import com.kglabs28.yapnplay.appengine.ui.screens.playinvite.PlayInviteUi

/** "Play now?": who invited, where, and Not now / JOIN ([enabled] off while answering). */
@Composable
fun PlayInviteCard(
    invite: PlayInviteUi,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = YapNPlayTheme.colors
    ModalCard(
        title = invite.title,
        subtitle = invite.body,
        modifier = modifier,
        actions = {
            GlassPillButton(text = NearbyStrings.inviteDecline, onClick = onDecline, enabled = enabled)
            PillButton(
                text = NearbyStrings.inviteAccept,
                onClick = onAccept,
                gradient = listOf(colors.PlayFriendsCta),
                contentColor = colors.TextPrimary,
                rimColor = colors.PlayFriendsCtaRim,
                glassFill = colors.PlayFriendsCta,
                enabled = enabled,
            )
        },
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            PresetAvatar(avatarId = invite.avatarId, size = YapNPlayTheme.dimens.friendAvatar * 2, rimColor = colors.PlayFriendsAccent)
        }
    }
}
