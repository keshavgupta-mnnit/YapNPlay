package com.kglabs28.yapnplay.appengine.ui.components.seat

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings

/** The keep-playing vote at a game's limit, in the same dialog as every table's seat and vote prompts. */
@Composable
fun KeepPlayingDialog(vote: KeepPlayingUi, onVote: (keepPlaying: Boolean) -> Unit, modifier: Modifier = Modifier) {
    SeatPickerDialog(
        title = TableStrings.extendTitle,
        message = vote.body,
        options = emptyList(),
        selectedIndex = null,
        onSelect = {},
        secondaryLabel = TableStrings.extendEnd,
        onSecondary = { onVote(false) },
        primaryLabel = TableStrings.extendContinue,
        onPrimary = { onVote(true) },
        modifier = modifier,
        countdown = vote.countdown,
        footer = listOfNotNull(vote.tally, vote.status),
        showActions = vote.canVote,
    )
}
