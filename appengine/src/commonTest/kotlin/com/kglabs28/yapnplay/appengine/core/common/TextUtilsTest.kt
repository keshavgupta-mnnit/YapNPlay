package com.kglabs28.yapnplay.appengine.core.common

import kotlin.test.Test
import kotlin.test.assertEquals

class TextUtilsTest {

    @Test
    fun `short text passes through`() {
        assertEquals("abc", TextUtils.capLength("abc", 6))
    }

    @Test
    fun `exact length passes through`() {
        assertEquals("abcdef", TextUtils.capLength("abcdef", 6))
    }

    @Test
    fun `overflow truncates`() {
        assertEquals("abcdef", TextUtils.capLength("abcdefghij", 6))
    }

    @Test
    fun `negative limit disables capping`() {
        assertEquals("abcdefghij", TextUtils.capLength("abcdefghij", -1))
    }
}
