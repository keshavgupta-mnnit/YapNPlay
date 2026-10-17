package com.kglabs28.yapnplay.appengine.games.elements.dice.mechanics

import com.kglabs28.yapnplay.appengine.games.base.play.Action

/**
 * Roll the dice. Shared by dice games (Royal Ludo, Monopoly-style games).
 * Card and drawing games never see this action.
 */
data object RollDice : Action
