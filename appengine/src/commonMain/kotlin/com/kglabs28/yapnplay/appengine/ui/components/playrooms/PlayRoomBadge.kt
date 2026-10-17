package com.kglabs28.yapnplay.appengine.ui.components.playrooms

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.FamilyRestroom
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoomIcon

/** A room's badge glyph and fill; the rooms list and the lobby share it. */
data class PlayRoomBadge(val icon: ImageVector, val fill: Color)

@Composable
fun PlayRoomIcon.badge(): PlayRoomBadge {
    val colors = YapNPlayTheme.colors
    return when (this) {
        PlayRoomIcon.GROUP -> PlayRoomBadge(Icons.Rounded.Groups, colors.RoomIconPurple)
        PlayRoomIcon.DICE -> PlayRoomBadge(Icons.Rounded.Casino, colors.RoomIconBlue)
        PlayRoomIcon.FAMILY -> PlayRoomBadge(Icons.Rounded.FamilyRestroom, colors.RoomIconSlate)
        PlayRoomIcon.GAMEPAD -> PlayRoomBadge(Icons.Rounded.SportsEsports, colors.RoomIconViolet)
    }
}
