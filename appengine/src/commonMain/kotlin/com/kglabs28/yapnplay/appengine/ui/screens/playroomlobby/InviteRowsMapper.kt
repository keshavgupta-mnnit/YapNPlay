package com.kglabs28.yapnplay.appengine.ui.screens.playroomlobby

import com.kglabs28.yapnplay.appengine.core.ui.NearbyStrings
import com.kglabs28.yapnplay.appengine.features.nearby.domain.NearbyPeer
import com.kglabs28.yapnplay.appengine.features.nearby.domain.PeerInvite
import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend
import com.kglabs28.yapnplay.appengine.features.profile.domain.FriendActivity

/** Turns friends or nearby phones into the party's Add Friends rows: free ones first. */
object InviteRowsMapper {

    /**
     * Online friends not in the party yet. Only someone free can be added: one
     * playing a game or in a room lobby shows why not, and one already asked
     * ([invited]) shows "Invited".
     */
    fun fromFriends(friends: List<Friend>, members: Set<String>, invited: Set<String>): List<InviteRowUi> =
        friends.filter { it.online && it.userId !in members }
            .map { friend ->
                val asked = friend.userId in invited
                val (status, tone) = when {
                    asked -> NearbyStrings.statusInvited to InviteTone.DONE
                    friend.activity == FriendActivity.PLAYING -> NearbyStrings.statusPlaying to InviteTone.BUSY
                    friend.activity == FriendActivity.IN_ROOM -> NearbyStrings.statusInRoom to InviteTone.BUSY
                    else -> NearbyStrings.statusOnline to InviteTone.FREE
                }
                InviteRowUi(friend.userId, friend.name, friend.avatarId, status, tone, canAdd = tone == InviteTone.FREE)
            }
            .sortedWith(compareBy({ it.tone.ordinal }, { it.name.lowercase() }))

    /** Phones around that can be asked (anyone already in the party is left out). */
    fun fromPeers(peers: List<NearbyPeer>, members: Set<String>): List<InviteRowUi> =
        peers.filter { it.player.userId !in members && it.invite != PeerInvite.JOINED }
            .map { peer ->
                val (status, tone) = when (peer.invite) {
                    PeerInvite.NONE -> NearbyStrings.statusNearby to InviteTone.FREE
                    PeerInvite.INVITING -> NearbyStrings.statusInviting to InviteTone.DONE
                    PeerInvite.INVITED -> NearbyStrings.statusInvited to InviteTone.DONE
                    PeerInvite.JOINED -> NearbyStrings.statusJoined to InviteTone.DONE
                    PeerInvite.DECLINED -> NearbyStrings.statusDeclined to InviteTone.AWAY
                }
                val canAdd = peer.invite == PeerInvite.NONE || peer.invite == PeerInvite.DECLINED
                InviteRowUi(peer.endpointId, peer.player.name, peer.player.avatarId, status, tone, canAdd)
            }
            .sortedWith(compareBy({ it.tone.ordinal }, { it.name.lowercase() }))
}
