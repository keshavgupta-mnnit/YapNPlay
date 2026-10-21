package com.kglabs28.yapnplay.appengine.ui.components.leaderboard

import androidx.compose.ui.zIndex
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalDensity
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.core.designsystem.AppBrushes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.common.glassScrollbar
import com.kglabs28.yapnplay.appengine.ui.components.common.rememberImageShare
import com.kglabs28.yapnplay.appengine.ui.components.home.HomeCenterLogo
import com.kglabs28.yapnplay.appengine.ui.placeholders.LeaderboardArtwork
import kotlinx.coroutines.launch

/**
 * The end of a match, as in the design: the celebration art fills the
 * screen; the app logo top left; the podium (2nd, 1st with a crown, 3rd);
 * the LEADERBOARD panel with 4th place onwards on the right; a bottom bar
 * with the game's art, SHARE THIS (a screenshot of this screen plus the
 * app link), match duration and EXP, and PLAY AGAIN.
 */
@Composable
fun MatchLeaderboard(board: LeaderboardUi, gameArt: @Composable (Modifier) -> Unit, onPlayAgain: () -> Unit, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val podium = board.entries.take(PODIUM)
    val rest = board.entries.drop(PODIUM)
    val layer = rememberGraphicsLayer()
    val scope = rememberCoroutineScope()
    val share = rememberImageShare()
    val shareText = TableStrings.leaderboardShareWithLink(TableStrings.leaderboardShareText(board.game, board.myPlace), board.shareLink)
    Box(
        modifier = modifier
            .fillMaxSize()
            // Recorded so SHARE THIS can send this very screen as a picture.
            .drawWithContent {
                layer.record { this@drawWithContent.drawContent() }
                drawLayer(layer)
            },
    ) {
        LeaderboardArtwork(modifier = Modifier.fillMaxSize())
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(colors.Background.copy(alpha = SHADE_TOP), colors.Background.copy(alpha = SHADE_BOTTOM)))))
        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.weight(1f).fillMaxWidth().padding(start = Spacing.Md, end = Spacing.Md, top = Spacing.Xs)) {
                // Logo, then the podium.
                Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    HomeCenterLogo(logoWidth = dimens.boardLogoWidth, logoHeight = dimens.boardLogoLogoHeight)
                    // Starts right of the game logo, which rises out of the bottom bar.
                    LeaderboardPodium(podium = podium, modifier = Modifier.weight(1f).fillMaxWidth().padding(start = dimens.boardGameLogo + Spacing.Sm, bottom = Spacing.Xs))
                }
                // LEADERBOARD panel: 4th onwards.
                if (rest.isNotEmpty()) {
                    val panelShape = RoundedCornerShape(dimens.cardRadius)
                    Column(
                        modifier = Modifier
                            .width(dimens.boardPanelWidth)
                            .fillMaxHeight()
                            .padding(vertical = Spacing.Sm)
                            .background(Color.Black.copy(alpha = PANEL_DARK), panelShape)
                            .background(Brush.verticalGradient(listOf(Color.White.copy(alpha = PANEL_TOP), Color.White.copy(alpha = PANEL_BOTTOM))), panelShape)
                            .border(dimens.hairline, Color.White.copy(alpha = PANEL_RIM), panelShape)
                            .padding(Spacing.Sm),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
                    ) {
                        BasicText(text = TableStrings.leaderboardTitle, style = typography.title.copy(color = Color.White, fontWeight = FontWeight.Black))
                        val scroll = rememberScrollState()
                        Column(
                            modifier = Modifier.weight(1f).fillMaxWidth().glassScrollbar(scroll).verticalScroll(scroll).padding(end = dimens.scrollbarWidth * 2),
                            verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
                        ) {
                            rest.forEach { LeaderboardRow(entry = it) }
                        }
                    }
                }
            }
            // Bottom bar.
            Box(modifier = Modifier.fillMaxWidth().height(dimens.boardBottomBar).padding(horizontal = Spacing.Xs)) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(AppBrushes.leaderboardBar, RoundedCornerShape(topStart = dimens.cardRadius, topEnd = dimens.cardRadius))
                        .border(dimens.hairline, Color.White.copy(alpha = PANEL_RIM), RoundedCornerShape(topStart = dimens.cardRadius, topEnd = dimens.cardRadius)),
                )
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = Spacing.Lg),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.Md),
                ) {
                    // The game's logo, big and rising out of the bar, its name underneath (as in the room).
                    Column(
                        // Nudged to the bar's left edge; the name tucks up under the icon so they read as one.
                        modifier = Modifier.wrapContentHeight(align = Alignment.Bottom, unbounded = true).offset(x = -Spacing.Md).padding(bottom = Spacing.Xxs),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(-Spacing.Sm),
                    ) {
                        gameArt(Modifier.size(dimens.boardGameLogo))
                        GameNameTitle(name = board.game, modifier = Modifier.zIndex(1f))
                    }
                    // SHARE THIS: a picture of this screen with the app link; glowing green.
                    Row(
                        modifier = Modifier
                            .width(dimens.boardButtonWidth)
                            .height(dimens.neonButtonHeight)
                            .shadow(elevation = dimens.neonButtonGlow, shape = CircleShape, ambientColor = colors.LiveGreen, spotColor = colors.LiveGreen)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = SHARE_FILL))
                            .border(dimens.promptRim, colors.LiveGreen, CircleShape)
                            .clickable(role = Role.Button) { scope.launch { share(layer.toImageBitmap(), shareText) } },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs, Alignment.CenterHorizontally),
                    ) {
                        AppIcon(imageVector = Icons.Rounded.Share, tint = colors.LiveGreen, size = dimens.iconMedium)
                        BasicText(text = TableStrings.leaderboardShare, style = typography.label.copy(color = colors.LiveGreen, fontWeight = FontWeight.Black))
                    }
                    Box(Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.End) {
                        BasicText(
                            text = buildAnnotatedString {
                                append(TableStrings.leaderboardMatchDuration)
                                withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Bold)) { append(board.duration) }
                            },
                            style = typography.label.copy(color = colors.TextSecondary),
                        )
                        if (board.myExp.isNotEmpty()) {
                            BasicText(
                                text = buildAnnotatedString {
                                    append(TableStrings.leaderboardExpGained)
                                    withStyle(SpanStyle(color = colors.LiveGreen, fontWeight = FontWeight.Bold)) { append(board.myExp) }
                                },
                                style = typography.label.copy(color = colors.TextSecondary),
                            )
                        }
                    }
                    Box(Modifier.width(dimens.hairline).height(dimens.neonButtonHeight).background(Color.White.copy(alpha = PANEL_RIM)))
                    // PLAY AGAIN.
                    Row(
                        modifier = Modifier
                            .width(dimens.boardButtonWidth)
                            .height(dimens.neonButtonHeight)
                            .shadow(elevation = dimens.neonButtonGlow, shape = CircleShape, ambientColor = colors.LiveGreen, spotColor = colors.LiveGreen)
                            .clip(CircleShape)
                            .background(Brush.horizontalGradient(listOf(colors.LiveGreen, colors.Cyan)))
                            .clickable(role = Role.Button, onClick = onPlayAgain),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs, Alignment.CenterHorizontally),
                    ) {
                        BasicText(text = TableStrings.leaderboardPlayAgain, style = typography.label.copy(color = colors.TextOnPlayAgainButton, fontWeight = FontWeight.Black))
                        AppIcon(imageVector = Icons.Rounded.ChevronRight, tint = colors.TextOnPlayAgainButton, size = dimens.iconMedium)
                    }
                }
            }
        }
    }
}

private const val PODIUM = 3
private const val SHADE_TOP = 0.15f
private const val SHADE_BOTTOM = 0.45f
private const val PANEL_DARK = 0.78f
private const val PANEL_TOP = 0.10f
private const val PANEL_BOTTOM = 0.05f
private const val PANEL_RIM = 0.18f
private const val SHARE_FILL = 0.45f

/** The game's name: white over a glowing accent outline, like the room's game title. */
@Composable
private fun GameNameTitle(name: String, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val style = YapNPlayTheme.typography.title
    val outlinePx = with(LocalDensity.current) { YapNPlayTheme.dimens.titleOutline.toPx() }
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        BasicText(
            text = name,
            style = style.copy(color = colors.StageAccent, fontWeight = FontWeight.Black, drawStyle = Stroke(width = outlinePx), shadow = Shadow(color = colors.StageAccent, blurRadius = style.fontSize.value)),
            maxLines = 1,
        )
        BasicText(text = name, style = style.copy(color = colors.TextPrimary, fontWeight = FontWeight.Black), maxLines = 1)
    }
}
