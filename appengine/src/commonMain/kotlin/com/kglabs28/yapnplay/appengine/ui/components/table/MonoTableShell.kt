package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * The MONO table: the shared skeleton both MONO games draw into.
 *
 *  - A top bar: the game's name and room on the left, the spectator
 *    strip in the middle, the turn countdown, the settings gear and the
 *    exit button on the right.
 *  - The opponents in a U around the arena: a left column, a top row and
 *    a right column, in turn order, so the table reads clockwise.
 *  - The centre arena, which the game fills with its two piles.
 *  - A bottom row: the local player's pod, the MONO! callout button, the
 *    hand, and the draw button.
 *
 * The shell owns the layout only; the game owns what goes in it.
 */
@Composable
fun MonoTableShell(
    title: String,
    subtitle: String,
    audience: List<TableAvatarUi>,
    showAudience: Boolean,
    spectatorCount: String,
    spectatorLabel: String,
    elapsed: String,
    turnLabel: String?,
    turnIsMine: Boolean,
    onSettings: () -> Unit,
    onExit: () -> Unit,
    leftPods: List<MonoPlayerPodUi>,
    topPods: List<MonoPlayerPodUi>,
    rightPods: List<MonoPlayerPodUi>,
    modifier: Modifier = Modifier,
    arena: @Composable () -> Unit,
    localPod: @Composable () -> Unit,
    callButton: @Composable () -> Unit,
    hand: @Composable () -> Unit,
    drawButton: @Composable () -> Unit,
) {
    val dimens = YapNPlayTheme.dimens

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.Md, vertical = Spacing.Xs),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        TableTopBar(
            title = title,
            subtitle = subtitle,
            audience = audience,
            showAudience = showAudience,
            spectatorCount = spectatorCount,
            spectatorLabel = spectatorLabel,
            elapsed = elapsed,
            turnLabel = turnLabel,
            turnIsMine = turnIsMine,
            onSettings = onSettings,
            onExit = onExit,
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
        ) {
            // The left column of the U.
            Column(
                modifier = Modifier.width(dimens.monoPodWidthCompact),
                verticalArrangement = Arrangement.spacedBy(Spacing.Xs, Alignment.CenterVertically),
            ) {
                leftPods.forEach { pod ->
                    MonoPlayerPod(card = pod, compact = true, modifier = Modifier.fillMaxWidth())
                }
            }

            // The top row of the U, and the arena beneath it.
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.Xs, Alignment.CenterHorizontally),
                ) {
                    topPods.forEach { pod ->
                        MonoPlayerPod(
                            card = pod,
                            modifier = Modifier.width(dimens.monoPodWidth),
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    arena()
                }
            }

            // The right column of the U.
            Column(
                modifier = Modifier.width(dimens.monoPodWidthCompact),
                verticalArrangement = Arrangement.spacedBy(Spacing.Xs, Alignment.CenterVertically),
            ) {
                rightPods.forEach { pod ->
                    MonoPlayerPod(card = pod, compact = true, modifier = Modifier.fillMaxWidth())
                }
            }
        }

        // The local player's row: pod, callout, hand, draw.
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
        ) {
            localPod()
            callButton()
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.BottomCenter) { hand() }
            // A fixed width, so the button can never squeeze the hand out.
            Box(modifier = Modifier.width(dimens.monoDrawWidth)) { drawButton() }
        }
    }
}

/**
 * Splits the opponents across the three rails of the U — left column, top
 * row, right column — in turn order, so the table reads clockwise. Seven
 * opponents fill it exactly: three left, two top, two right.
 */
fun <T> monoTableZones(opponents: List<T>): Triple<List<T>, List<T>, List<T>> {
    val leftCount = (opponents.size + 2) / 3
    val topCount = (opponents.size + 1) / 3
    val rightCount = opponents.size - leftCount - topCount
    val left = opponents.take(leftCount)
    val top = opponents.drop(leftCount).take(topCount)
    val right = opponents.drop(leftCount + topCount).take(rightCount)
    return Triple(left, top, right)
}
