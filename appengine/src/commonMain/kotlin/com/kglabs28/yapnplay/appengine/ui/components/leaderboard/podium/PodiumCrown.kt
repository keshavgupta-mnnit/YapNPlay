package com.kglabs28.yapnplay.appengine.ui.components.leaderboard.podium

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.zIndex
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/** The crown over the winner: wholly above the avatar, bobbing gently. */
@Composable
fun PodiumCrown(modifier: Modifier = Modifier) {
    val dimens = YapNPlayTheme.dimens
    val bob by rememberInfiniteTransition().animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(AppConstants.STAGE_BOB_CYCLE_MS), RepeatMode.Reverse),
    )
    val fontSize = with(LocalDensity.current) { dimens.podiumCrown.toSp() }
    BasicText(
        text = CROWN,
        style = YapNPlayTheme.typography.headline.copy(fontSize = fontSize, lineHeight = fontSize),
        modifier = modifier.offset(y = -dimens.podiumCrown + dimens.boardRing * bob).zIndex(2f),
    )
}

private const val CROWN = "\uD83D\uDC51"
