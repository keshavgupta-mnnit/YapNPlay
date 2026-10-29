package com.kglabs28.yapnplay.appengine.ui.components.seat

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings

/**
 * "Keep playing?" once the winners are in, at any table, in the same
 * dialog as Leave the match and End the game: end or keep playing, the
 * votes so far and the time left. One who can't vote ([canVote] false)
 * just sees it wait.
 */
@Composable
fun KeepPlayingVoteDialog(
    secondsLeft: Long,
    yes: Int,
    no: Int,
    voters: Int,
    canVote: Boolean,
    modifier: Modifier = Modifier,
    onVote: (keepPlaying: Boolean) -> Unit,
) {
    SeatPickerDialog(
        title = TableStrings.keepPlayingTitle,
        message = TableStrings.keepPlayingBody,
        options = emptyList(),
        selectedIndex = null,
        onSelect = {},
        secondaryLabel = TableStrings.keepPlayingEnd,
        onSecondary = { onVote(false) },
        primaryLabel = TableStrings.keepPlayingKeep,
        onPrimary = { onVote(true) },
        modifier = modifier,
        countdown = TableStrings.seatCountdown(secondsLeft),
        footer = listOfNotNull(TableStrings.keepPlayingCount(yes, no, voters), TableStrings.keepPlayingWaiting.takeIf { !canVote }),
        showActions = canVote,
    )
}
