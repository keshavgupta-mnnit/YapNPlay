package com.kglabs28.yapnplay.appengine.games.royalludo.domain

/**
 * Whether a seat's owner is at the table. [AWAY]: their app is in the
 * background or offline, so the server plays their turns at once.
 * [BOT]: their seat was offered and nobody took it, so the server plays
 * it to the end (it is still theirs if they come back).
 */
enum class LudoPresence { PLAYING, AWAY, BOT }
