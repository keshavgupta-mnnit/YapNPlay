package com.kglabs28.yapnplay.appengine.games.citybazaar.utils

/**
 * Luck and Shagun Chest, as in the original game: the dice total of the
 * roll that brought the player there (2–12) picks the result. The original
 * tables are printed on its board and not available, so these use the
 * game's own effects (docs/city-bazaar-rules.md §7).
 */
object CityBazaarCards {
    val luck: List<CardSpec> = listOf(
        CardSpec("luck-2", CardDeck.LUCK, 2, "Lottery win!", CardEffect.Money(2_500)),
        CardSpec("luck-3", CardDeck.LUCK, 3, "Go to Jail", CardEffect.GoToJail),
        CardSpec("luck-4", CardDeck.LUCK, 4, "Traffic fine", CardEffect.Money(-1_000)),
        CardSpec("luck-5", CardDeck.LUCK, 5, "Advance to START", CardEffect.AdvanceTo(START_TILE)),
        CardSpec("luck-6", CardDeck.LUCK, 6, "Scholarship", CardEffect.Money(1_000)),
        CardSpec("luck-7", CardDeck.LUCK, 7, "Go back 3 tiles", CardEffect.MoveBack(BACK_STEPS)),
        CardSpec("luck-8", CardDeck.LUCK, 8, "Crossword prize", CardEffect.Money(1_000)),
        CardSpec("luck-9", CardDeck.LUCK, 9, "Advance to the Club", CardEffect.AdvanceTo(CLUB_TILE)),
        CardSpec("luck-10", CardDeck.LUCK, 10, "Advance to Mumbai", CardEffect.AdvanceTo(MUMBAI_TILE)),
        CardSpec("luck-11", CardDeck.LUCK, 11, "Your birthday", CardEffect.CollectFromEach(500)),
        CardSpec("luck-12", CardDeck.LUCK, 12, "Treat everyone", CardEffect.PayEach(250)),
    )
    val chest: List<CardSpec> = listOf(
        CardSpec("chest-2", CardDeck.CHEST, 2, "Wedding shagun", CardEffect.Money(1_500)),
        CardSpec("chest-3", CardDeck.CHEST, 3, "Medical bill", CardEffect.Money(-1_000)),
        CardSpec("chest-4", CardDeck.CHEST, 4, "Income tax refund", CardEffect.Money(2_000)),
        CardSpec("chest-5", CardDeck.CHEST, 5, "Go to Jail", CardEffect.GoToJail),
        CardSpec("chest-6", CardDeck.CHEST, 6, "Diwali bonus", CardEffect.Money(1_000)),
        CardSpec("chest-7", CardDeck.CHEST, 7, "Insurance premium", CardEffect.Money(-1_500)),
        CardSpec("chest-8", CardDeck.CHEST, 8, "Bank interest", CardEffect.Money(500)),
        CardSpec("chest-9", CardDeck.CHEST, 9, "Advance to START", CardEffect.AdvanceTo(START_TILE)),
        CardSpec("chest-10", CardDeck.CHEST, 10, "School fees", CardEffect.Money(-750)),
        CardSpec("chest-11", CardDeck.CHEST, 11, "Festival gifts", CardEffect.CollectFromEach(250)),
        CardSpec("chest-12", CardDeck.CHEST, 12, "Property tax", CardEffect.PerProperty(PROPERTY_TAX_PER)),
    )

    fun deck(deck: CardDeck): List<CardSpec> = if (deck == CardDeck.LUCK) luck else chest

    /** The result for a dice [total] (2–12). */
    fun forRoll(deck: CardDeck, total: Int): CardSpec = deck(deck).first { it.roll == total }

    fun find(id: String): CardSpec? = (luck + chest).firstOrNull { it.id == id }
}
