package com.kglabs28.yapnplay.appengine.games.citybaazar.utils

import com.kglabs28.yapnplay.appengine.games.base.rules.HouseRuleOption

// Identity and showcase (shared with the server's game list).
const val CITY_BAAZAR_ID = "city-baazar"
const val CITY_BAAZAR_DISPLAY_NAME = "City Baazar"
val CITY_BAAZAR_TAGLINES = listOf("Buy", "Build", "Boss")

const val CITY_BAAZAR_MIN_PLAYERS = 2
const val CITY_BAAZAR_MAX_PLAYERS = 6
/** A random (Quick Play) City Baazar table seats four. */
const val CITY_BAAZAR_QUICK_PLAY_SEATS = 4
const val CITY_BAAZAR_SEARCH_SECONDS = 30L
/** Seconds per decision before the turn plays itself. */
const val CITY_BAAZAR_TURN_SECONDS = 20L

// Money (₹)
const val STARTING_CASH = 25_000
const val START_BONUS = 1_500
const val JAIL_FINE = 500
const val CLUB_FEE = 500
/** Selling to the bank pays half the price (tiles) or build cost (buildings). */
const val RESALE_DIVISOR = 2
/** Transit rent by how many transit tiles the owner holds (1–4). */
val TRANSIT_RENTS = listOf(500, 1_000, 2_000, 4_000)
/** Utility rent: dice total times this (one utility owned / both). */
const val UTILITY_RENT_ONE = 100
const val UTILITY_RENT_BOTH = 250

// Board
const val TILE_COUNT = 36
const val TILES_PER_EDGE = 9
const val START_TILE = 0
const val JAIL_TILE = 9
const val CLUB_TILE = 18
const val REST_HOUSE_TILE = 27
const val MAX_HOUSES = 3
/** Building level of a hotel (after 3 houses). */
const val HOTEL_LEVEL = 4
const val DICE_FACES = 6
const val MAX_DOUBLES = 3
const val MAX_JAIL_TRIES = 3
const val ROUND_LIMIT = 30
const val BACK_STEPS = 3

// Bot: money it keeps after buying or building.
const val BOT_BUY_RESERVE = 2_000
const val BOT_SET_BUY_RESERVE = 500
const val BOT_BUILD_RESERVE = 3_000
const val BOT_JAIL_FINE_CASH = 6_000

// Lobby: house-rule toggles (keys match CityBaazarHouseRules fields) and
// the fixed rule sheet. Defaults are the standard rules.
val CITY_BAAZAR_HOUSE_RULES = listOf(
    HouseRuleOption("set_double_rent", "Set Bonus", "Bare land rent doubles on a complete colour group", true),
    HouseRuleOption("rest_house_skip", "Rest House Rest", "Landing on Rest House skips your next turn", true),
    HouseRuleOption("club_fee", "Club Fee", "Landing on Club costs ₹500", true),
    HouseRuleOption("round_limit", "30 Rounds", "End after 30 rounds and rank by net worth", true),
)

val CITY_BAAZAR_FIXED_RULES = listOf(
    "2–6 players, ₹25,000 each to start",
    "₹1,500 for passing or landing on START",
    "Build only on complete colour groups: 3 houses, then a hotel",
    "Doubles roll again; three doubles in a row go to jail",
    "Jail: pay ₹500, use a Jail Pass, or roll doubles (3 tries)",
    "Sell to the bank at half price to pay debts",
    "20 seconds per decision; the bot plays when time runs out",
)
