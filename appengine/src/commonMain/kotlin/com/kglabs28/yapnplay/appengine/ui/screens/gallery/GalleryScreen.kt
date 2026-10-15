package com.kglabs28.yapnplay.appengine.ui.screens.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.kglabs28.yapnplay.appengine.core.designsystem.LayoutTier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.AppDialog
import com.kglabs28.yapnplay.appengine.ui.components.AppIconButton
import com.kglabs28.yapnplay.appengine.ui.components.AppIconGlyph
import com.kglabs28.yapnplay.appengine.ui.components.AppSnackbar
import com.kglabs28.yapnplay.appengine.ui.components.AppTextField
import com.kglabs28.yapnplay.appengine.ui.components.CountdownRing
import com.kglabs28.yapnplay.appengine.ui.components.DangerTextButton
import com.kglabs28.yapnplay.appengine.ui.components.DiceView
import com.kglabs28.yapnplay.appengine.ui.components.LargeVoiceToggle
import com.kglabs28.yapnplay.appengine.ui.components.MemberChip
import com.kglabs28.yapnplay.appengine.ui.components.PresenceDot
import com.kglabs28.yapnplay.appengine.ui.components.PrimaryButton
import com.kglabs28.yapnplay.appengine.ui.components.ReconnectBanner
import com.kglabs28.yapnplay.appengine.ui.components.RoomCodeDisplay
import com.kglabs28.yapnplay.appengine.ui.components.RoomCodeInput
import com.kglabs28.yapnplay.appengine.ui.components.SeatCard
import com.kglabs28.yapnplay.appengine.ui.components.SecondaryButton
import com.kglabs28.yapnplay.appengine.ui.components.SpeakingRing
import com.kglabs28.yapnplay.appengine.ui.components.StatusPill
import com.kglabs28.yapnplay.appengine.ui.components.TextActionButton
import com.kglabs28.yapnplay.appengine.ui.components.ToggleIconButton
import org.koin.compose.koinInject

/**
 * Debug-only component gallery: every component in every state, with a
 * tier switcher that previews S/M/L layouts. Visual check target.
 * Single-letter glyphs stand in for art assets landing later.
 */
@Composable
fun GalleryScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GalleryViewModel = koinInject(),
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            if (effect is GalleryScreenEffect.Back) onBack()
        }
    }
    YapNPlayTheme(tierOverride = state.tierOverride) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(YapNPlayTheme.colors.Background)
                .verticalScroll(rememberScrollState())
                .padding(Spacing.Lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.Lg),
        ) {
            GalleryHeader(
                activeTier = state.tierOverride,
                onSelectTier = { viewModel.onIntent(GalleryScreenIntent.ForceTier(it)) },
                onShowAll = { viewModel.onIntent(GalleryScreenIntent.ShowAllTiers) },
            )
            Section(title = Strings.homeQuickPlay) {
                PrimaryButton(text = Strings.homeQuickPlay, onClick = {})
                PrimaryButton(text = Strings.galleryDisabled, onClick = {}, enabled = false)
                SecondaryButton(text = Strings.homeHostLobby, onClick = {})
                SecondaryButton(text = Strings.galleryDisabled, onClick = {}, enabled = false)
                TextActionButton(text = Strings.actionInviteFriends, onClick = {})
                DangerTextButton(text = Strings.actionLeaveRoom, onClick = {})
            }
            Section(title = Strings.actionSettings) {
                ToggleIconButton(selected = true, onToggle = {}) { AppIconGlyph(glyph = "M") }
                ToggleIconButton(selected = false, onToggle = {}) { AppIconGlyph(glyph = "S") }
                LargeVoiceToggle(selected = false, onToggle = {}) { AppIconGlyph(glyph = "M") }
                AppIconButton(onClick = {}) { AppIconGlyph(glyph = "⚙") }
            }
            Section(title = Strings.hintDisplayName) {
                AppTextField(value = "", onValueChange = {}, hint = Strings.hintDisplayName)
                AppTextField(value = Strings.gallerySampleName, onValueChange = {}, isError = true)
                RoomCodeDisplay(code = Strings.gallerySampleCode)
                RoomCodeInput(code = "", onCodeChange = {}, hint = Strings.hintRoomCode)
            }
            Section(title = Strings.galleryStatusReady) {
                StatusPill(text = Strings.galleryStatusReady, color = YapNPlayTheme.colors.Success)
                StatusPill(text = Strings.galleryStatusNotReady, color = YapNPlayTheme.colors.TextDisabled)
                PresenceDot(online = true)
                PresenceDot(online = false)
                MemberChip(text = Strings.gallerySampleMember)
                MemberChip(text = Strings.galleryLongMemberName)
                CountdownRing(progress = 0.65f)
                ReconnectBanner(message = Strings.galleryReconnecting)
                AppSnackbar(message = Strings.galleryCopied)
            }
            Section(title = Strings.actionRollDice) {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.Md)) {
                    DiceView(value = 1)
                    DiceView(value = 6)
                    SeatCard(seatColor = YapNPlayTheme.colors.PlayerRed) {}
                    SeatCard(seatColor = YapNPlayTheme.colors.PlayerBlue, active = false) {}
                }
                SpeakingRing(speaking = true) {
                    AppIconButton(onClick = {}) { AppIconGlyph(glyph = "V") }
                }
            }
            Section(title = Strings.galleryLeaveTitle) {
                AppDialog(
                    title = Strings.galleryLeaveTitle,
                    body = Strings.galleryLeaveBody,
                    onDismiss = {},
                    confirmLabel = Strings.galleryLeaveConfirm,
                    onConfirm = {},
                    dismissLabel = Strings.galleryLeaveDismiss,
                )
            }
        }
    }
}

@Composable
private fun GalleryHeader(
    activeTier: LayoutTier?,
    onSelectTier: (LayoutTier) -> Unit,
    onShowAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Spacing.Md)) {
        BasicText(
            text = Strings.galleryTitle,
            style = YapNPlayTheme.typography.headline.copy(color = YapNPlayTheme.colors.TextPrimary),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
            TierButton(label = Strings.galleryShowAll, selected = activeTier == null, onClick = onShowAll)
            for (tier in LayoutTier.entries) {
                TierButton(label = tier.name, selected = activeTier == tier, onClick = { onSelectTier(tier) })
            }
        }
    }
}

@Composable
private fun TierButton(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    if (selected) {
        PrimaryButton(text = label, onClick = onClick, modifier = modifier)
    } else {
        SecondaryButton(text = label, onClick = onClick, modifier = modifier)
    }
}

@Composable
private fun Section(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.Md),
        horizontalAlignment = Alignment.Start,
    ) {
        BasicText(
            text = title,
            style = YapNPlayTheme.typography.title.copy(color = YapNPlayTheme.colors.TextSecondary),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        content()
    }
}
