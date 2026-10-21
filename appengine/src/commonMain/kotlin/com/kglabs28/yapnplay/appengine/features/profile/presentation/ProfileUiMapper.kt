package com.kglabs28.yapnplay.appengine.features.profile.presentation

import com.kglabs28.yapnplay.appengine.core.ui.ProfileStrings
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.features.profile.domain.BlockedUser
import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend
import com.kglabs28.yapnplay.appengine.features.profile.domain.FriendRequestOutcome
import com.kglabs28.yapnplay.appengine.features.profile.domain.ProfileScreenData
import com.kglabs28.yapnplay.appengine.ui.screens.profile.FriendsTab
import com.kglabs28.yapnplay.appengine.ui.screens.profile.ProfileBlockedUi
import com.kglabs28.yapnplay.appengine.ui.screens.profile.ProfilePersonUi
import com.kglabs28.yapnplay.appengine.ui.screens.profile.ProfileRequestUi
import com.kglabs28.yapnplay.appengine.ui.screens.profile.ProfileStatKind
import com.kglabs28.yapnplay.appengine.ui.screens.profile.ProfileState
import com.kglabs28.yapnplay.appengine.ui.screens.profile.ProfileStatUi
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/** Profile data to what the Profile screen draws. Pure functions. */
object ProfileUiMapper {

    fun state(state: ProfileState, data: ProfileScreenData): ProfileState {
        val p = data.profile
        val friends = data.friends.sortedWith(compareBy<Friend> { !it.online }.thenBy { it.name.lowercase() })
        return state.copy(
            loaded = true,
            name = p.name,
            username = ProfileStrings.profileUsername(p.username),
            userCode = p.userCode,
            avatarId = p.avatarId,
            stats = listOf(
                ProfileStatUi(ProfileStatKind.GAMES, p.gamesPlayed.toString(), ProfileStrings.profileStatGames),
                ProfileStatUi(ProfileStatKind.WINS, p.gamesWon.toString(), ProfileStrings.profileStatWins),
                ProfileStatUi(ProfileStatKind.FRIENDS, friends.size.toString(), ProfileStrings.profileStatFriends),
                ProfileStatUi(ProfileStatKind.LEVEL, p.level.toString(), ProfileStrings.profileStatLevel),
            ),
            friends = friends,
            onlineCount = friends.count { it.online },
            offlineCount = friends.count { !it.online },
            tabFriends = filter(friends, state.friendsTab),
            allTabFriends = filter(friends, state.allFriendsTab),
            requests = data.requests.map { ProfileRequestUi(it.requestId, person(it.from)) },
            requestsLabel = ProfileStrings.profileRequestsPending(data.requests.size),
            blocked = data.blocked.map { blocked(it) },
            blockedLabel = ProfileStrings.profileBlockedLabel(data.blocked.size),
        )
    }

    fun filter(friends: List<Friend>, tab: FriendsTab): List<Friend> = when (tab) {
        FriendsTab.ALL -> friends
        FriendsTab.ONLINE -> friends.filter { it.online }
        FriendsTab.OFFLINE -> friends.filterNot { it.online }
    }

    fun person(friend: Friend): ProfilePersonUi = ProfilePersonUi(
        userId = friend.userId,
        name = friend.name,
        username = ProfileStrings.profileUsername(friend.username),
        userCode = friend.userCode,
        avatarId = friend.avatarId,
        online = friend.online,
        status = if (friend.online) ProfileStrings.profileOnline else ProfileStrings.profileOffline,
    )

    private fun blocked(user: BlockedUser): ProfileBlockedUi {
        val date = Instant.fromEpochMilliseconds(user.blockedAt).toLocalDateTime(TimeZone.currentSystemDefault()).date
        return ProfileBlockedUi(
            userId = user.userId,
            name = user.name,
            username = ProfileStrings.profileUsername(user.username),
            avatarId = user.avatarId,
            blockedOn = ProfileStrings.profileBlockedOn(Strings.shortDate(date.dayOfMonth, date.monthNumber, date.year)),
        )
    }

    /** The line under Add Friend after sending, and whether it's an error. */
    fun outcome(outcome: FriendRequestOutcome, code: String): Pair<String, Boolean> = when (outcome) {
        FriendRequestOutcome.SENT -> ProfileStrings.profileRequestSent(code) to false
        FriendRequestOutcome.ACCEPTED -> ProfileStrings.profileNowFriends to false
        FriendRequestOutcome.NOT_FOUND -> ProfileStrings.profileCodeNotFound to true
        FriendRequestOutcome.SELF -> ProfileStrings.profileOwnCode to true
        FriendRequestOutcome.ALREADY_FRIENDS -> ProfileStrings.profileAlreadyFriends to true
        FriendRequestOutcome.ALREADY_REQUESTED -> ProfileStrings.profileAlreadyRequested to true
        FriendRequestOutcome.BLOCKED -> ProfileStrings.profileBlockedCode to true
    }

    /** What's typed into the ID box: upper case, only ID characters, at most the ID's length. */
    fun cleanCode(text: String): String =
        text.uppercase().filter { it in AppConstants.USER_CODE_ALPHABET }.take(AppConstants.USER_CODE_LENGTH)
}
