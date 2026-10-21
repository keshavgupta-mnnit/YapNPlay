package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.mapper

import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.core.ui.LudoStrings
import com.kglabs28.yapnplay.appengine.core.ui.CityBazaarStrings
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbNoticeArt
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.MoneyReason
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarEvent
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarExtensionVoteInfo
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardEntryUi
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbTransactionUi
import com.kglabs28.yapnplay.appengine.ui.components.profile.AvatarPresets
import com.kglabs28.yapnplay.appengine.ui.components.table.AvatarRing
import com.kglabs28.yapnplay.appengine.ui.components.table.TableAvatarUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarAuctionInfo
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarColour
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarMatch
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarPlayer
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarPresence
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarTurnPhase
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CardDeck
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CardEffect
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CityBazaarBoard
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CityBazaarCards
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CityGroup
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.HOTEL_LEVEL
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.JAIL_FINE
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.RESALE_DIVISOR
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.RupeeFormat
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.SET_FOR_DOUBLE
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.TileKind
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbAssetUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbDeckRowUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbModal
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbOwnedRowUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbPanel
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbPlayerCardUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbPropertyCardUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbRateRowUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbResultRowUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbRowKind
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbSheetRowUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbStackUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbTileUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbToastUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbTokenUi
import kotlin.time.Duration

/** The end of the match: leaderboard and result. */
object CbResultMapper {
    /** The end-of-match leaderboard: by net worth, a title and EXP for each place. */
    fun leaderboard(match: CityBazaarMatch, duration: String): LeaderboardUi {
        val entries = match.ranking.mapIndexedNotNull { index, seat ->
            match.player(seat)?.let { p ->
                LeaderboardEntryUi(
                    place = index + 1,
                    placeLabel = LudoStrings.ludoPlace(index + 1),
                    name = p.name,
                    avatarId = p.avatarId,
                    title = CityBazaarStrings.cbPlaceTitles[index.coerceAtMost(CityBazaarStrings.cbPlaceTitles.lastIndex)],
                    score = TableStrings.leaderboardPts(AppConstants.PLACE_EXP[index.coerceAtMost(AppConstants.PLACE_EXP.lastIndex)]),
                    exp = TableStrings.leaderboardExp(AppConstants.PLACE_EXP[index.coerceAtMost(AppConstants.PLACE_EXP.lastIndex)]),
                    micOn = p.micOn,
                    tint = Color.Unspecified,
                    isMe = p.isMe,
                    seat = seat,
                )
            }
        }
        val mine = entries.firstOrNull { it.isMe }?.placeLabel.orEmpty()
        return LeaderboardUi(CityBazaarStrings.cbTitle, duration, entries, mine, myExp = entries.firstOrNull { it.isMe }?.let { e -> "+" + AppConstants.PLACE_EXP[(e.place - 1).coerceAtMost(AppConstants.PLACE_EXP.lastIndex)] }.orEmpty())
    }

    fun result(match: CityBazaarMatch): CbModal.Result = CbModal.Result(
        match.ranking.mapIndexedNotNull { index, seat ->
            match.player(seat)?.let { CbResultRowUi(LudoStrings.ludoPlace(index + 1), it.name, it.colour, CbFormat.rupees(match.netWorth[seat] ?: 0)) }
        },
    )
}
