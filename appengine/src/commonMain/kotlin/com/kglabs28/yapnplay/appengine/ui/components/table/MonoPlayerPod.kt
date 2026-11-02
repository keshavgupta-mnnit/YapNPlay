package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.layout.BoxScope
import com.kglabs28.yapnplay.appengine.ui.components.emojigift.emojiGiftAvatarTap
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayColors
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.MonoStrings
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.emojigift.EmojiGiftLanded
import com.kglabs28.yapnplay.appengine.ui.components.emojigift.EmojiGiftLandingUi
import com.kglabs28.yapnplay.appengine.ui.components.emojigift.EmojiGiftSeatButton
import com.kglabs28.yapnplay.appengine.ui.components.emojigift.LocalEmojiGiftTable
import com.kglabs28.yapnplay.appengine.ui.components.emojigift.emojiGiftAnchor
import androidx.compose.runtime.DisposableEffect
import com.kglabs28.yapnplay.appengine.ui.components.profile.AvatarPresets
import com.kglabs28.yapnplay.appengine.ui.placeholders.PresetAvatarArtwork
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.sqrt

/**
 * One player at the card table (design): a round avatar ringed in the
 * player's colour with their level under it, and a glass pill beside it
 * carrying their name and their speaker; a place or Away tag sits under
 * it. Underneath, the fan of face-down cards they hold — every card is drawn, never a
 * number, so the table reads a hand by its size.
 *
 * On their turn the timer runs round the whole outline (avatar and pill
 * together) and shrinks as the clock runs out. While they talk, a blue
 * and pink light glows behind the card.
 */
@Composable
fun MonoPlayerPod(
    card: MonoPlayerPodUi,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    /** Hides the fan (the local player's hand is drawn along the bottom). */
    showFan: Boolean = true,
    /** The fan beside the pill instead of under it (the top row, so it stays off the table). */
    fanBeside: Boolean = false,
    /** Extra controls at the end of the pill (the local player's switches); null shows the speaker. */
    trailing: (@Composable RowScope.() -> Unit)? = null,
) {
    val dimens = YapNPlayTheme.dimens
    val avatar = if (compact) dimens.monoSeatAvatarCompact else dimens.monoSeatAvatar
    Box(modifier = modifier) {
        if (fanBeside) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
                Column(modifier = Modifier.weight(1f).heightIn(min = seatBoxHeight(avatar, dimens.seatTagSlot, dimens.zero))) {
                    MonoSeatHeaderFor(card, avatar, trailing)
                    MonoSeatLabel(card, avatar)
                }
                // The fan's place is kept even when empty, so every top seat is as wide as the others.
                BoxWithConstraints(modifier = Modifier.width(YapNPlayTheme.dimens.monoSideFanWidth)) {
                    if (showFan && card.cards > 0) {
                        MonoCardFan(count = card.cards, cardWidth = dimens.monoSeatFanCard, maxWidth = maxWidth, eliminated = card.eliminated)
                    }
                }
            }
        } else Column(
            // An opponent's seat keeps room for the header, a tag and the fan up front: it never moves, the rest
            // shifts inside. The user's own seat (no fan) reserves nothing here: its box (MonoLocalPod) does.
            modifier = if (showFan) Modifier.heightIn(min = seatBoxHeight(avatar, dimens.seatTagSlot, dimens.monoSeatFanCard * FAN_CARD_HEIGHT)) else Modifier,
        ) {
            MonoSeatHeaderFor(card, avatar, trailing)
            // The status right under the pill; the fan below it.
            MonoSeatLabel(card, avatar)
            if (showFan && card.cards > 0) {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        // Tucked up under the pill when no tag sits there, so it reads as this seat's hand.
                        .offset(y = if (card.tagLabel() == null) -avatar * FAN_TUCK else dimens.zero)
                        .padding(start = avatar * FAN_START),
                ) {
                    MonoCardFan(
                        count = card.cards,
                        cardWidth = dimens.monoSeatFanCard,
                        maxWidth = maxWidth,
                        eliminated = card.eliminated,
                    )
                }
            }
        }
        // A player on one card who called MONO wears a pulsing shield.
        if (card.calledMono) {
            MonoCalledBadge(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = avatar * CALLED_SHIFT, y = -Spacing.Xs),
            )
        }
    }
}

/** The seat header for a MONO player: the shared arena header with their speaker (or [trailing]). */
@Composable
private fun MonoSeatHeaderFor(card: MonoPlayerPodUi, avatar: Dp, trailing: (@Composable RowScope.() -> Unit)?) {
    ArenaSeatHeader(
        name = card.name,
        level = card.level,
        avatarId = card.avatarId,
        tint = card.tint,
        isTurn = card.isTurn,
        turnEndsAtMillis = card.turnEndsAtMillis,
        turnTotalMs = card.turnTotalMs,
        talking = card.talking,
        talkingMuted = card.talkingMuted,
        avatar = avatar,
        seat = card.seat,
        isMe = card.isMe,
        // The user's own seat says how many cards they hold (their hand lies along the bottom, not by the seat).
        details = if (card.isMe && card.cards > 0) {
            {
                BasicText(
                    text = MonoStrings.monoCardsLeft(card.cards),
                    style = YapNPlayTheme.typography.micro.copy(color = YapNPlayTheme.colors.TextSecondary, fontWeight = FontWeight.Bold),
                    maxLines = 1,
                )
            }
        } else {
            null
        },
        trailing = trailing ?: if (card.canSpeak) {
            {
                AppIcon(
                    imageVector = if (card.micOn) Icons.AutoMirrored.Rounded.VolumeUp else Icons.AutoMirrored.Rounded.VolumeOff,
                    tint = if (card.micOn) YapNPlayTheme.colors.TextPrimary else YapNPlayTheme.colors.IconMuted,
                    size = YapNPlayTheme.dimens.iconSmall,
                )
            }
        } else {
            null
        },
    )
}

/** A seat's place or status ("2nd", "ELIMINATED", Away), or null. */
fun MonoPlayerPodUi.tagLabel(): String? = placeLabel ?: statusLabel

/** A finishing place or Away / Autoplay, under the seat. */
@Composable
internal fun MonoSeatLabel(card: MonoPlayerPodUi, avatar: Dp) {
    ArenaSeatTagSlot(
        text = card.tagLabel(),
        tint = if (card.placeLabel != null) card.tint else YapNPlayTheme.colors.IconMuted,
        avatar = avatar,
    )
}

/**
 * A seat at any arena table (design): a round avatar ringed in the
 * player's [tint] with their [level] under it, joined to a glass pill
 * holding the name and any [details] (a balance); [trailing] controls
 * (speaker, mute) sit just outside the pill's right end, centred on its
 * height, so they never cover the name. The turn timer runs round the outside of the whole
 * outline, never over the avatar; while [talking], a blue-pink light glows
 * behind it, and while [talkingMuted] (the user talking with their mic off) a grey one. Shared by MONO, Royal Ludo and City Royale.
 *
 * At a table with emoji gifts ([LocalEmojiGiftTable]) another player's
 * [seat] wears a small smiley on the avatar's lower edge (like an online
 * dot), and the smiley or the avatar opens the emoji picker (while the user
 * has a seat to send from); an emoji sent to this seat covers the avatar for a while.
 */
@Composable
fun ArenaSeatHeader(
    name: String,
    level: String,
    avatarId: String,
    tint: Color,
    isTurn: Boolean,
    /** When the turn ends (this device's clock); null holds the ring full. */
    turnEndsAtMillis: Long?,
    turnTotalMs: Long,
    talking: Boolean,
    avatar: Dp,
    modifier: Modifier = Modifier,
    talkingMuted: Boolean = false,
    /** This seat's number at the table (emoji gifts); null: no emoji button or avatar swap. */
    seat: Int? = null,
    /** The user's own seat: where their emojis fly from, so no emoji button on it. */
    isMe: Boolean = false,
    details: (@Composable ColumnScope.() -> Unit)? = null,
    trailing: (@Composable RowScope.() -> Unit)? = null,
) {
    val gifts = LocalEmojiGiftTable.current
    if (gifts != null && seat != null) DisposableEffect(gifts, seat) { onDispose { gifts.anchors.forget(seat) } }
    // Another player, and the user has a seat to send from: the smiley on the avatar, and a tap on the avatar too.
    val canGift = gifts != null && seat != null && !isMe && gifts.anchors.mySeat != null
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val pillHeight = avatar * PILL_SHARE
    val pillShape = RoundedCornerShape(percent = 50)
    // The breathing is read while drawing only: the seat isn't recomposed every frame.
    val glow = if (talking || talkingMuted) talkPulse() else null
    val shownTime = rememberTurnFraction(turnEndsAtMillis, turnTotalMs)

    // The speaker / mute button sits outside the pill, at its right end, so the name keeps the pill's whole width.
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(avatar),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .seatTalkGlow({ glow?.value ?: 0f }, pillHeight, muted = !talking)
                .seatTimer(
                    active = isTurn,
                    fraction = { shownTime.value },
                    tint = tint,
                    pillHeight = pillHeight,
                    width = dimens.monoSeatTimer,
                ),
        ) {
            // The pill starts under the avatar's centre, so the avatar sits on its left end.
            Row(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = avatar / 2)
                    .fillMaxWidth()
                    .height(pillHeight)
                    .clip(pillShape)
                    .background(Brush.verticalGradient(listOf(colors.ArenaGlassTop, colors.ArenaGlassBottom)))
                    .border(dimens.hairline, colors.ArenaGlassRim, pillShape)
                    .padding(start = avatar / 2 + Spacing.Xs, end = Spacing.Sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    ShrinkText(
                        text = name,
                        style = typography.label.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold),
                    )
                    details?.invoke(this)
                }
            }
            ArenaSeatAvatar(
                tint = tint,
                level = level,
                avatarId = avatarId,
                size = avatar,
                landed = seat?.let { gifts?.ui?.value?.landed?.get(it) },
                modifier = Modifier.emojiGiftAnchor(gifts, seat, isMe).emojiGiftAvatarTap(gifts, seat, canGift),
                badge = if (canGift && gifts != null && seat != null) {
                    {
                        EmojiGiftSeatButton(
                            table = gifts,
                            seat = seat,
                            name = name,
                            size = avatar * EMOJI_BADGE_SHARE,
                            modifier = Modifier.align(Alignment.BottomEnd).offset(x = avatar * EMOJI_BADGE_OUT, y = avatar * EMOJI_BADGE_OUT),
                        )
                    }
                } else {
                    null
                },
            )
        }
        trailing?.invoke(this)
    }
}

/** The round avatar in the player's colour, with their level in a badge under it; an emoji that [landed] here covers the face. */
@Composable
private fun ArenaSeatAvatar(
    tint: Color,
    level: String,
    avatarId: String,
    size: Dp,
    landed: EmojiGiftLandingUi?,
    modifier: Modifier = Modifier,
    /** Worn on the avatar (the emoji smiley at its lower right); null: none. */
    badge: (@Composable BoxScope.() -> Unit)? = null,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    Box(modifier = modifier.size(size), contentAlignment = Alignment.BottomCenter) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(Brush.verticalGradient(listOf(lerp(tint, colors.Background, AVATAR_BACK_DARKEN), colors.Background)))
                .border(dimens.monoSeatRing, tint, CircleShape)
                .padding(dimens.monoSeatRing),
            contentAlignment = Alignment.Center,
        ) {
            val preset = AvatarPresets.index(avatarId)
            if (landed != null) {
                EmojiGiftLanded(landing = landed, size = size)
            } else if (preset >= 0) {
                PresetAvatarArtwork(index = preset, modifier = Modifier.fillMaxSize().clip(CircleShape))
            } else {
                AppIcon(imageVector = Icons.Rounded.Person, tint = colors.IconMuted, size = size * ICON_SHARE)
            }
        }
        if (level.isNotBlank()) {
            BasicText(
                text = level,
                style = YapNPlayTheme.typography.micro.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
                maxLines = 1,
                modifier = Modifier
                    .offset(y = Spacing.Xs)
                    .widthIn(min = size * LEVEL_BADGE_SHARE)
                    .clip(CircleShape)
                    .background(colors.Background)
                    .border(dimens.hairline, tint, CircleShape)
                    .padding(horizontal = Spacing.Xxs),
            )
        }
        badge?.invoke(this)
    }
}


/** A player, resolved for the seat. */
data class MonoPlayerPodUi(
    val seat: Int,
    val name: String,
    val avatarId: String = "",
    /** The level under the avatar ("" for a CPU). */
    val level: String,
    /** The player's colour: the avatar ring and the turn timer. */
    val tint: Color,
    val isMe: Boolean,
    val isTurn: Boolean,
    val talking: Boolean,
    val cards: Int,
    /** The user talking with their mic off: a grey glow, so they notice. */
    val talkingMuted: Boolean = false,
    /** When this seat's turn ends (this device's clock); null: not running. */
    val turnEndsAtMillis: Long? = null,
    val turnTotalMs: Long = 0,
    val placeLabel: String? = null,
    val statusLabel: String? = null,
    val canSpeak: Boolean = true,
    /** Their microphone is on (the speaker on their pill). */
    val micOn: Boolean = false,
    /** Knocked out at the 25-card threshold. */
    val eliminated: Boolean = false,
    /** Down to one card with the callout made. */
    val calledMono: Boolean = false,
    /** The user muted them, for the user only. */
    val mutedForMe: Boolean = false,
)

/**
 * The face-down cards a player holds, fanned: every card is drawn (no
 * count), overlapping more as the hand grows so it always fits [maxWidth].
 */
@Composable
fun MonoCardFan(
    count: Int,
    cardWidth: Dp,
    maxWidth: Dp,
    modifier: Modifier = Modifier,
    eliminated: Boolean = false,
) {
    if (count <= 0) return
    val step = if (count == 1) cardWidth else ((maxWidth - cardWidth) / (count - 1).toFloat()).coerceIn(cardWidth * FAN_MIN_STEP, cardWidth * FAN_MAX_STEP)
    val middle = (count - 1) / 2f
    val spread = (FAN_DEGREES / count).coerceAtMost(FAN_STEP_DEGREES)
    Box(modifier = modifier.width(step * (count - 1).toFloat() + cardWidth).alpha(if (eliminated) ELIMINATED_ALPHA else 1f)) {
        repeat(count) { index ->
            MonoCardFace(
                look = MonoCardLook(fill = YapNPlayTheme.colors.MonoBackBase, rim = YapNPlayTheme.colors.MonoBackRim),
                faceDown = true,
                modifier = Modifier
                    .offset(x = step * index.toFloat())
                    .width(cardWidth)
                    .graphicsLayer {
                        rotationZ = (index - middle) * spread
                        transformOrigin = TransformOrigin(0.5f, 1f)
                    },
            )
        }
    }
}

/** The pulsing MONO! shield worn by a player on one card. */
@Composable
fun MonoCalledBadge(modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val pulse = rememberInfiniteTransition(label = "mono-call")
    val shown by pulse.animateFloat(
        initialValue = PULSE_MIN,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(PULSE_MS), repeatMode = RepeatMode.Reverse),
        label = "mono-call-alpha",
    )
    BasicText(
        text = MonoStrings.monoCalled,
        style = YapNPlayTheme.typography.micro.copy(color = colors.TextPrimary, fontWeight = FontWeight.Black),
        modifier = modifier
            // Faded in the draw step only: the badge isn't recomposed every frame.
            .graphicsLayer { alpha = shown }
            .clip(YapNPlayTheme.shapes.pill)
            .background(colors.ArenaCallFillTop)
            .border(dimens.hairline, colors.ArenaCallNeon, YapNPlayTheme.shapes.pill)
            .padding(horizontal = Spacing.Xs, vertical = Spacing.Xxxs),
    )
}

/** 0 → 1 → 0, slowly: the talking glow breathing. */
@Composable
private fun talkPulse(): State<Float> {
    val pulse = rememberInfiniteTransition(label = "seat-talk")
    return pulse.animateFloat(
        initialValue = TALK_MIN,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(TALK_MS), repeatMode = RepeatMode.Reverse),
        label = "seat-talk-glow",
    )
}

/** The blue-pink light behind the seat while the player talks, grey when [muted] ([strength] 0 hides it). */
private fun Modifier.seatTalkGlow(strength: () -> Float, pillHeight: Dp, muted: Boolean): Modifier = drawBehind {
    val strength = strength()
    if (strength <= 0f) return@drawBehind
    val outline = seatOutline(size, pillHeight.toPx())
    val brush = Brush.horizontalGradient(
        if (muted) {
            listOf(YapNPlayColors.ArenaTalkMutedLight, YapNPlayColors.ArenaTalkMutedDark)
        } else {
            listOf(YapNPlayColors.ArenaTalkBlue, YapNPlayColors.ArenaTalkPink)
        },
    )
    // Soft layers, widest and faintest first: a glow without a blur pass.
    GLOW_LAYERS.forEach { (width, alpha) ->
        drawPath(outline, brush, alpha = alpha * strength, style = Stroke(size.height * width))
    }
}

/** The turn timer round the seat's outline: it starts at the avatar's top and shrinks clockwise. */
private fun Modifier.seatTimer(active: Boolean, fraction: () -> Float, tint: Color, pillHeight: Dp, width: Dp): Modifier = drawWithContent {
    // Drawn first, so the avatar's level badge (which hangs over the outline) stays in front of it.
    if (!active) {
        drawContent()
        return@drawWithContent
    }
    // Drawn twice as wide on the line and clipped to the outside, so only the boundary lights — never the avatar.
    val stroke = width.toPx() * 2
    val outline = seatOutline(size, pillHeight.toPx())
    val measure = PathMeasure().apply { setPath(outline, false) }
    val left = Path()
    measure.getSegment(measure.length * (1f - fraction().coerceIn(0f, 1f)), measure.length, left, true)
    clipPath(outline, clipOp = ClipOp.Difference) {
        drawPath(outline, tint.copy(alpha = TRACK_ALPHA), style = Stroke(stroke))
        drawPath(left, tint, style = Stroke(stroke, cap = StrokeCap.Round))
    }
    drawContent()
}

/**
 * The seat's silhouette as one closed line: the avatar circle on the
 * left (as tall as the seat), joined to the pill running to the right
 * edge. It starts at the circle's top and runs clockwise.
 */
private fun seatOutline(size: Size, pillHeight: Float): Path {
    val r = size.height / 2
    val half = (pillHeight / 2).coerceAtMost(r * PILL_CAP)
    val joinX = r + sqrt(r * r - half * half)
    val joinDeg = asin(half / r) * HALF / PI.toFloat()
    val top = r - half
    val bottom = r + half
    val circle = Rect(0f, 0f, r * 2, r * 2)
    return Path().apply {
        moveTo(r, 0f)
        arcTo(circle, -QUARTER, QUARTER - joinDeg, false)
        lineTo(size.width - half, top)
        arcTo(Rect(size.width - half * 2, top, size.width, bottom), -QUARTER, HALF, false)
        lineTo(joinX, bottom)
        arcTo(circle, joinDeg, HALF + QUARTER - joinDeg, false)
        close()
    }
}

private const val PILL_SHARE = SEAT_PILL_SHARE
private const val PILL_CAP = 0.95f
/** A fan card's height for its width (cards are 0.68 as wide as tall). */
private const val FAN_CARD_HEIGHT = 1.47f
/** How far the fan tucks up under the pill with no tag there (× the avatar). */
private const val FAN_TUCK = 0.1f
private const val FAN_START = 0.75f
/** How far the fan under a seat tucks up towards the pill (× avatar). */
private const val CALLED_SHIFT = 0.7f
private const val ICON_SHARE = 0.55f
private const val LEVEL_BADGE_SHARE = 0.42f
private const val AVATAR_BACK_DARKEN = 0.6f
private const val QUARTER = 90f
private const val HALF = 180f
private const val TRACK_ALPHA = 0.22f
private const val TALK_MIN = 0.45f
private const val TALK_MS = 900
private val GLOW_LAYERS = listOf(0.55f to 0.12f, 0.36f to 0.2f, 0.2f to 0.32f, 0.08f to 0.5f)
private const val FAN_DEGREES = 30f
private const val FAN_STEP_DEGREES = 6f
private const val FAN_MIN_STEP = 0.12f
private const val FAN_MAX_STEP = 0.5f
private const val ELIMINATED_ALPHA = 0.4f
private const val PULSE_MIN = 0.45f
private const val PULSE_MS = 700

/** The emoji smiley on another player's avatar, against the avatar, and how far it sits out past its edge. */
private const val EMOJI_BADGE_SHARE = 0.4f
private const val EMOJI_BADGE_OUT = 0.06f
