package com.kglabs28.yapnplay.appengine.games.base.play

/**
 * A player action. Marker interface only — each game mechanic defines its
 * own actions under games/elements (dice, pieces, cards, ...), so games
 * without dice never see dice actions.
 */
interface Action
