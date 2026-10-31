package com.kglabs28.yapnplay.appengine.games.mono.domain

import com.kglabs28.yapnplay.appengine.games.mono.identity.MonoConstants

/**
 * Classic rule: a Wild +4 goes down only when [hand] holds no card of
 * [colour] (the colour in play), unless +4 Anytime ([anytime]) is on.
 * Stacking a +4 onto a penalty is never held back.
 */
fun MonoCard.heldBackByColour(hand: List<MonoCard>, colour: MonoConstants.Color?, anytime: Boolean): Boolean =
    this is MonoCard.PrismDrawFour && !anytime && colour != null && hand.any { it.playColor() == colour }
