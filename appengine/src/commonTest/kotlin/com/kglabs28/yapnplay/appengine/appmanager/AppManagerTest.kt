package com.kglabs28.yapnplay.appengine.appmanager

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private class FakeStore(initial: Map<String, String> = emptyMap()) : AppConfigStore {
    val saved = mutableMapOf<String, String>()
    private val map = initial.toMutableMap()

    override fun getAll(): Map<String, String> = map.toMap()

    override fun saveAll(map: Map<String, String>) {
        saved.putAll(map)
        this.map.putAll(map)
    }
}

/**
 * AppManager contract: local-first, remote merges over local and persists,
 * every getter never throws and falls back to its default.
 */
class AppManagerTest {

    @Test
    fun `empty store yields defaults`() {
        val manager = AppManager(FakeStore())
        assertTrue(manager.isVoiceEnabled())
        assertTrue(manager.isRoyalLudoEnabled())
        assertTrue(manager.isPartyChatEnabled())
        assertEquals("", manager.updateConfig())
    }

    @Test
    fun `local values load at construction`() {
        val manager = AppManager(FakeStore(mapOf(AppManagerConstants.Key.VOICE_ENABLED to "false")))
        assertFalse(manager.isVoiceEnabled())
        assertTrue(manager.isRoyalLudoEnabled())
    }

    @Test
    fun `remote merges over local and persists`() {
        val store = FakeStore(mapOf(AppManagerConstants.Key.VOICE_ENABLED to "true"))
        val manager = AppManager(store)
        manager.syncWithRemote(mapOf(AppManagerConstants.Key.VOICE_ENABLED to "false"))
        assertFalse(manager.isVoiceEnabled())
        assertEquals("false", store.saved[AppManagerConstants.Key.VOICE_ENABLED])
    }

    @Test
    fun `empty remote keeps local values`() {
        val manager = AppManager(FakeStore(mapOf(AppManagerConstants.Key.VOICE_ENABLED to "false")))
        manager.syncWithRemote(emptyMap())
        assertFalse(manager.isVoiceEnabled())
    }

    @Test
    fun `garbage values fall back to defaults`() {
        val manager = AppManager(
            FakeStore(
                mapOf(
                    AppManagerConstants.Key.VOICE_ENABLED to "maybe",
                    "custom_int" to "abc",
                ),
            ),
        )
        assertTrue(manager.isVoiceEnabled())
        assertEquals(7, manager.getInt("custom_int", 7))
    }

    @Test
    fun `throwing store still yields defaults`() {
        val broken = object : AppConfigStore {
            override fun getAll(): Map<String, String> = throw IllegalStateException("disk gone")
            override fun saveAll(map: Map<String, String>) = throw IllegalStateException("disk gone")
        }
        val manager = AppManager(broken)
        assertTrue(manager.isVoiceEnabled())
        // Persist fails internally but must not throw out; memory still
        // takes the fresher remote value for this session.
        manager.syncWithRemote(mapOf(AppManagerConstants.Key.VOICE_ENABLED to "false"))
        assertFalse(manager.isVoiceEnabled())
    }

    @Test
    fun `typed getters parse correctly`() {
        val manager = AppManager(
            FakeStore(
                mapOf(
                    "s" to "hi",
                    "b" to "true",
                    "i" to "42",
                    "l" to "4200000000",
                    "f" to "1.5",
                    "d" to "2.5",
                ),
            ),
        )
        assertEquals("hi", manager.getString("s"))
        assertTrue(manager.getBoolean("b"))
        assertEquals(42, manager.getInt("i"))
        assertEquals(4200000000L, manager.getLong("l"))
        assertEquals(1.5f, manager.getFloat("f"))
        assertEquals(2.5, manager.getDouble("d"))
        assertEquals("fallback", manager.getString("missing", "fallback"))
    }
}
