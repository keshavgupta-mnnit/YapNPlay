package com.kglabs28.yapnplay.appengine.games.doodleking.domain

/** A turn's part: the drawer's intro, picking a word, drawing, the word shown. LIMIT: the rounds are over and the table votes to keep playing. */
enum class DoodlePhase { INTRO, PICK, DRAW, REVEAL, LIMIT, FINISHED }
