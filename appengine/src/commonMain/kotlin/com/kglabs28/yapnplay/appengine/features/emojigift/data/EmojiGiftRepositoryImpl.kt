package com.kglabs28.yapnplay.appengine.features.emojigift.data

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.core.network.RealtimeRejectedException
import com.kglabs28.yapnplay.appengine.features.emojigift.data.remote.EmojiGiftClient
import com.kglabs28.yapnplay.appengine.features.emojigift.domain.EmojiGiftFlight
import com.kglabs28.yapnplay.appengine.features.emojigift.domain.EmojiGiftMenu
import com.kglabs28.yapnplay.appengine.features.emojigift.domain.EmojiGiftOffer
import com.kglabs28.yapnplay.appengine.features.emojigift.domain.EmojiGiftReceipt
import com.kglabs28.yapnplay.appengine.features.emojigift.domain.EmojiGiftRepository
import com.kglabs28.yapnplay.appengine.protocol.ErrorCode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/** Maps the emoji-gift wire messages to domain. Never throws: failures come back typed. */
class EmojiGiftRepositoryImpl(
    private val client: EmojiGiftClient,
    private val dispatchers: DispatcherProvider,
) : EmojiGiftRepository {

    override suspend fun menu(): AppResult<EmojiGiftMenu> = withContext(dispatchers.io) {
        try {
            val body = client.prices()
            AppResult.Success(EmojiGiftMenu(body.coins, body.gifts.map { EmojiGiftOffer(it.id, it.coins) }))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            CrashReporter.record(e, "EmojiGiftRepository.menu")
            AppResult.Failure(YapNPlayError.Network("emoji prices failed"))
        }
    }

    override suspend fun send(matchId: String, emojiId: String, toSeat: Int, price: Int): AppResult<EmojiGiftReceipt> = withContext(dispatchers.io) {
        try {
            val body = client.send(matchId, emojiId, toSeat)
            AppResult.Success(EmojiGiftReceipt(body.coins, body.spent))
        } catch (e: CancellationException) {
            throw e
        } catch (e: RealtimeRejectedException) {
            // Short of coins is expected (the picker says so); anything else is worth a look.
            if (e.code == ErrorCode.NOT_ENOUGH_COINS) {
                AppResult.Failure(YapNPlayError.LimitReached(price))
            } else {
                CrashReporter.record(e, "EmojiGiftRepository.send")
                AppResult.Failure(YapNPlayError.Network("emoji gift refused"))
            }
        } catch (e: Exception) {
            CrashReporter.record(e, "EmojiGiftRepository.send")
            AppResult.Failure(YapNPlayError.Network("emoji gift failed"))
        }
    }

    override fun flights(matchId: String): Flow<EmojiGiftFlight> =
        client.gifts(matchId).map { EmojiGiftFlight(it.fromSeat, it.toSeat, it.emojiId) }
}
