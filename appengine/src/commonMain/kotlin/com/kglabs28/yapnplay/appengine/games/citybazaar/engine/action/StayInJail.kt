package com.kglabs28.yapnplay.appengine.games.citybazaar.engine.action

import com.kglabs28.yapnplay.appengine.games.base.play.Action

/** Stay in jail after three failed rolls: rent collected is halved until the next turn. */
data object StayInJail : Action
