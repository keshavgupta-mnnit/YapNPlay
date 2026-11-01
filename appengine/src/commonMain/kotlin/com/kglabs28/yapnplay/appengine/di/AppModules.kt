package com.kglabs28.yapnplay.appengine.di

/**
 * The whole dependency graph, in order. The Koin graph test verifies
 * every definition resolves; add each new feature module here.
 */
val appModules = listOf(coreModule, appManagerModule, profileModule, playRoomsModule, gamesModule, playRoomLobbyModule, notificationsModule, voiceModule, moderationModule, coinsModule, matchResultModule, royalLudoModule, cityBazaarModule, mainModule, monoModule, monoChaosModule, matchHistoryModule, feedbackModule, emojiGiftModule)
