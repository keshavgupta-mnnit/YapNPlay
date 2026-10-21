package com.kglabs28.yapnplay.appengine.features.profile.usecase

/** What a game screen needs for long-press add / remove friend on a player card. */
class PlayerFriendUseCases(
    val friendIds: GetFriendIdsUseCase,
    val toggle: TogglePlayerFriendUseCase,
)
