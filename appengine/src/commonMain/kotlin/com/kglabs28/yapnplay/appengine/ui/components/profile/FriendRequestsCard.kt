package com.kglabs28.yapnplay.appengine.ui.components.profile

import com.kglabs28.yapnplay.appengine.core.ui.ProfileStrings
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton

/** "Friend Requests" with the waiting count, and View Requests at the end (off while none). */
@Composable
fun FriendRequestsCard(count: Int, label: String, onView: () -> Unit, modifier: Modifier = Modifier) {
    ProfileActionCard(
        icon = Icons.Rounded.GroupAdd,
        iconTint = YapNPlayTheme.colors.SocialZoneAccent,
        title = ProfileStrings.profileRequestsTitle,
        description = label,
        badge = count,
        modifier = modifier,
    ) {
        Spacer(Modifier.weight(1f))
        GlassPillButton(text = ProfileStrings.profileViewRequests, onClick = onView, enabled = count > 0, accent = YapNPlayTheme.colors.SocialZoneAccent)
    }
}
