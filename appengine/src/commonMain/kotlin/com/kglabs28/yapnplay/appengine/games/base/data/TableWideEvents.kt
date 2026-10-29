package com.kglabs28.yapnplay.appengine.games.base.data

import com.kglabs28.yapnplay.appengine.games.base.match.EndVoteResult
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

/**
 * Events the server sends at every table, whatever the game (the end-vote
 * result). A game reads them here, not as one of its own events.
 */
fun JsonElement.isTableWideEvent(): Boolean = type() in TABLE_WIDE_TYPES

/** The end vote's result in this update, if one finished. */
fun List<JsonElement>.endVoteResult(): EndVoteResult? = lastOrNull { it.type() == END_VOTED }?.let { event ->
    val fields = event as JsonObject
    EndVoteResult(
        ended = fields[ENDED]?.jsonPrimitive?.booleanOrNull == true,
        yes = fields[YES]?.jsonPrimitive?.intOrNull ?: 0,
        voters = fields[VOTERS]?.jsonPrimitive?.intOrNull ?: 0,
    )
}

private fun JsonElement.type(): String? = (this as? JsonObject)?.get(TYPE_KEY)?.jsonPrimitive?.contentOrNull

private const val TYPE_KEY = "type"
private const val END_VOTED = "EndVoted"
private const val ENDED = "ended"
private const val YES = "yes"
private const val VOTERS = "voters"
private val TABLE_WIDE_TYPES = setOf(END_VOTED)
