package com.kglabs28.yapnplay.appengine.games.doodleking.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.DoodleStrings
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match.DoodlePickUi
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton

/** The drawer's 3 words, one tap to pick; when time runs out one is picked for them. */
@Composable
fun DoodlePickCard(pick: DoodlePickUi, onPick: (Int) -> Unit, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    ModalCard(title = DoodleStrings.dkPickTitle, subtitle = pick.seconds, modifier = modifier.width(dimens.doodlePickWidth)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
            pick.words.forEachIndexed { index, word ->
                PillButton(
                    text = word.uppercase(),
                    onClick = { onPick(index) },
                    gradient = listOf(colors.CtaPurpleStart, colors.CtaPurpleEnd),
                    contentColor = colors.TextPrimary,
                    height = dimens.doodleWordButtonHeight,
                    trailingIcon = null,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
