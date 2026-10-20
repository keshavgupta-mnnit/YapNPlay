package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Block
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton

/** "Blocked Users" with how many, and See List at the end. */
@Composable
fun BlockedUsersCard(count: Int, label: String, onSeeList: () -> Unit, modifier: Modifier = Modifier) {
    ProfileActionCard(
        icon = Icons.Rounded.Block,
        iconTint = YapNPlayTheme.colors.Error,
        title = Strings.profileBlockedTitle,
        description = label,
        badge = count,
        modifier = modifier,
    ) {
        Spacer(Modifier.weight(1f))
        GlassPillButton(text = Strings.profileSeeList, onClick = onSeeList, enabled = count > 0, accent = YapNPlayTheme.colors.SocialZoneAccent)
    }
}
