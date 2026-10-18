package com.kglabs28.yapnplay.appengine.features.profile.data.remote

import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.common.FakeDataProvider
import com.kglabs28.yapnplay.appengine.protocol.BlockUserBody
import com.kglabs28.yapnplay.appengine.protocol.BlockedUserSummary
import com.kglabs28.yapnplay.appengine.protocol.BlockedUsers
import com.kglabs28.yapnplay.appengine.protocol.FriendRequestOutcomeCode
import com.kglabs28.yapnplay.appengine.protocol.FriendRequests
import com.kglabs28.yapnplay.appengine.protocol.Friends
import com.kglabs28.yapnplay.appengine.protocol.Me
import com.kglabs28.yapnplay.appengine.protocol.SendFriendRequestBody
import com.kglabs28.yapnplay.appengine.protocol.SendFriendRequestReply
import com.kglabs28.yapnplay.appengine.protocol.UpdateProfileBody
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Stand-in for the profile and friends endpoints. Keeps the user, their
 * friends, pending requests, sent requests and blocks in memory, so every
 * action on the Profile screen behaves as it will against the server.
 * ProfileApiClient's production calls replace it (Phase 12).
 */
class FakeProfileServer(private val clock: AppClock) {

    private val mutex = Mutex()
    private var me: Me = FakeDataProvider.Profile.me
    private var friends = FakeDataProvider.Profile.friends.friends
    private var requests = FakeDataProvider.Profile.requests
    private val directory = FakeDataProvider.Profile.directory
    private val sentTo = mutableSetOf<String>()
    private var blocked = FakeDataProvider.Profile.blocked

    suspend fun me(): Me = mutex.withLock { me }

    suspend fun friends(): Friends = mutex.withLock { Friends(friends) }

    suspend fun requests(): FriendRequests = mutex.withLock { FriendRequests(requests) }

    suspend fun updateMe(body: UpdateProfileBody): Me = mutex.withLock {
        me = me.copy(
            displayName = body.displayName ?: me.displayName,
            username = body.username ?: me.username,
            avatarId = body.avatarId ?: me.avatarId,
        )
        FakeDataProvider.Profile.myAvatarId = me.avatarId
        me
    }

    suspend fun sendRequest(body: SendFriendRequestBody): SendFriendRequestReply = mutex.withLock {
        val code = body.userCode.uppercase()
        val theirs = requests.firstOrNull { it.from.userCode == code }
        val person = directory.firstOrNull { it.userCode == code } ?: theirs?.from
        val outcome = when {
            code == me.userCode -> FriendRequestOutcomeCode.SELF
            friends.any { it.userCode == code } -> FriendRequestOutcomeCode.ALREADY_FRIENDS
            person == null -> FriendRequestOutcomeCode.NOT_FOUND
            blocked.any { it.userId == person.userId } -> FriendRequestOutcomeCode.BLOCKED
            theirs != null -> {
                acceptLocked(theirs.requestId)
                FriendRequestOutcomeCode.ACCEPTED
            }
            person.userId in sentTo -> FriendRequestOutcomeCode.ALREADY_REQUESTED
            else -> {
                sentTo += person.userId
                FriendRequestOutcomeCode.SENT
            }
        }
        SendFriendRequestReply(outcome)
    }

    suspend fun accept(requestId: String) = mutex.withLock { acceptLocked(requestId) }

    suspend fun decline(requestId: String) = mutex.withLock {
        requests = requests.filterNot { it.requestId == requestId }
    }

    suspend fun removeFriend(userId: String) = mutex.withLock {
        friends = friends.filterNot { it.userId == userId }
    }

    suspend fun blocked(): BlockedUsers = mutex.withLock { BlockedUsers(blocked) }

    suspend fun unblock(userId: String) = mutex.withLock {
        blocked = blocked.filterNot { it.userId == userId }
    }

    suspend fun block(body: BlockUserBody) = mutex.withLock {
        val person = (friends + requests.map { it.from } + directory).firstOrNull { it.userId == body.userId }
        if (person != null && blocked.none { it.userId == person.userId }) {
            blocked = listOf(BlockedUserSummary(person.userId, person.displayName, person.username, person.avatarId, clock.now().toEpochMilliseconds())) + blocked
        }
        friends = friends.filterNot { it.userId == body.userId }
        requests = requests.filterNot { it.from.userId == body.userId }
    }

    private fun acceptLocked(requestId: String) {
        val request = requests.firstOrNull { it.requestId == requestId } ?: return
        requests = requests - request
        if (friends.none { it.userId == request.from.userId }) friends = friends + request.from
    }
}
