package com.kglabs28.yapnplay.appengine.games.monochaos.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayColors
import com.kglabs28.yapnplay.appengine.core.ui.MonoStrings
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosCard
import com.kglabs28.yapnplay.appengine.games.monochaos.identity.MonoChaosConstants
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoCardFace
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoCardIcon
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoCardLook
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoHue
import com.kglabs28.yapnplay.appengine.ui.components.table.fill
import com.kglabs28.yapnplay.appengine.ui.components.table.rim

/**
 * A MONO: Complete Chaos card. Same face and same components as Standard
 * MONO — only the cards differ. Its back carries the COMPLETE CHAOS
 * sub-plate, which is how a player tells the two decks apart.
 */
@Composable
fun MonoChaosCardView(
    card: MonoChaosCard,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isPlayable: Boolean = true,
    faceDown: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    MonoCardFace(
        look = card.look(),
        modifier = modifier,
        isSelected = isSelected,
        isPlayable = isPlayable,
        faceDown = faceDown,
        chaos = true,
        onClick = onClick,
    )
}

/** The card, resolved to what the face draws. */
fun MonoChaosCard.look(): MonoCardLook = when (this) {
    is MonoChaosCard.NumberCard -> coloured(label = number.toString(), hue = color.hue())
    is MonoChaosCard.ReverseCard -> coloured(icon = MonoCardIcon.REVERSE, hue = color.hue())
    is MonoChaosCard.SkipCard -> coloured(icon = MonoCardIcon.SKIP, hue = color.hue())
    is MonoChaosCard.DrawTwoCard -> coloured(label = "+2", hue = color.hue())
    is MonoChaosCard.ColoredDrawFourCard -> coloured(label = "+4", hue = color.hue())
    is MonoChaosCard.DiscardAllCard -> coloured(label = MonoStrings.monoDiscardAll, icon = MonoCardIcon.DISCARD_ALL, hue = color.hue())
    is MonoChaosCard.SkipEveryoneCard -> coloured(icon = MonoCardIcon.SKIP_ALL, hue = color.hue())
    MonoChaosCard.PrismColorWild -> prism(icon = MonoCardIcon.WILD)
    MonoChaosCard.PrismDrawFour -> prism(label = "+4")
    MonoChaosCard.PrismReverseDrawFour -> prism(label = "+4", icon = MonoCardIcon.REVERSE)
    MonoChaosCard.PrismDrawSix -> prism(label = "+6")
    MonoChaosCard.PrismDrawTen -> prism(label = "+10")
    MonoChaosCard.PrismColorRoulette -> prism(icon = MonoCardIcon.ROULETTE)
}

/** A card of one colour. */
private fun coloured(label: String = "", hue: MonoHue, icon: MonoCardIcon? = null) = MonoCardLook(
    label = label,
    fill = hue.fill,
    rim = hue.rim,
    icon = icon,
)

/** A multi-colour card: four quadrants behind a silver bevel. */
private fun prism(label: String = "", icon: MonoCardIcon? = null) = MonoCardLook(
    label = label,
    fill = MonoHue.RED.fill,
    rim = YapNPlayColors.MonoSilverMid,
    prism = true,
    icon = icon,
)

/** The jewel tone of a card colour. */
fun MonoChaosConstants.Color.hue(): MonoHue = when (this) {
    MonoChaosConstants.Color.RED -> MonoHue.RED
    MonoChaosConstants.Color.GREEN -> MonoHue.GREEN
    MonoChaosConstants.Color.BLUE -> MonoHue.BLUE
    MonoChaosConstants.Color.GOLD -> MonoHue.GOLD
}
