package com.kglabs28.yapnplay.appengine.core.audio

/**
 * The sound files, shared by every platform (commonMain/composeResources/files/sounds).
 * Each platform's GameAudio reads them with Res.readBytes and plays them its own way.
 */
object GameSoundFiles {
    const val MUSIC = "files/sounds/music_loop.wav"

    fun path(sound: GameSound): String = "files/sounds/" + when (sound) {
        GameSound.DICE_ROLL -> "dice_roll.wav"
        GameSound.HOP -> "piece_hop.wav"
        GameSound.CAPTURE -> "piece_capture.wav"
        GameSound.WIN -> "winner.wav"
        GameSound.YOUR_TURN -> "your_turn.wav"
        GameSound.TICK -> "clock_tick.wav"
        GameSound.HURRY_TICK -> "hurry_tick.wav"
        GameSound.CARD_PLAY -> "card_play.wav"
        GameSound.CARD_DRAW -> "card_draw.wav"
        GameSound.CARD_SHUFFLE -> "card_shuffle.wav"
        GameSound.MONO_CALL -> "mono_call.mp3"
        GameSound.CAUGHT -> "caught_you.mp3"
        GameSound.EMOJI_TOSS -> "emoji_toss.wav"
        GameSound.EMOJI_POP -> "emoji_pop.wav"
    }
}
