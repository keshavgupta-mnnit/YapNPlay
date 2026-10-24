package com.kglabs28.yapnplay.appengine.ui.components.coins

import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.AppLog
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.rounded.OndemandVideo
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.core.designsystem.MotionTokens
import com.kglabs28.yapnplay.appengine.core.common.TextUtils
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.CoinsStrings
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton

/**
 * "Watch an ad for coins", on festive violet-magenta glass: today's
 * progress as a rainbow bar ("2/5"), then the day's ladder — one coloured
 * chip per ad with what it pays (the next one lit, watched ones ticked).
 * WATCH AD (gold) says what the next ad pays; PLAY STARTER shows when
 * [CoinsOfferUi.offerFree].
 */
@Composable
fun CoinsOfferCard(
    offer: CoinsOfferUi,
    onWatchAd: () -> Unit,
    onPlayFree: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val next = offer.nextReward
    ModalCard(
        title = offer.title,
        subtitle = offer.body,
        onClose = onClose,
        modifier = modifier,
        surfaceBrush = Brush.linearGradient(listOf(colors.CoinsCardTop, colors.CoinsCardMid, colors.CoinsCardBottom)),
        actions = {
            if (offer.offerFree) {
                GlassPillButton(text = CoinsStrings.playFree, onClick = onPlayFree, leadingIcon = Icons.Rounded.SportsEsports, height = YapNPlayTheme.dimens.ctaHeight)
            }
            if (offer.adReady) PillButton(
                text = when {
                    offer.watchingAd -> CoinsStrings.watchingAd
                    next != null -> CoinsStrings.watchAdFor(TextUtils.groupedThousands(next))
                    else -> CoinsStrings.watchAd
                },
                onClick = {
                    AppLog.d(AppConstants.LOG_TAG_ADS, "dialog: Watch Ad button pressed")
                    onWatchAd()
                },
                // Off while an ad plays, and during the cooldown or past today's limit (the note under it says which).
                enabled = !offer.watchingAd && offer.canWatch,
                gradient = listOf(colors.CoinsWatchStart, colors.CoinsWatchEnd),
                contentColor = colors.CoinsWatchContent,
                leadingIcon = Icons.Rounded.OndemandVideo,
                trailingIcon = null,
                textStyle = YapNPlayTheme.typography.sectionTitle,
            )
        },
    ) {
        if (offer.adReady && offer.rewards.isNotEmpty()) {
            CoinsAdProgress(watched = offer.adsWatched, total = offer.rewards.size)
            CoinsAdLadder(rewards = offer.rewards, watched = offer.adsWatched)
        }
        offer.message?.let {
            BasicText(text = it, style = YapNPlayTheme.typography.label.copy(color = colors.Gold, fontWeight = FontWeight.SemiBold))
        }
        offer.note?.takeIf { offer.adReady }?.let {
            BasicText(text = it, style = YapNPlayTheme.typography.label.copy(color = if (offer.canWatch) colors.TextSecondary else colors.Warning))
        }
    }
}

/** "Today's free coins  2/5" over a rainbow bar that fills as ads are watched. */
@Composable
private fun CoinsAdProgress(watched: Int, total: Int) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    val fill by animateFloatAsState(
        targetValue = if (total == 0) 0f else (watched.toFloat() / total).coerceIn(0f, 1f),
        animationSpec = tween(MotionTokens.STANDARD_MS),
        label = "coins-ad-progress",
    )
    val shape = RoundedCornerShape(percent = 50)
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.Xxs)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            BasicText(
                text = CoinsStrings.adProgressTitle,
                style = typography.label.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f),
            )
            BasicText(
                text = CoinsStrings.adProgress(watched, total),
                style = typography.label.copy(color = colors.CoinsLadder3, fontWeight = FontWeight.Black),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(Spacing.Md)
                .clip(shape)
                .background(colors.CoinsProgressTrack),
        ) {
            if (fill > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fill)
                        .fillMaxHeight()
                        .clip(shape)
                        .background(Brush.horizontalGradient(listOf(colors.Gold, colors.CoinsLadder3))),
                )
            }
            // A divider between each ad's stretch, so the bar reads in steps.
            val divider = colors.Background.copy(alpha = DIVIDER_ALPHA)
            val dividerWidth = YapNPlayTheme.dimens.offerDivider
            Canvas(modifier = Modifier.matchParentSize()) {
                for (step in 1 until total) {
                    val x = size.width * step / total
                    drawLine(divider, Offset(x, 0f), Offset(x, size.height), strokeWidth = dividerWidth.toPx())
                }
            }
        }
    }
}

/** One reward per ad today, as coloured text only (no block behind it): ticked once watched, the next one marked NEXT. */
@Composable
private fun CoinsAdLadder(rewards: List<Int>, watched: Int) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val palette = ladderColours()
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
        rewards.forEachIndexed { index, coins ->
            val tint = palette[index % palette.size]
            val done = index < watched
            val isNext = index == watched
            Column(
                modifier = Modifier
                    .weight(1f)
                    .alpha(if (done) DONE_ALPHA else 1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs),
            ) {
                AppIcon(
                    imageVector = if (done) Icons.Rounded.CheckCircle else Icons.Rounded.MonetizationOn,
                    tint = if (done) colors.TextSecondary else tint,
                    size = dimens.iconSmall,
                )
                BasicText(
                    text = CoinsStrings.adRewardChip(TextUtils.groupedThousands(coins)),
                    style = typography.label.copy(color = if (done) colors.TextSecondary else tint, fontWeight = FontWeight.Black, textAlign = TextAlign.Center),
                    maxLines = 1,
                )
                if (isNext) {
                    BasicText(
                        text = CoinsStrings.adNext,
                        style = typography.micro.copy(color = tint, fontWeight = FontWeight.Bold),
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

/** The ladder's five colours, cool to hot, also the progress bar's rainbow. */
@Composable
private fun ladderColours(): List<Color> {
    val colors = YapNPlayTheme.colors
    return listOf(colors.CoinsLadder1, colors.CoinsLadder2, colors.CoinsLadder3, colors.CoinsLadder4, colors.CoinsLadder5)
}

private const val DONE_ALPHA = 0.55f
private const val DIVIDER_ALPHA = 0.85f
