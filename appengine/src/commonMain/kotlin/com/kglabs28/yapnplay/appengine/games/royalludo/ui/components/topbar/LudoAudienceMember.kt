package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.topbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.players.LudoAvatar
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.players.tint
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoAvatarUi

/** One person in the top bar strip: their avatar (ring purple while talking, red with the mic off) with their name under it. */
@Composable
fun LudoAudienceMember(
    person: LudoAvatarUi,
    modifier: Modifier = Modifier,
) {
    val dimens = YapNPlayTheme.dimens
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        LudoAvatar(
            ring = person.ring.tint(),
            size = dimens.ludoAudienceAvatar,
            ringWidth = dimens.ludoAudienceRing,
        )
        BasicText(
            text = person.name,
            style = YapNPlayTheme.typography.micro.copy(color = YapNPlayTheme.colors.TextSecondary),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = dimens.ludoAudienceNameWidth),
        )
    }
}
