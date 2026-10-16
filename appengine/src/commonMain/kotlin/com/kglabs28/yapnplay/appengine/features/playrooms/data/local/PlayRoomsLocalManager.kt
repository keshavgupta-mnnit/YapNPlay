package com.kglabs28.yapnplay.appengine.features.playrooms.data.local

import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoom

/**
 * Local copy of the user's rooms: instant reads, offline survival.
 * In-memory today; persist in AppDatabase (Room) instead, keeping
 * this same API so callers never change.
 */
class PlayRoomsLocalManager {

    private var cachedRooms: List<PlayRoom> = emptyList()

    fun rooms(): List<PlayRoom> = cachedRooms

    fun saveRooms(rooms: List<PlayRoom>) {
        cachedRooms = rooms
    }

    /** A newly joined room goes to the top, replacing any stale copy. */
    fun addRoom(room: PlayRoom) {
        cachedRooms = listOf(room) + cachedRooms.filterNot { it.id == room.id }
    }

    fun clear() {
        cachedRooms = emptyList()
    }
}
