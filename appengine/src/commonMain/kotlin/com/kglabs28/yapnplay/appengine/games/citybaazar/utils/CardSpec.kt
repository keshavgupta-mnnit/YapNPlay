package com.kglabs28.yapnplay.appengine.games.citybaazar.utils

/** One card: its id (sent to the app), pile, text and effect. */
data class CardSpec(val id: String, val deck: CardDeck, val text: String, val effect: CardEffect)
