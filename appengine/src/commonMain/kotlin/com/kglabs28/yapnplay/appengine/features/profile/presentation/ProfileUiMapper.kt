package com.kglabs28.yapnplay.appengine.features.profile.presentation

import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend
import com.kglabs28.yapnplay.appengine.features.profile.domain.FriendRequestOutcome
import com.kglabs28.yapnplay.appengine.features.profile.domain.ProfileScreenData
import com.kglabs28.yapnplay.appengine.ui.screens.profile.ProfilePersonUi
import com.kglabs28.yapnplay.appengine.ui.screens.profile.ProfileRequestUi
import com.kglabs28.yapnplay.appengine.ui.screens.profile.ProfileState
import com.kglabs28.yapnplay.appengine.ui.screens.profile.ProfileStatUi

/** Profile data to what the Profile screen draws. Pure functions. */
object ProfileUiMapper {

    fun state(state: ProfileState, data: ProfileScreenData): ProfileState {
        val p = data.profile
        val friends = data.friends.sortedWith(compareBy<Friend> { !it.online }.thenBy { it.name.lowercase() })
        return state.copy(
            loaded = true,
            name = p.name,
            username = Strings.profileUsername(p.username),
            userCode = p.userCode,
            avatarId = p.avatarId,
            stats = listOf(
                ProfileStatUi(p.gamesPlayed.toString(), Strings.profileStatGames),
                ProfileStatUi(p.gamesWon.toString(), Strings.profileStatWins),
                ProfileStatUi(friends.size.toString(), Strings.profileStatFriends),
            ),
            friends = friends,
            friendsSubtitle = Strings.profileFriendsSubtitle(friends.size, friends.count { it.online }),
            requests = data.requests.map { ProfileRequestUi(it.requestId, person(it.from)) },
            requestsLabel = Strings.profileRequestsPending(data.requests.size),
        )
    }

    fun person(friend: Friend): ProfilePersonUi = ProfilePersonUi(
        userId = friend.userId,
        name = friend.name,
        username = Strings.profileUsername(friend.username),
        userCode = Strings.profileUserCode(friend.userCode),
        avatarId = friend.avatarId,
        online = friend.online,
        status = if (friend.online) Strings.profileOnline else Strings.profileOffline,
    )

    /** The line under "Add a friend" after sending, and whether it's an error. */
    fun outcome(outcome: FriendRequestOutcome, code: String): Pair<String, Boolean> = when (outcome) {
        FriendRequestOutcome.SENT -> Strings.profileRequestSent(code) to false
        FriendRequestOutcome.ACCEPTED -> Strings.profileNowFriends to false
        FriendRequestOutcome.NOT_FOUND -> Strings.profileCodeNotFound to true
        FriendRequestOutcome.SELF -> Strings.profileOwnCode to true
        FriendRequestOutcome.ALREADY_FRIENDS -> Strings.profileAlreadyFriends to true
        FriendRequestOutcome.ALREADY_REQUESTED -> Strings.profileAlreadyRequested to true
        FriendRequestOutcome.BLOCKED -> Strings.profileBlockedCode to true
    }

    /** What's typed into the ID box: upper case, only ID characters, at most the ID's length. */
    fun cleanCode(text: String): String =
        text.uppercase().filter { it in AppConstants.USER_CODE_ALPHABET }.take(AppConstants.USER_CODE_LENGTH)
}
