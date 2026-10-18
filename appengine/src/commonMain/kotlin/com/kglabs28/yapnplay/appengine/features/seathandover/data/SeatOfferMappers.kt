package com.kglabs28.yapnplay.appengine.features.seathandover.data

import com.kglabs28.yapnplay.appengine.features.seathandover.domain.SeatOffer
import com.kglabs28.yapnplay.appengine.features.seathandover.domain.SeatOfferStage
import com.kglabs28.yapnplay.appengine.protocol.SeatOfferInfo
import com.kglabs28.yapnplay.appengine.protocol.SeatOfferStageName
import kotlinx.datetime.Instant

/** Wire seat offer → domain; [toLocal] moves a server time onto this device's clock. */
fun SeatOfferInfo.toDomain(toLocal: (Long) -> Instant): SeatOffer {
    val who = userId.orEmpty()
    val whoName = name.orEmpty()
    val domainStage = when (stage) {
        SeatOfferStageName.CONFIRMING_ABSENCE -> SeatOfferStage.ConfirmingAbsence(who, whoName)
        SeatOfferStageName.TO_SPECTATOR -> SeatOfferStage.ToSpectator(who, whoName, round ?: FIRST_ROUND)
        SeatOfferStageName.LEADER_CHOOSING -> SeatOfferStage.LeaderChoosing(who, whoName)
        SeatOfferStageName.OPEN_TO_ALL -> SeatOfferStage.OpenToAll
    }
    return SeatOffer(seat, leaverName, domainStage, toLocal(endsAt))
}

private const val FIRST_ROUND = 1
