package com.kglabs28.yapnplay.appengine.games.mono.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoCard
import com.kglabs28.yapnplay.appengine.games.mono.identity.MonoConstants
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoCardFace
import com.kglabs28.yapnplay.appengine.ui.components.table.monoCardColour

/**
 * A Standard MONO card. Sized by [modifier]; the face is 2:3 portrait.
 * [shownColour] overrides the colour the card is drawn in — the table
 * uses it to show the colour in force rather than the card's own.
 * [isPlayable] false pushes the card back, so the player can see at a
 * glance which cards the table will accept.
 */
@Composable
fun MonoCardView(
    card: MonoCard,
    modifier: Modifier = Modifier,
    shownColour: MonoConstants.Color? = null,
    isSelected: Boolean = false,
    isPlayable: Boolean = true,
    faceDown: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    MonoCardFace(
        glyph = card.glyph(),
        cardColor = monoCardColour(shownColour ?: card.playColor()),
        modifier = modifier,
        isSelected = isSelected,
        isPlayable = isPlayable,
        faceDown = faceDown,
        onClick = onClick,
    )
}