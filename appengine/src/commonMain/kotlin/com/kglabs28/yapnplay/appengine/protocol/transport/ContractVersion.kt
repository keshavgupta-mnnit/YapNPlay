package com.kglabs28.yapnplay.appengine.protocol.transport

/**
 * The contract version. Any change bumps this, is written in
 * docs/backend-api-contract.md, and the golden files change with it.
 *
 * 0.4.0 — matches on the server: MatchSnapshot gains gameId, title,
 * roomId, startedAt, spectators, seatOffer, events; SeatInfo gains
 * displayName, level, micOn, presence; TurnInfo.deadline nullable; Colour
 * PURPLE/ORANGE; GameAction, SetAutoplay, SetMic; Royal Ludo and City
 * Bazaar public state / events / actions; MODERATION channel; join
 * approval (ApproveJoin, DeclineJoin, RoomInfo.waitingList, JoinApproved);
 * error codes BLOCKED, SEAT_TAKEN, VOICE_UNAVAILABLE; JoinRoomResult reply type.
 * 0.3.0 — Quick Play matchmaking, lobby, CPU seats.
 * 0.6.0 — MONO and Complete Chaos: the Caught action; MonoCaught,
 * FalseCatch and DeckReshuffled events (MonoMissed no longer sent); public
 * state gains catchSeat, catchClosedFor, caughtPenaltyCards,
 * falseCatchPenaltyCards, callBeforePlay, noPowerFinish. Room house rules
 * no_power_finish, call_before_play, strict_catch; stacking_allowed now
 * stacks upward.
 * 0.7.0 — MONO catch penalties fixed in every mode (MONO 5/2, Chaos 7/4);
 * strict_catch removed. AdRewardStatus and CoinsReward gain adRewards
 * (the day's ladder); coinsPerAd / granted are the next / paid step.
 * 0.8.0 — MONO plays on past the first finisher: continueVote in public
 * state, VoteContinue action, ContinueVoted event (vote from the 2nd
 * finisher, the 3rd in rooms of 5+); ranking adds unfinished seats by
 * fewest cards, then knockouts.
 * 0.9.0 — room channel RenameRoom (admin); InviteToRoomBody gains
 * userCodes (invite by player code, friends or not).
 * 0.10.0 — MatchEnded RankedSeat: coinsWon is every seat's place share
 * (CPUs and leavers too, shown not credited); new stake (the entry put
 * in); CPUs show the flat completion bonus.
 */
const val CONTRACT_VERSION: String = "0.10.0"
