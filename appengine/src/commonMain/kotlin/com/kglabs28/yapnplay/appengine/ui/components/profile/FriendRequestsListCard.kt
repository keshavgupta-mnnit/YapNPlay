package com.kglabs28.yapnplay.appengine.ui.components.profile

import com.kglabs28.yapnplay.appengine.core.ui.ProfileStrings
import com.kglabs28.yapnplay.appengine.core.designsystem.AppBrushes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import com.kglabs28.yapnplay.appengine.ui.components.common.glassScrollbar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.screens.profile.ProfileRequestUi

/** "Friend Requests (n)": each person with Decline and a green Accept; tap a row for their card. */
@Composable
fun FriendRequestsListCard(
    requests: List<ProfileRequestUi>,
    onOpen: (requestId: String) -> Unit,
    onAccept: (requestId: String) -> Unit,
    onDecline: (requestId: String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listScroll = rememberScrollState()
    val dimens = YapNPlayTheme.dimens
    val rowShape = RoundedCornerShape(dimens.cbRowRadius)
    ModalCard(
        surfaceBrush = AppBrushes.dialogGeneric,title = ProfileStrings.profileRequestsDialogTitle(requests.size), onClose = onClose, modifier = modifier.width(dimens.profileWideDialogWidth)) {
        Column(
            modifier = Modifier.fillMaxWidth().heightIn(max = dimens.profileListMaxHeight).glassScrollbar(listScroll).verticalScroll(listScroll).padding(end = YapNPlayTheme.dimens.scrollbarWidth * 2),
            verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
        ) {
            requests.forEach { request ->
                val p = request.person
                PersonRow(
                    avatarId = p.avatarId,
                    name = p.name,
                    detail = ProfileStrings.profileYapIdLine(p.userCode),
                    modifier = Modifier.fillMaxWidth().clip(rowShape).background(YapNPlayTheme.colors.ListRow).clickable { onOpen(request.requestId) }.padding(Spacing.Xs),
                ) {
                    ProfileActionButton(text = ProfileStrings.profileDecline, kind = ProfileButtonKind.NEUTRAL, onClick = { onDecline(request.requestId) }, height = dimens.smallButtonHeight)
                    ProfileActionButton(text = ProfileStrings.profileAccept, kind = ProfileButtonKind.CONFIRM, onClick = { onAccept(request.requestId) }, height = dimens.smallButtonHeight)
                }
            }
        }
    }
}
