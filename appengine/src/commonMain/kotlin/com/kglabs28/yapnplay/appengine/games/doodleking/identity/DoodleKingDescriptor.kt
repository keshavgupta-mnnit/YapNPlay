package com.kglabs28.yapnplay.appengine.games.doodleking.identity

import com.kglabs28.yapnplay.appengine.games.base.identity.GameId
import com.kglabs28.yapnplay.appengine.games.base.rules.GameDescriptor
import com.kglabs28.yapnplay.appengine.games.base.rules.GameRules
import com.kglabs28.yapnplay.appengine.games.base.rules.GameShowcase
import com.kglabs28.yapnplay.appengine.games.base.rules.HouseRules
import com.kglabs28.yapnplay.appengine.games.base.rules.MandatoryRules
import com.kglabs28.yapnplay.appengine.games.base.rules.MatchmakingConfig
import com.kglabs28.yapnplay.appengine.games.base.rules.PlayMode
import com.kglabs28.yapnplay.appengine.games.base.rules.TimingConfig
import com.kglabs28.yapnplay.appengine.games.base.rules.TurnPolicy

/**
 * Doodle King in the game pickers. It has no engine in the app: it needs
 * real people and the server runs every turn.
 */
class DoodleKingDescriptor : GameDescriptor {
    override val id = GameId(DoodleKingConstants.ID)
    override val displayName = DoodleKingConstants.DISPLAY_NAME
    override val timing = TimingConfig(turnSeconds = DoodleKingConstants.DRAW_SECONDS)
    override val matchmaking = MatchmakingConfig(
        seats = DoodleKingConstants.QUICK_PLAY_SEATS,
        searchSeconds = DoodleKingConstants.SEARCH_SECONDS,
        fillWithCpu = false,
    )
    override val showcase = GameShowcase(taglines = DoodleKingConstants.TAGLINES, artKey = DoodleKingConstants.ID)
    override val houseRuleOptions = DoodleKingConstants.HOUSE_RULES
    override val fixedRules = DoodleKingConstants.FIXED_RULES
    override val minPlayers = DoodleKingConstants.PLAYERS_MIN
    override val maxPlayers = DoodleKingConstants.PLAYERS_MAX
    override val maxSpectators: Int? = null
    override val turnPolicy: TurnPolicy = TurnPolicy.Simultaneous

    override fun defaultRules(): GameRules = DoodleKingRules

    override fun rulesSummary(): String = DoodleKingConstants.FIXED_RULES.joinToString(". ")

    /** The rules are all the server's; nothing to choose here but the room's toggles. */
    private object DoodleKingRules : GameRules {
        override val mandatory: MandatoryRules = object : MandatoryRules {}
        override val house: HouseRules = object : HouseRules {}
        override fun isAllowed(mode: PlayMode): Boolean = true
    }
}
