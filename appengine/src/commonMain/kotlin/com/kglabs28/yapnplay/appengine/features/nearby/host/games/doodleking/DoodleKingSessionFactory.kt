package com.kglabs28.yapnplay.appengine.features.nearby.host.games.doodleking

import com.kglabs28.yapnplay.appengine.features.nearby.host.games.GameSession
import com.kglabs28.yapnplay.appengine.features.nearby.host.games.GameSessionFactory
import com.kglabs28.yapnplay.appengine.features.nearby.host.HostConstants
import com.kglabs28.yapnplay.appengine.features.nearby.host.HostClock
import com.kglabs28.yapnplay.appengine.features.nearby.host.settings.GameSettings
import com.kglabs28.yapnplay.appengine.features.nearby.host.settings.IntSetting
import com.kglabs28.yapnplay.appengine.features.nearby.host.settings.Settings
import com.kglabs28.yapnplay.appengine.games.base.play.GameRandom
import com.kglabs28.yapnplay.appengine.games.doodleking.DoodleKingEngine
import com.kglabs28.yapnplay.appengine.games.doodleking.DoodleKingHouseRules
import com.kglabs28.yapnplay.appengine.games.doodleking.DoodleKingMandatoryRules
import com.kglabs28.yapnplay.appengine.games.doodleking.DoodleKingRules
import com.kglabs28.yapnplay.appengine.games.doodleking.DoodleKingState
import com.kglabs28.yapnplay.appengine.games.doodleking.HINGLISH_WORDS_KEY
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

/** Starts Doodle King sessions: timing and points from the admin settings, Hinglish words by the room's toggle. */
class DoodleKingSessionFactory(
    override val game: DoodleKingEngine,
    private val random: GameRandom,
    private val json: Json,
    private val clock: HostClock,
    private val settings: Settings = Settings.Defaults,
) : GameSessionFactory {

    override fun start(seatCount: Int, houseRules: Map<String, Boolean>?): GameSession {
        // Quick Play (no toggles) is English only; a room's toggle is on unless turned off.
        val hinglish = houseRules?.let { it[HINGLISH_WORDS_KEY] ?: true } ?: false
        val rules = DoodleKingRules(mandatory(), DoodleKingHouseRules(hinglish))
        val state = game.start(rules, seatCount, random) as DoodleKingState
        return session(state)
    }

    override fun restore(saved: JsonObject): GameSession = session(json.decodeFromJsonElement(DoodleKingSaved.serializer(), saved).engine)

    // The engine reads the test-bots setting; the session follows it.
    private fun session(state: DoodleKingState) = DoodleKingSession(game, state, random, json, clock, testBots = { game.cpuFillAllowed }, extensionRounds = { settings[GameSettings.DK_EXTENSION_ROUNDS] })

    private fun mandatory() = DoodleKingMandatoryRules(
        rounds = settings[GameSettings.DK_ROUNDS],
        introMs = seconds(GameSettings.DK_INTRO_SECONDS),
        pickMs = seconds(GameSettings.DK_PICK_SECONDS),
        drawMs = seconds(GameSettings.DK_DRAW_SECONDS),
        revealMs = seconds(GameSettings.DK_REVEAL_SECONDS),
        guessPoints = settings[GameSettings.DK_GUESS_POINTS],
        levelPct = settings[GameSettings.DK_LEVEL_POINTS_PCT],
        drawerPerGuess = settings[GameSettings.DK_DRAWER_PER_GUESS],
        allGuessedBonus = settings[GameSettings.DK_ALL_GUESSED_BONUS],
        passPenalty = settings[GameSettings.DK_PASS_PENALTY],
        hintStartPct = settings[GameSettings.DK_HINT_START_PCT],
    )

    private fun seconds(setting: IntSetting): Long =
        settings[setting] * HostConstants.MILLIS_PER_SECOND
}
