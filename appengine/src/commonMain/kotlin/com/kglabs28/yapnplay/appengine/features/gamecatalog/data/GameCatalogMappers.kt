package com.kglabs28.yapnplay.appengine.features.gamecatalog.data

import com.kglabs28.yapnplay.appengine.features.gamecatalog.domain.GameCatalogItem
import com.kglabs28.yapnplay.appengine.games.base.rules.GameDescriptor

/** A registered game's descriptor to its picker entry. */
fun GameDescriptor.toCatalogItem(): GameCatalogItem = GameCatalogItem(
    id = id.value,
    name = displayName,
    taglines = showcase.taglines,
    artKey = showcase.artKey,
    minPlayers = minPlayers,
    maxPlayers = maxPlayers,
    quickPlaySeats = matchmaking.seats,
    houseRuleOptions = houseRuleOptions,
    fixedRules = fixedRules,
)
