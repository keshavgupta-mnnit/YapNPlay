package com.kglabs28.yapnplay.appengine.ui.components.playrooms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import com.kglabs28.yapnplay.appengine.core.designsystem.revealWhen
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoom
import com.kglabs28.yapnplay.appengine.ui.components.common.EmptyStateText
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.SectionHeader

/**
 * "Your Rooms" panel: header with SEE ALL, then a scrolling list of
 * rooms. Fades in once [loaded]; an empty list shows the empty text.
 */
@Composable
fun YourRoomsPanel(
    rooms: List<PlayRoom>,
    loaded: Boolean,
    onOpenRoom: (String) -> Unit,
    onSeeAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    Column(
        modifier = modifier
            .glassPanel(
                glowColor = colors.IconMuted,
                surface = colors.RoomsPanel.copy(alpha = 0.9f),
            )
            .padding(Spacing.Sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        SectionHeader(
            icon = Icons.Rounded.Forum,
            title = Strings.roomsYourRoomsTitle,
            subtitle = Strings.roomsYourRoomsSubtitle,
            modifier = Modifier.fillMaxWidth(),
        ) {
            PillButton(
                text = Strings.roomsSeeAll,
                onClick = onSeeAll,
                gradient = listOf(colors.RoomsSmallButton, colors.RoomsSmallButton),
                contentColor = colors.TextPrimary,
                height = dimens.smallButtonHeight,
                textStyle = YapNPlayTheme.typography.sectionTitle,
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .revealWhen(loaded),
        ) {
            if (loaded && rooms.isEmpty()) {
                EmptyStateText(
                    text = Strings.roomsNoRooms,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
                ) {
                    items(rooms, key = { it.id }) { room ->
                        PlayRoomListItem(
                            room = room,
                            onClick = { onOpenRoom(room.id) },
                        )
                    }
                }
            }
        }
    }
}
