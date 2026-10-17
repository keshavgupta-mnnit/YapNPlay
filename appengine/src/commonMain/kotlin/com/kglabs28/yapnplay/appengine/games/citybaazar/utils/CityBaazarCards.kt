package com.kglabs28.yapnplay.appengine.games.citybaazar.utils

/** Every Luck and Shagun Chest card (each draw is equally likely; cards stay in the pile). */
object CityBaazarCards {
    val luck: List<CardSpec> = listOf(
        CardSpec("luck-lottery", CardDeck.LUCK, "Lottery win!", CardEffect.Money(2_000)),
        CardSpec("luck-traffic", CardDeck.LUCK, "Traffic fine", CardEffect.Money(-1_000)),
        CardSpec("luck-start", CardDeck.LUCK, "Advance to START", CardEffect.AdvanceTo(START_TILE)),
        CardSpec("luck-jail", CardDeck.LUCK, "Go to Jail", CardEffect.GoToJail),
        CardSpec("luck-pass", CardDeck.LUCK, "Jail Pass: leave jail free once", CardEffect.JailPass),
        CardSpec("luck-back", CardDeck.LUCK, "Go back 3 tiles", CardEffect.MoveBack(BACK_STEPS)),
        CardSpec("luck-mumbai", CardDeck.LUCK, "Advance to Mumbai", CardEffect.AdvanceTo(35)),
        CardSpec("luck-club", CardDeck.LUCK, "Advance to the Club", CardEffect.AdvanceTo(CLUB_TILE)),
        CardSpec("luck-birthday", CardDeck.LUCK, "Your birthday: ₹500 from each player", CardEffect.CollectFromEach(500)),
        CardSpec("luck-repairs", CardDeck.LUCK, "Repairs: ₹400 per house, ₹1,000 per hotel", CardEffect.Repairs(400, 1_000)),
        CardSpec("luck-crossword", CardDeck.LUCK, "Crossword prize", CardEffect.Money(1_000)),
        CardSpec("luck-treat", CardDeck.LUCK, "Treat everyone: ₹250 to each player", CardEffect.PayEach(250)),
    )
    val chest: List<CardSpec> = listOf(
        CardSpec("chest-wedding", CardDeck.CHEST, "Wedding shagun", CardEffect.Money(1_500)),
        CardSpec("chest-refund", CardDeck.CHEST, "Income tax refund", CardEffect.Money(2_000)),
        CardSpec("chest-medical", CardDeck.CHEST, "Medical bill", CardEffect.Money(-1_000)),
        CardSpec("chest-insurance", CardDeck.CHEST, "Insurance premium", CardEffect.Money(-1_500)),
        CardSpec("chest-diwali", CardDeck.CHEST, "Diwali bonus", CardEffect.Money(1_000)),
        CardSpec("chest-school", CardDeck.CHEST, "School fees", CardEffect.Money(-750)),
        CardSpec("chest-interest", CardDeck.CHEST, "Bank interest", CardEffect.Money(500)),
        CardSpec("chest-start", CardDeck.CHEST, "Advance to START", CardEffect.AdvanceTo(START_TILE)),
        CardSpec("chest-jail", CardDeck.CHEST, "Go to Jail", CardEffect.GoToJail),
        CardSpec("chest-pass", CardDeck.CHEST, "Jail Pass: leave jail free once", CardEffect.JailPass),
        CardSpec("chest-gifts", CardDeck.CHEST, "Festival gifts: ₹250 from each player", CardEffect.CollectFromEach(250)),
        CardSpec("chest-property", CardDeck.CHEST, "Property tax: ₹250 per house, ₹750 per hotel", CardEffect.Repairs(250, 750)),
    )

    fun deck(deck: CardDeck): List<CardSpec> = if (deck == CardDeck.LUCK) luck else chest

    fun find(id: String): CardSpec? = (luck + chest).firstOrNull { it.id == id }
}
