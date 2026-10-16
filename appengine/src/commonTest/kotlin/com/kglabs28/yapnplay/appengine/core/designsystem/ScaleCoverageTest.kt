package com.kglabs28.yapnplay.appengine.core.designsystem

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The scaling mechanism must be impossible to bypass by accident.
 * Every token multiplies through the screen multiplier at read time.
 * Adding a token: declare it with `.scaled()` / `.scaledSp` AND add
 * one line below — the compiler forces the value, this test forces
 * the scaling.
 */
class ScaleCoverageTest {

    private fun assertDpFollows(label: String, reads: List<() -> Dp>) {
        AdaptiveScale.overrideForTests(1f)
        val base = reads.map { it() }
        AdaptiveScale.overrideForTests(2f)
        try {
            val doubled = reads.map { it() }
            base.zip(doubled).forEachIndexed { i, (b, d) ->
                assertEquals(b * 2f, d, "$label[$i] skips the multiplier")
            }
        } finally {
            AdaptiveScale.overrideForTests(1f)
        }
    }

    private fun assertSpFollows(label: String, reads: List<() -> TextStyle>) {
        AdaptiveScale.overrideForTests(1f)
        val base = reads.map { it().fontSize.value }
        AdaptiveScale.overrideForTests(2f)
        try {
            val doubled = reads.map { it().fontSize.value }
            base.zip(doubled).forEachIndexed { i, (b, d) ->
                assertEquals(b * 2f, d, 0.01f, "$label[$i] skips the multiplier")
            }
        } finally {
            AdaptiveScale.overrideForTests(1f)
        }
    }

    @Test
    fun `every dimens field scales`() {
        val d = YapNPlayDimens()
        assertDpFollows(
            "dimens",
            listOf(
                { d.touchMin }, { d.buttonHeight }, { d.chipHeight },
                { d.memberRowHeight }, { d.seatCardSize }, { d.diceSize },
                { d.avatarList }, { d.avatarCard }, { d.avatarRing },
                { d.panelRadius }, { d.buttonCut }, { d.timerStroke },
                { d.borderThin }, { d.hairline }, { d.zero },
                { d.timerRing }, { d.statusDot }, { d.statusDotSmall },
                { d.voiceToggle }, { d.contentMaxWidth }, { d.glowBlur },
                { d.cardRadius }, { d.badgeSize }, { d.badgeMedium },
                { d.badgeLarge }, { d.badgeXLarge }, { d.graphicSize },
                { d.graphicMaxHeight }, { d.cardHeaderIconSize },
                { d.compactButtonHeight }, { d.progressWidth },
                { d.progressHeight }, { d.headerPillHeight },
                { d.logoWidth }, { d.logoHeight },
            ),
        )
    }

    @Test
    fun `every spacing gap scales`() {
        assertDpFollows(
            "spacing",
            listOf(
                { Spacing.Xxs }, { Spacing.Xs }, { Spacing.Sm },
                { Spacing.Md }, { Spacing.Lg }, { Spacing.Xl },
            ),
        )
    }

    @Test
    fun `every type size scales`() {
        val t = YapNPlayTypography()
        assertSpFollows(
            "type",
            listOf(
                { t.display }, { t.headline }, { t.title }, { t.body },
                { t.label }, { t.caption }, { t.micro },
            ),
        )
    }
}
