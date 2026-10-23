package com.kglabs28.yapnplay.appengine.ui.components.table

import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * A choice the table is waiting on (a colour, the Roulette colour, a swap
 * target, the keep-playing vote), as a small glass card over the piles.
 * It never dims or covers the hand or the seats: the player can still see
 * their cards and everyone else while they choose.
 */
@Composable
fun MonoTablePrompt(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    // The app's dialog card, as every other choice in the app.
    ModalCard(title = title, modifier = modifier.widthIn(max = YapNPlayTheme.dimens.modalWidth), content = content)
}
