package com.kglabs28.yapnplay.appengine.ui.screens.mono

import com.kglabs28.yapnplay.appengine.core.ui.MonoStrings
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoEvent
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoMatch
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosEvent
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosMatch
import com.kglabs28.yapnplay.appengine.ui.components.table.TableFeedLineUi
import com.kglabs28.yapnplay.appengine.ui.components.table.TableFeedTone

/**
 * The snackbar for a MONO! penalty that concerns the user, so they see
 * what happened to them: caught without calling, the call window missed,
 * a wrong catch (all a loss), or catching someone (a gain). Null when the
 * update has none of these for the user; the last one wins.
 */
object MonoPenaltySnack {
    fun mono(events: List<MonoEvent>, m: MonoMatch, id: Long): TableFeedLineUi? {
        val me = m.me?.seat ?: return null
        fun name(seat: Int) = m.player(seat)?.name.orEmpty()
        return events.mapNotNull { e ->
            when (e) {
                is MonoEvent.MonoCaught -> caught(me, e.catcher.value, e.seat.value, e.penaltyCards, id, ::name)
                is MonoEvent.FalseCatch -> wrongCatch(me, e.seat.value, e.penaltyCards, id)
                is MonoEvent.MonoMissed -> missed(me, e.seat.value, e.penaltyCards, id)
                else -> null
            }
        }.lastOrNull()
    }

    fun chaos(events: List<MonoChaosEvent>, m: MonoChaosMatch, id: Long): TableFeedLineUi? {
        val me = m.me?.seat ?: return null
        fun name(seat: Int) = m.player(seat)?.name.orEmpty()
        return events.mapNotNull { e ->
            when (e) {
                is MonoChaosEvent.MonoCaught -> caught(me, e.catcher.value, e.seat.value, e.penaltyCards, id, ::name)
                is MonoChaosEvent.FalseCatch -> wrongCatch(me, e.seat.value, e.penaltyCards, id)
                is MonoChaosEvent.MonoMissed -> missed(me, e.seat.value, e.penaltyCards, id)
                else -> null
            }
        }.lastOrNull()
    }

    private fun caught(me: Int, catcher: Int, caught: Int, count: Int, id: Long, name: (Int) -> String): TableFeedLineUi? = when (me) {
        caught -> TableFeedLineUi(id, MonoStrings.monoNoteYouWereCaught(name(catcher), count), TableFeedTone.LOSS)
        catcher -> TableFeedLineUi(id, MonoStrings.monoNoteYouCaught(name(caught), count), TableFeedTone.GAIN)
        else -> null
    }

    private fun wrongCatch(me: Int, catcher: Int, count: Int, id: Long): TableFeedLineUi? =
        TableFeedLineUi(id, MonoStrings.monoNoteYouWrongCatch(count), TableFeedTone.LOSS).takeIf { catcher == me && count > 0 }

    private fun missed(me: Int, seat: Int, count: Int, id: Long): TableFeedLineUi? =
        TableFeedLineUi(id, MonoStrings.monoNoteYouMissed(count), TableFeedTone.LOSS).takeIf { seat == me }
}
