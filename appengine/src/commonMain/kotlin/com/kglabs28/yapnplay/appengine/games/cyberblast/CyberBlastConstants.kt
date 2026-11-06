package com.kglabs28.yapnplay.appengine.games.cyberblast

const val CYBER_BLAST_ID = "cyber-blast"
const val CYBER_BLAST_DISPLAY_NAME = "Cyber Blast"
val CYBER_BLAST_TAGLINES = listOf("Drop", "Blast", "Survive")

/** 2 to 8 on one board; random tables seat 4 (CPUs fill the empty seats). */
const val CYBER_BLAST_MIN_PLAYERS = 2
const val CYBER_BLAST_MAX_PLAYERS = 8
const val CYBER_BLAST_QUICK_PLAY_SEATS = 4
const val CYBER_BLAST_SEARCH_SECONDS = 20L

val CYBER_BLAST_FIXED_RULES = listOf(
    "Rooms 2–8 players, random tables 4",
    "Drop bombs to break blockers and blast the others; metal pillars never break",
    "Every hit costs a heart (5 each); you come back somewhere safe",
    "Hit someone: +100 and your blast reaches 1 tile further",
    "Hit by someone: −25 · your own bomb: −15 · each blocker: +10",
    "Grab the speed boots for 30 s of extra speed",
    "Last player with hearts left wins; later the arena closes in",
)

/** The board: 15 × 11 tiles, metal pillars on every odd column of every odd row. */
const val BLAST_COLS = 15
const val BLAST_ROWS = 11

/** Positions are in thousandths of a tile; a tile's centre is at +500. */
const val BLAST_TILE = 1_000
const val BLAST_HALF_TILE = 500

/** The simulation never steps further than this at once (a smooth, fair step for everyone). */
const val BLAST_MAX_STEP_MS = 50L

/** A tile counts as "safe enough" to respawn on when this many steps of free tiles lead somewhere out of a bomb's reach. */
const val BLAST_ESCAPE_DEPTH = 5

/** Fewer free tiles than this around a respawn tile: too boxed in. */
const val BLAST_MIN_ROOM = 3

/** The arena stops closing when this many tiles of the middle are left on each side of the centre (a 5 × 3 core). */
const val BLAST_CORE_HALF_COLS = 2
const val BLAST_CORE_HALF_ROWS = 1


/** Fewer players with hearts left than this: the match is over. */
const val BLAST_MIN_ALIVE = 2
