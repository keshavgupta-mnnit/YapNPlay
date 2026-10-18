package com.kglabs28.yapnplay.appengine.protocol.transport

/**
 * The contract version. Any change to a contract module bumps this
 * and is announced to all development chats.
 *
 * Current: 0.3.0 — Quick Play matchmaking channel (JoinQueue, LeaveQueue,
 * QueueStatus, MatchFound, QueueCancelled), SelectGame, SetBotFill +
 * RoomInfo.botFill, lobby (adminId, description, houseRules, member
 * watching/micOn/speakerOn/isFriend/onlineSince, SetHouseRule,
 * SetWatching, SetVoiceState, TransferLeader, TransferAdmin, NOT_ADMIN),
 * GameDescriptor.houseRuleOptions/fixedRules, CPU seats, MatchmakingConfig.seats,
 * GameDescriptor.matchmaking/showcase, optional Me progression fields,
 * RoomSummary leader/icon, Friends, OnlineStats, new error codes.
 * Previous: 0.2.0 — app-engine restructure.
 */
const val CONTRACT_VERSION: String = "0.3.1"
