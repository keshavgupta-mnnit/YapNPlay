package com.kglabs28.yapnplay.appengine.ui.screens.rooms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.HeadlineFontFamily
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.home.PlayWithFriendsCard

/**
 * Rooms tab: hub header plus the play-with-friends card, full width.
 */
@Composable
fun RoomsScreen(
    onHostRoom: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RoomsViewModel? = null,
) {
    val state = viewModel?.state?.collectAsState()?.value ?: RoomsState()
    LaunchedEffect(viewModel) {
        viewModel?.onIntent(RoomsIntent.Refresh)
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
    ) {
        BasicText(
            text = state.title,
            style = YapNPlayTheme.typography.headline.copy(
                fontFamily = HeadlineFontFamily,
                fontWeight = FontWeight.Bold,
                color = YapNPlayTheme.colors.TextPrimary,
            ),
        )
        BasicText(
            text = state.subtitle,
            style = YapNPlayTheme.typography.body.copy(
                color = YapNPlayTheme.colors.TextSecondary,
            ),
        )
        PlayWithFriendsCard(
            onPlayWithFriends = onHostRoom,
            modifier = Modifier.weight(1f),
        )
    }
}
