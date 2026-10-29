package com.kglabs28.yapnplay.appengine.ui.components.moderation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.ui.components.seat.SeatPickerDialog

/**
 * "END THE GAME?" at any table, in the same dialog as Leave the match:
 * keep playing or end, the votes so far and the time left. Only players
 * still in can vote.
 */
@Composable
fun EndVoteCard(vote: EndVoteUi, onVote: (agree: Boolean) -> Unit, modifier: Modifier = Modifier) {
    SeatPickerDialog(
        title = TableStrings.endVoteTitle,
        message = vote.body,
        options = emptyList(),
        selectedIndex = null,
        onSelect = {},
        secondaryLabel = TableStrings.endVoteKeep,
        onSecondary = { onVote(false) },
        primaryLabel = TableStrings.endVoteEnd,
        onPrimary = { onVote(true) },
        modifier = modifier,
        countdown = vote.countdown,
        footer = listOfNotNull(vote.tally, vote.status),
        showActions = vote.canVote,
    )
}
