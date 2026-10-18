package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.screens.profile.ProfileRequestUi

/** Everyone asking to be friends, each on a person card with Decline and a green Accept. */
@Composable
fun FriendRequestsListCard(
    requests: List<ProfileRequestUi>,
    onAccept: (requestId: String) -> Unit,
    onDecline: (requestId: String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    ModalCard(title = Strings.profileRequestsDialogTitle, onClose = onClose, modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
        ) {
            requests.forEach { request ->
                PersonCard(person = request.person) {
                    TintedGlassButton(text = Strings.profileDecline, tint = colors.IconMuted, onClick = { onDecline(request.requestId) }, modifier = Modifier.weight(1f))
                    TintedGlassButton(text = Strings.profileAccept, tint = colors.LiveGreen, onClick = { onAccept(request.requestId) }, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
