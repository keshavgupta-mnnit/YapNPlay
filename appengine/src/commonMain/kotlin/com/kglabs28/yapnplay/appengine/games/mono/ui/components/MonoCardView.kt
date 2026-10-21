package com.kglabs28.yapnplay.appengine.games.mono.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayColors
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoCard
import com.kglabs28.yapnplay.appengine.games.mono.identity.MonoConstants
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoCardFace
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoCardIcon
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoCardLook
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoHue
import com.kglabs28.yapnplay.appengine.ui.components.table.fill
import com.kglabs28.yapnplay.appengine.ui.components.table.rim

/**
 * A Standard MONO card. Sized by [modifier]; the face is 2:3 portrait.
 * [isPlayable] false pushes the card back, so the player can see at a
 * glance which cards the table will accept. [faceDown] draws the back.
 */
@Composable
fun MonoCardView(
    card: MonoCard,
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
        onClick = onClick,
    )
}

/** The card, resolved to what the face draws. */
fun MonoCard.look(): MonoCardLook = when (this) {
    is MonoCard.NumberCard -> coloured(label = number.toString(), hue = color.hue())
    is MonoCard.ReverseCard -> coloured(icon = MonoCardIcon.REVERSE, hue = color.hue())
    is MonoCard.SkipCard -> coloured(icon = MonoCardIcon.SKIP, hue = color.hue())
    is MonoCard.DrawTwoCard -> coloured(label = "+2", hue = color.hue())
    MonoCard.PrismColorWild -> prism(icon = MonoCardIcon.WILD)
    MonoCard.PrismDrawFour -> prism(label = "+4")
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
fun MonoConstants.Color.hue(): MonoHue = when (this) {
    MonoConstants.Color.RED -> MonoHue.RED
    MonoConstants.Color.GREEN -> MonoHue.GREEN
    MonoConstants.Color.BLUE -> MonoHue.BLUE
    MonoConstants.Color.GOLD -> MonoHue.GOLD
}
