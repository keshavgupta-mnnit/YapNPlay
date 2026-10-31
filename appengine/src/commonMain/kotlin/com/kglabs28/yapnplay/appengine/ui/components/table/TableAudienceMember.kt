package com.kglabs28.yapnplay.appengine.ui.components.table

import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.LudoStrings

/**
 * One person in the top bar strip: their avatar (ring purple and glowing
 * while talking, red with the mic off) with their name under it. A long
 * press drops their speaker button below the avatar: it mutes them for
 * the user only (red while muted); a tap anywhere else closes it.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TableAudienceMember(
    person: TableAvatarUi,
    modifier: Modifier = Modifier,
    /** Mutes or unmutes them for the user; null: no long press. */
    onToggleHear: (() -> Unit)? = null,
) {
    val dimens = YapNPlayTheme.dimens
    var menuOpen by remember { mutableStateOf(false) }
    Column(
        modifier = modifier.then(if (onToggleHear != null) Modifier.combinedClickable(onClick = {}, onLongClick = { menuOpen = true }) else Modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box {
            TableAvatar(
                ring = person.ring.tint(),
                size = dimens.ludoAudienceAvatar,
                ringWidth = dimens.ludoAudienceRing,
                glow = (person.ring == AvatarRing.TALKING || person.ring == AvatarRing.TALKING_MUTED) && !person.mutedForMe,
                avatarId = person.avatarId,
            )
            if (menuOpen && onToggleHear != null) {
                Popup(
                    alignment = Alignment.TopCenter,
                    offset = IntOffset(0, 0),
                    onDismissRequest = { menuOpen = false },
                    properties = PopupProperties(focusable = true),
                ) {
                    Box(modifier = Modifier.padding(top = dimens.ludoAudienceAvatar + Spacing.Xs).arenaGlass(YapNPlayTheme.shapes.pill).padding(Spacing.Xs)) {
                        TableMuteButton(
                            muted = person.mutedForMe,
                            description = LudoStrings.ludoMuteFor(person.name),
                            onToggle = {
                                onToggleHear()
                                menuOpen = false
                            },
                        )
                    }
                }
            }
        }
        ShrinkText(
            text = person.name,
            style = YapNPlayTheme.typography.micro.copy(color = YapNPlayTheme.colors.TextSecondary),
            modifier = Modifier.widthIn(max = dimens.ludoAudienceNameWidth),
        )
    }
}
