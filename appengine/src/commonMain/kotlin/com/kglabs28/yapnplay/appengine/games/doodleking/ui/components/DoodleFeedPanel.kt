package com.kglabs28.yapnplay.appengine.games.doodleking.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.DoodleStrings
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match.DoodleFeedRowUi
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match.DoodleFeedTone
import com.kglabs28.yapnplay.appengine.ui.components.common.glassScrollbar

/**
 * The turn's guesses (empty again every turn), the newest at the bottom
 * and kept in view: wrong guesses, "very close" (only to the one who typed
 * it), who guessed it, chat among those who did, and the table's notes.
 * Each name in its seat's colour.
 */
@Composable
fun DoodleFeedPanel(rows: List<DoodleFeedRowUi>, seatTint: (Int) -> Color, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val style = YapNPlayTheme.typography.caption
    val listState = rememberLazyListState()
    LaunchedEffect(rows.size) { if (rows.isNotEmpty()) listState.animateScrollToItem(rows.lastIndex) }
    DoodleSidePanel(title = DoodleStrings.dkGuesses, modifier = modifier) {
        if (rows.isEmpty()) {
            BasicText(text = DoodleStrings.dkNoGuesses, style = style.copy(color = colors.TextSecondary))
        }
        LazyColumn(state = listState, modifier = Modifier.fillMaxWidth().glassScrollbar(listState)) {
            items(rows, key = { it.key }) { row ->
                val tone = when (row.tone) {
                    DoodleFeedTone.CORRECT -> colors.ReadyGreen
                    DoodleFeedTone.CLOSE -> colors.Warning
                    DoodleFeedTone.CHAT -> colors.Cyan
                    DoodleFeedTone.NOTE -> colors.TextSecondary
                    DoodleFeedTone.GUESS -> colors.TextPrimary
                }
                BasicText(
                    text = buildAnnotatedString {
                        if (row.name.isNotEmpty()) {
                            withStyle(SpanStyle(color = seatTint(row.seat), fontWeight = FontWeight.Bold)) { append(row.name) }
                            append(NAME_GAP)
                        }
                        withStyle(SpanStyle(color = tone)) { append(row.text) }
                    },
                    style = style,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

private const val NAME_GAP = ": "
