package com.kglabs28.yapnplay.appengine.features.profile.usecase

import com.kglabs28.yapnplay.appengine.core.auth.PlayGamesPlayer

/** The player's Play Games name, to suggest as their YapNPlay name (null: none). */
class GetPlayGamesNameUseCase(private val player: PlayGamesPlayer) {
    operator fun invoke(): String? = player.displayName.value
}
