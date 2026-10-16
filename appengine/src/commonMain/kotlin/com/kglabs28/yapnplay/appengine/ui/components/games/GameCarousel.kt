package com.kglabs28.yapnplay.appengine.ui.components.games

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.util.lerp
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.LoopIndex
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.features.gamecatalog.domain.GameCatalogItem
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

/**
 * Reusable looping game carousel (Game Zone, room lobby). The focused
 * game sits in the centre; neighbours stay visible but smaller, dimmed
 * and blurred; ‹ › and swipes go round the loop forever (first ← last).
 * Position dots sit under the focused game. The caller owns
 * [focusedIndex], hears changes through [onFocusChange], and puts its
 * own action (QUICK PLAY, SELECT) below.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameCarousel(
    games: List<GameCatalogItem>,
    focusedIndex: Int,
    onFocusChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = YapNPlayTheme.dimens
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = LoopIndex.middlePageFor(focusedIndex, games.size, AppConstants.CAROUSEL_LOOP_PAGES),
    ) { AppConstants.CAROUSEL_LOOP_PAGES }

    // Keep the pager and the caller's focus in step, in both directions.
    LaunchedEffect(focusedIndex) {
        val steps = LoopIndex.stepsTo(pagerState.currentPage, focusedIndex, games.size)
        if (steps != 0) pagerState.animateScrollToPage(pagerState.currentPage + steps)
    }
    LaunchedEffect(pagerState.currentPage) {
        val item = LoopIndex.itemAt(pagerState.currentPage, games.size)
        if (item != focusedIndex) onFocusChange(item)
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            val pageWidth = maxWidth * AppConstants.CAROUSEL_PAGE_WIDTH_FRACTION
            val sidePadding = (maxWidth - pageWidth) / 2

            HorizontalPager(
                state = pagerState,
                pageSize = PageSize.Fixed(pageWidth),
                contentPadding = PaddingValues(horizontal = sidePadding),
                beyondViewportPageCount = AppConstants.CAROUSEL_PAGES_BEYOND_VIEW,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                val index = LoopIndex.itemAt(page, games.size)
                val distance = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
                    .absoluteValue
                    .coerceIn(0f, 1f)
                GameShowcaseCard(
                    game = games[index],
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            val scale = lerp(1f, AppConstants.CAROUSEL_SIDE_SCALE, distance)
                            scaleX = scale
                            scaleY = scale
                            alpha = lerp(1f, AppConstants.CAROUSEL_SIDE_ALPHA, distance)
                        }
                        .blur(dimens.carouselSideBlur * distance),
                )
            }

            CarouselArrowButton(
                icon = Icons.Rounded.ChevronLeft,
                contentDescription = Strings.gameZonePreviousGame,
                onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
                modifier = Modifier.align(Alignment.CenterStart),
            )
            CarouselArrowButton(
                icon = Icons.Rounded.ChevronRight,
                contentDescription = Strings.gameZoneNextGame,
                onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                modifier = Modifier.align(Alignment.CenterEnd),
            )
        }
        CarouselDots(count = games.size, selectedIndex = focusedIndex)
    }
}
