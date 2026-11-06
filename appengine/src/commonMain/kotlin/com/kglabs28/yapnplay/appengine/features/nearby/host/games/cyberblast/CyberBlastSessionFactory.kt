package com.kglabs28.yapnplay.appengine.features.nearby.host.games.cyberblast

import com.kglabs28.yapnplay.appengine.features.nearby.host.games.GameSession
import com.kglabs28.yapnplay.appengine.features.nearby.host.games.GameSessionFactory
import com.kglabs28.yapnplay.appengine.features.nearby.host.HostConstants
import com.kglabs28.yapnplay.appengine.features.nearby.host.settings.GameSettings
import com.kglabs28.yapnplay.appengine.features.nearby.host.settings.IntSetting
import com.kglabs28.yapnplay.appengine.features.nearby.host.settings.Settings
import com.kglabs28.yapnplay.appengine.games.base.play.GameRandom
import com.kglabs28.yapnplay.appengine.games.cyberblast.CyberBlastEngine
import com.kglabs28.yapnplay.appengine.games.cyberblast.CyberBlastMandatoryRules
import com.kglabs28.yapnplay.appengine.games.cyberblast.CyberBlastRules
import com.kglabs28.yapnplay.appengine.games.cyberblast.CyberBlastState
import com.kglabs28.yapnplay.appengine.games.cyberblast.bot.BlastBotSettings
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

/** Starts Cyber Blast sessions: every rule and the bots' weights from the admin settings (no room toggles). */
class CyberBlastSessionFactory(
    override val game: CyberBlastEngine,
    private val random: GameRandom,
    private val json: Json,
    private val settings: Settings = Settings.Defaults,
) : GameSessionFactory {

    override fun start(seatCount: Int, houseRules: Map<String, Boolean>?): GameSession =
        session(game.start(CyberBlastRules(mandatory()), seatCount, random) as CyberBlastState)

    override fun restore(saved: JsonObject): GameSession = session(json.decodeFromJsonElement(CyberBlastSaved.serializer(), saved).engine)

    private fun session(state: CyberBlastState) =
        CyberBlastSession(game, state, random, json, settings[GameSettings.BLAST_FRAME_MS].toLong(), ::botSettings)

    private fun mandatory() = CyberBlastMandatoryRules(
        lives = settings[GameSettings.BLAST_LIVES],
        startRadius = settings[GameSettings.BLAST_START_RADIUS],
        maxRadius = maxOf(settings[GameSettings.BLAST_MAX_RADIUS], settings[GameSettings.BLAST_START_RADIUS]),
        radiusPerHit = settings[GameSettings.BLAST_RADIUS_PER_HIT],
        bombs = settings[GameSettings.BLAST_BOMBS],
        fuseMs = ms(GameSettings.BLAST_FUSE_MS),
        flameMs = ms(GameSettings.BLAST_FLAME_MS),
        speed = settings[GameSettings.BLAST_SPEED],
        boostSpeed = settings[GameSettings.BLAST_BOOST_SPEED],
        boostMs = seconds(GameSettings.BLAST_BOOST_SECONDS),
        shieldMs = ms(GameSettings.BLAST_SHIELD_MS),
        hitPoints = settings[GameSettings.BLAST_HIT_POINTS],
        hitPenalty = settings[GameSettings.BLAST_HIT_PENALTY],
        selfPenalty = settings[GameSettings.BLAST_SELF_PENALTY],
        cratePoints = settings[GameSettings.BLAST_CRATE_POINTS],
        crateDensityPct = settings[GameSettings.BLAST_CRATE_DENSITY_PCT],
        itemEveryMs = seconds(GameSettings.BLAST_ITEM_EVERY_SECONDS),
        itemJitterMs = seconds(GameSettings.BLAST_ITEM_JITTER_SECONDS),
        itemLifeMs = seconds(GameSettings.BLAST_ITEM_LIFE_SECONDS),
        maxItems = settings[GameSettings.BLAST_MAX_ITEMS],
        countdownMs = seconds(GameSettings.BLAST_COUNTDOWN_SECONDS),
        closeAfterMs = seconds(GameSettings.BLAST_CLOSE_AFTER_SECONDS),
        closeStepMs = ms(GameSettings.BLAST_CLOSE_STEP_MS),
    )

    /** Read on every bot look, so a change on the admin page applies at once. */
    private fun botSettings() = BlastBotSettings(
        reactMs = ms(GameSettings.BLAST_BOT_REACT_MS),
        reactJitterMs = ms(GameSettings.BLAST_BOT_REACT_JITTER_MS),
        bombGapMs = ms(GameSettings.BLAST_BOT_BOMB_GAP_MS),
        bombGapJitterMs = ms(GameSettings.BLAST_BOT_BOMB_GAP_JITTER_MS),
        lifeValue = settings[GameSettings.BLAST_BOT_LIFE_VALUE].toDouble(),
        stepCost = settings[GameSettings.BLAST_BOT_STEP_COST].toDouble(),
        hitChancePct = settings[GameSettings.BLAST_BOT_HIT_CHANCE_PCT],
        cornerBonusPct = settings[GameSettings.BLAST_BOT_CORNER_BONUS_PCT],
        boostValue = settings[GameSettings.BLAST_BOT_BOOST_VALUE].toDouble(),
        huntValue = settings[GameSettings.BLAST_BOT_HUNT_VALUE].toDouble(),
        threatPct = settings[GameSettings.BLAST_BOT_THREAT_PCT],
        plannedBombPct = settings[GameSettings.BLAST_BOT_PLANNED_BOMB_PCT],
        standingMinPct = settings[GameSettings.BOT_STANDING_MIN_PCT],
        standingMaxPct = settings[GameSettings.BOT_STANDING_MAX_PCT],
    )

    private fun ms(setting: IntSetting): Long = settings[setting].toLong()

    private fun seconds(setting: IntSetting): Long = settings[setting] * HostConstants.MILLIS_PER_SECOND
}
