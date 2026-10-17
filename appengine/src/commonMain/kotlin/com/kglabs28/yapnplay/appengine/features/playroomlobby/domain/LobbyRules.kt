package com.kglabs28.yapnplay.appengine.features.playroomlobby.domain

import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.features.gamecatalog.domain.GameCatalogItem

/** What a non-leader's main button shows. */
enum class PlayerButtonState {
    /** No game selected yet. */
    DISABLED,
    NOT_READY,
    READY,
    /** Every seat is taken: opt in/out of spectating instead. */
    NOT_WATCHING,
    WATCHING,
}

/** Options in a member's tap menu, depending on who you are. */
enum class MemberMenuOption { MAKE_LEADER, MAKE_ADMIN, ADD_FRIEND, REMOVE_FRIEND }

/**
 * The lobby's display rules, as pure functions so they are testable and
 * stay out of the UI. The server enforces the same rules for real.
 */
object LobbyRules {

    /**
     * You first, then online members (leader, then admin, then the rest),
     * then everyone offline.
     */
    fun sortMembers(members: List<LobbyMember>, myUserId: String): List<LobbyMember> =
        members.sortedWith(
            compareBy<LobbyMember>(
                { it.userId != myUserId },
                { !it.online },
                { !it.isLeader },
                { !it.isAdmin },
            ),
        )

    /** Seated-to-be players: online members who pressed ready (the leader always counts). */
    fun readyCount(room: LobbyRoom): Int =
        room.members.count { it.online && (it.ready || it.isLeader) }

    fun seatsFull(room: LobbyRoom, game: GameCatalogItem?): Boolean =
        game != null && readyCount(room) >= game.maxPlayers

    fun playerButtonState(room: LobbyRoom, me: LobbyMember, game: GameCatalogItem?): PlayerButtonState = when {
        game == null -> PlayerButtonState.DISABLED
        me.ready -> PlayerButtonState.READY
        seatsFull(room, game) && me.watching -> PlayerButtonState.WATCHING
        seatsFull(room, game) -> PlayerButtonState.NOT_WATCHING
        else -> PlayerButtonState.NOT_READY
    }

    /**
     * Leader may start when a game is picked, at least
     * [AppConstants.LOBBY_MIN_HUMANS_TO_START] humans are ready, and either
     * the game's minimum is met or bot fill will make up the rest.
     */
    fun canStart(room: LobbyRoom, game: GameCatalogItem?): Boolean {
        if (game == null) return false
        val ready = readyCount(room)
        return ready >= AppConstants.LOBBY_MIN_HUMANS_TO_START &&
            (ready >= game.minPlayers || room.botFill)
    }

    /** Menu for tapping [target]: role hand-offs you hold, plus friend add/remove. */
    fun menuOptions(me: LobbyMember, target: LobbyMember): List<MemberMenuOption> {
        if (me.userId == target.userId) return emptyList()
        return buildList {
            if (me.isLeader) add(MemberMenuOption.MAKE_LEADER)
            if (me.isAdmin) add(MemberMenuOption.MAKE_ADMIN)
            add(if (target.isFriend) MemberMenuOption.REMOVE_FRIEND else MemberMenuOption.ADD_FRIEND)
        }
    }
}
