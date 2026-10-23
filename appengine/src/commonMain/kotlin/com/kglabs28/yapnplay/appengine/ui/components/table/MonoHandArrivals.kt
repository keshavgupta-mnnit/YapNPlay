package com.kglabs28.yapnplay.appengine.ui.components.table

/**
 * Which sorted hand slots are still waiting for a card in the air: each
 * flight into the hand is matched (by face) to the first slot in [order]
 * holding that face that no other flight has taken. Returns slot index →
 * flight id, so the card can stay hidden until it lands and its flight can
 * aim at that very slot.
 */
object MonoHandArrivals {
    fun <C> slots(order: List<Int>, hand: List<C>, look: (C) -> MonoCardLook, arriving: List<MonoFlight>): Map<Int, Long> {
        val waiting = arriving.toMutableList()
        val slots = mutableMapOf<Int, Long>()
        order.forEach { index ->
            val flight = waiting.firstOrNull { it.look == look(hand[index]) } ?: return@forEach
            waiting.remove(flight)
            slots[index] = flight.id
        }
        return slots
    }
}
