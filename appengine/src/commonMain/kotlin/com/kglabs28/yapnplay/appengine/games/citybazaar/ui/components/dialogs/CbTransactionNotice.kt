package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs

import androidx.compose.material.icons.rounded.MoneyOff
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Hotel
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.Redeem
import androidx.compose.material.icons.rounded.QuestionMark
import androidx.compose.ui.graphics.vector.ImageVector
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbNoticeArt
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.CurrencyRupee
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbTransactionUi
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.profile.PresetAvatar

/**
 * A money transfer as in the design: what it is, "X pays Y ₹…", the payer
 * (avatar, or the bank) → the receiver with the amount flying across, a bar
 * filling while it's deducted, then the balance before, the change and after.
 * Goes away by itself.
 */
@Composable
fun CbTransactionNotice(tx: CbTransactionUi, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val progress = remember(tx.id) { Animatable(0f) }
    LaunchedEffect(tx.id) { progress.animateTo(1f, tween(AppConstants.CB_RENT_NOTICE_MS.toInt(), easing = LinearEasing)) }
    val accent = when (tx.art) {
        CbNoticeArt.MONEY -> if (tx.gain) colors.CbGain else colors.Gold
        CbNoticeArt.LUCK -> colors.Gold
        CbNoticeArt.CHEST -> colors.Error
        CbNoticeArt.JAIL, CbNoticeArt.BANKRUPT -> colors.CbLoss
        CbNoticeArt.REST, CbNoticeArt.FREED -> colors.Cyan
    }
    CbPromptFrame(
        title = buildAnnotatedString { append(tx.title) },
        accent = accent,
        width = dimens.promptNoticeWidth,
        modifier = modifier,
    ) {
        BasicText(text = tx.line, style = typography.label.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center), maxLines = 2)
        val amount = tx.amount
        if (amount == null) {
            // No money: the player and a picture of what happened.
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Lg)) {
                Party(avatarId = tx.fromAvatarId, size = dimens.promptAvatar)
                AppIcon(imageVector = tx.art.icon(), tint = accent, size = dimens.promptArtIcon)
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Md)) {
                Party(avatarId = tx.fromAvatarId, size = dimens.promptAvatar)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AppIcon(imageVector = Icons.Rounded.CurrencyRupee, tint = colors.SocialZoneAccent, size = dimens.iconMedium)
                    BasicText(text = amount, style = typography.label.copy(color = if (tx.gain) colors.CbGain else colors.CbLoss, fontWeight = FontWeight.Bold))
                    AppIcon(imageVector = Icons.AutoMirrored.Rounded.ArrowForward, tint = colors.TextSecondary, size = dimens.iconMedium)
                }
                Party(avatarId = tx.toAvatarId, size = dimens.promptAvatarSmall)
            }
            Box(Modifier.fillMaxWidth(BAR_WIDTH).height(dimens.promptProgressHeight).clip(CircleShape).background(colors.Background.copy(alpha = TRACK_ALPHA))) {
                Box(Modifier.fillMaxHeight().fillMaxWidth(progress.value).background(Brush.horizontalGradient(listOf(colors.SocialZoneAccent, colors.Cyan)), CircleShape))
            }
            CbBalanceStrip(cash = tx.cash, changeLabel = tx.changeLabel, change = tx.change, remaining = tx.remaining, changeColor = if (tx.gain) colors.CbGain else colors.CbLoss)
        }
    }
}

/** A player's avatar, or the bank (null) as a gold bank icon in a dark circle. */
@Composable
private fun Party(avatarId: String?, size: Dp) {
    val colors = YapNPlayTheme.colors
    if (avatarId != null) {
        PresetAvatar(avatarId = avatarId, size = size)
    } else {
        Box(
            modifier = Modifier.size(size).clip(CircleShape).background(colors.Background).border(YapNPlayTheme.dimens.borderThin, colors.Gold, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            AppIcon(imageVector = Icons.Rounded.AccountBalance, tint = colors.Gold, size = size / 2)
        }
    }
}

private const val BAR_WIDTH = 0.85f
private const val TRACK_ALPHA = 0.6f

/** The picture for a popup without money. */
private fun CbNoticeArt.icon(): ImageVector = when (this) {
    CbNoticeArt.MONEY -> Icons.Rounded.CurrencyRupee
    CbNoticeArt.LUCK -> Icons.Rounded.QuestionMark
    CbNoticeArt.CHEST -> Icons.Rounded.Redeem
    CbNoticeArt.JAIL -> Icons.Rounded.Gavel
    CbNoticeArt.REST -> Icons.Rounded.Hotel
    CbNoticeArt.FREED -> Icons.Rounded.LockOpen
    CbNoticeArt.BANKRUPT -> Icons.Rounded.MoneyOff
}
