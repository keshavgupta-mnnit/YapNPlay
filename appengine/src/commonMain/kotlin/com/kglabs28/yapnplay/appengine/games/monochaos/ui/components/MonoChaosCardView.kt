package com.kglabs28.yapnplay.appengine.games.monochaos.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosCard
import com.kglabs28.yapnplay.appengine.games.monochaos.identity.MonoChaosConstants
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoCardFace
import com.kglabs28.yapnplay.appengine.ui.components.table.monoChaosCardColour

/**
 * A MONO: Complete Chaos card. Same face as Standard MONO — the two
 * games share one card component, so a card never looks different
 * between them. Sized by [modifier]; the face is 2:3 portrait.
 * [isPlayable] false pushes the card back.
 */
@Composable
fun MonoChaosCardView(
    card: MonoChaosCard,
    modifier: Modifier = Modifier,
    shownColour: MonoChaosConstants.Color? = null,
    isSelected: Boolean = false,
    isPlayable: Boolean = true,
    faceDown: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    MonoCardFace(
        glyph = card.glyph(),
        cardColor = monoChaosCardColour(shownColour ?: card.playColor()),
        modifier = modifier,
        isSelected = isSelected,
        isPlayable = isPlayable,
        faceDown = faceDown,
        onClick = onClick,
    )
}