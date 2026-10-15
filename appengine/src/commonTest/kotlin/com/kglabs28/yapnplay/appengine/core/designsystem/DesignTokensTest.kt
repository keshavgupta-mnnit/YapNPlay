package com.kglabs28.yapnplay.appengine.core.designsystem

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Every token equals the approved design list. If the design changes,
 * update the list AND this test together.
 */
class DesignTokensTest {

    @Test
    fun `surfaces match design values`() {
        assertEquals(Color(0xFF050917), YapNPlayColors.Background)
        assertEquals(Color(0xFF152235), YapNPlayColors.Panel)
        assertEquals(Color(0xB3233246), YapNPlayColors.Glass)
        assertEquals(Color(0xFF323B4D), YapNPlayColors.Raised)
    }

    @Test
    fun `accents match design values`() {
        assertEquals(Color(0xFF3ADBED), YapNPlayColors.Cyan)
        assertEquals(Color(0xFF09FEFE), YapNPlayColors.ButtonFace)
        assertEquals(Color(0xFFDF8733), YapNPlayColors.Orange)
        assertEquals(Color(0xFFC0671F), YapNPlayColors.OrangeCore)
        assertEquals(Color(0xFFE64DA0), YapNPlayColors.Pink)
        assertEquals(Color(0xFFC26FE8), YapNPlayColors.Purple)
    }

    @Test
    fun `states match design values`() {
        assertEquals(Color(0xFF72F597), YapNPlayColors.Success)
        assertEquals(Color(0xFFF0A030), YapNPlayColors.Warning)
        assertEquals(Color(0xFFD9344A), YapNPlayColors.Error)
    }

    @Test
    fun `text colors match design values`() {
        assertEquals(Color(0xFFF2F6FF), YapNPlayColors.TextPrimary)
        assertEquals(Color(0xFF9FB0C8), YapNPlayColors.TextSecondary)
        assertEquals(Color(0xFF5B6880), YapNPlayColors.TextDisabled)
    }

    @Test
    fun `player colors match design values`() {
        assertEquals(Color(0xFFD9344A), YapNPlayColors.PlayerRed)
        assertEquals(Color(0xFF2DD462), YapNPlayColors.PlayerGreen)
        assertEquals(Color(0xFFEDD942), YapNPlayColors.PlayerYellow)
        assertEquals(Color(0xFF338ED0), YapNPlayColors.PlayerBlue)
    }

    @Test
    fun `type scale matches design values`() {
        val type = YapNPlayTypography()
        assertEquals(40.sp, type.display.fontSize)
        assertEquals(48.sp, type.display.lineHeight)
        assertEquals(28.sp, type.headline.fontSize)
        assertEquals(34.sp, type.headline.lineHeight)
        assertEquals(20.sp, type.title.fontSize)
        assertEquals(26.sp, type.title.lineHeight)
        assertEquals(16.sp, type.body.fontSize)
        assertEquals(22.sp, type.body.lineHeight)
        assertEquals(14.sp, type.label.fontSize)
        assertEquals(18.sp, type.label.lineHeight)
        assertEquals(12.sp, type.caption.fontSize)
        assertEquals(16.sp, type.caption.lineHeight)
    }

    @Test
    fun `no text style is smaller than 12sp`() {
        val type = YapNPlayTypography()
        val sizes = listOf(
            type.display.fontSize, type.headline.fontSize, type.title.fontSize,
            type.body.fontSize, type.label.fontSize, type.caption.fontSize,
        )
        assertTrue(sizes.all { it >= 12.sp })
    }

    @Test
    fun `motion tokens are positive`() {
        assertTrue(MotionTokens.FAST_MS > 0)
        assertTrue(MotionTokens.STANDARD_MS > MotionTokens.FAST_MS)
        assertTrue(MotionTokens.SLOW_MS > MotionTokens.STANDARD_MS)
    }
}
