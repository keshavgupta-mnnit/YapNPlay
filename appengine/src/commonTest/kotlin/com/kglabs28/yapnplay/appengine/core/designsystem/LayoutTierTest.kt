package com.kglabs28.yapnplay.appengine.core.designsystem

import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tier thresholds: S below 740dp, M from 740dp, L from 960dp.
 * Reference windows: 640x360 -> S, 800x360 and 915x412 -> M, 1280x800 -> L.
 */
class LayoutTierTest {

    @Test
    fun `small phone is tier S`() {
        assertEquals(LayoutTier.S, layoutTierFor(640))
    }

    @Test
    fun `landscape phones are tier M`() {
        assertEquals(LayoutTier.M, layoutTierFor(800))
        assertEquals(LayoutTier.M, layoutTierFor(915))
    }

    @Test
    fun `tablet is tier L`() {
        assertEquals(LayoutTier.L, layoutTierFor(1280))
    }

    @Test
    fun `threshold boundaries`() {
        assertEquals(LayoutTier.S, layoutTierFor(739))
        assertEquals(LayoutTier.M, layoutTierFor(740))
        assertEquals(LayoutTier.M, layoutTierFor(959))
        assertEquals(LayoutTier.L, layoutTierFor(960))
    }

    @Test
    fun `multiplier follows the tighter side against the reference screen`() {
        assertEquals(1f, AdaptiveScale.resolveMultiplier(870, 391))
        // Wide but short: the height decides.
        assertEquals(1f, AdaptiveScale.resolveMultiplier(1740, 391))
        // Narrow: the width decides.
        assertEquals(696f / 870f, AdaptiveScale.resolveMultiplier(696, 391))
        // Upright: the narrow width decides, so the whole design still fits.
        assertEquals(391f / 870f, AdaptiveScale.resolveMultiplier(391, 870))
    }

    @Test
    fun `tokens follow the multiplier`() {
        AdaptiveScale.overrideForTests(1f)
        val base = YapNPlayDimens().avatarCard
        AdaptiveScale.overrideForTests(1.35f)
        try {
            assertEquals(base * 1.35f, YapNPlayDimens().avatarCard)
        } finally {
            AdaptiveScale.overrideForTests(1f)
        }
    }
}
