package com.kglabs28.yapnplay.appengine.core.designsystem

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

/**
 * YapNPlay type scale. Sizes match the approved design list at 1.0.
 * Every size multiplies with the screen at read time — add a style
 * with `.scaledSp` and it follows automatically, nothing else to wire.
 * Body text never goes below 12sp at baseline; tiny metadata (online
 * dots, scores, pillar captions) uses micro.
 */
class YapNPlayTypography(
    private val headlineFamily: FontFamily = FontFamily.Default,
    private val bodyFamily: FontFamily = FontFamily.Default,
) {
    val display: TextStyle
        get() = TextStyle(
            fontFamily = headlineFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 32.scaledSp,
            lineHeight = 38.scaledSp,
        )
    val headline: TextStyle
        get() = TextStyle(
            fontFamily = headlineFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.scaledSp,
            lineHeight = 28.scaledSp,
        )
    val title: TextStyle
        get() = TextStyle(
            fontFamily = headlineFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.scaledSp,
            lineHeight = 22.scaledSp,
        )
    val body: TextStyle
        get() = TextStyle(
            fontFamily = bodyFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 13.scaledSp,
            lineHeight = 18.scaledSp,
        )
    val label: TextStyle
        get() = TextStyle(
            fontFamily = bodyFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.scaledSp,
            lineHeight = 16.scaledSp,
        )
    val caption: TextStyle
        get() = TextStyle(
            fontFamily = bodyFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.scaledSp,
            lineHeight = 16.scaledSp,
        )
    /** Card headline: ENTER GAME ZONE, CREATE NEW. */
    val cardTitle: TextStyle
        get() = TextStyle(
            fontFamily = headlineFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 15.scaledSp,
            lineHeight = 18.scaledSp,
        )

    /** Oversized hero word: ROOM. */
    val heroWord: TextStyle
        get() = TextStyle(
            fontFamily = headlineFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.scaledSp,
            lineHeight = 26.scaledSp,
        )

    /** Panel headings: Your Rooms, Online Friends. */
    val sectionTitle: TextStyle
        get() = TextStyle(
            fontFamily = bodyFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.scaledSp,
            lineHeight = 14.scaledSp,
        )

    /** List item names (rooms, friends). */
    val itemTitle: TextStyle
        get() = TextStyle(
            fontFamily = bodyFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 10.scaledSp,
            lineHeight = 13.scaledSp,
        )

    /** The lobby START MATCH label. */
    val startLabel: TextStyle
        get() = TextStyle(
            fontFamily = headlineFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.scaledSp,
            lineHeight = 22.scaledSp,
        )

    /** Pill button labels: QUICK PLAY, JOIN, Create Room. */
    val button: TextStyle
        get() = TextStyle(
            fontFamily = headlineFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 11.scaledSp,
            lineHeight = 14.scaledSp,
        )

    val micro: TextStyle
        get() = TextStyle(
            fontFamily = bodyFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 10.scaledSp,
            lineHeight = 13.scaledSp,
        )

    /** The leaderboard podium's player card: name, coins won, and the title chip. */
    val podiumName: TextStyle
        get() = TextStyle(fontFamily = headlineFamily, fontWeight = FontWeight.Black, fontSize = 15.scaledSp, lineHeight = 18.scaledSp)
    val podiumCoins: TextStyle
        get() = TextStyle(fontFamily = headlineFamily, fontWeight = FontWeight.Bold, fontSize = 16.scaledSp, lineHeight = 20.scaledSp)
    val podiumChip: TextStyle
        get() = TextStyle(fontFamily = bodyFamily, fontWeight = FontWeight.Bold, fontSize = 12.scaledSp, lineHeight = 15.scaledSp)
    /** The top bar: the tagline under the app's name, a tab's label, and the level number in its shield. */
    val topBarBrand: TextStyle
        get() = TextStyle(fontFamily = headlineFamily, fontWeight = FontWeight.Black, fontSize = 18.scaledSp, lineHeight = 21.scaledSp)
    val topBarTagline: TextStyle
        get() = TextStyle(fontFamily = bodyFamily, fontWeight = FontWeight.Medium, fontSize = 8.scaledSp, lineHeight = 10.scaledSp)
    /** The XP pill's "350 / 1,000 XP", small enough for "7,999 / 10,000 XP". */
    val xpLabel: TextStyle
        get() = TextStyle(fontFamily = bodyFamily, fontWeight = FontWeight.SemiBold, fontSize = 9.scaledSp, lineHeight = 11.scaledSp)
    /** A home card's heading ("ENTER GAME ZONE"). */
    val zoneTitle: TextStyle
        get() = TextStyle(fontFamily = headlineFamily, fontWeight = FontWeight.Bold, fontSize = 19.scaledSp, lineHeight = 23.scaledSp)
    val topBarTab: TextStyle
        get() = TextStyle(fontFamily = bodyFamily, fontWeight = FontWeight.Medium, fontSize = 11.scaledSp, lineHeight = 14.scaledSp)
    val levelBadge: TextStyle
        get() = TextStyle(fontFamily = bodyFamily, fontWeight = FontWeight.Black, fontSize = 13.scaledSp, lineHeight = 15.scaledSp)
    /** A home card's subtitle, over two lines. */
    val zoneSubtitle: TextStyle
        get() = TextStyle(fontFamily = bodyFamily, fontWeight = FontWeight.Normal, fontSize = 13.scaledSp, lineHeight = 17.scaledSp)
}
