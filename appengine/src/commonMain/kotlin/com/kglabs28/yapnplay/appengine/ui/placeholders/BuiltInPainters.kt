package com.kglabs28.yapnplay.appengine.ui.placeholders

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.kglabs28.yapnplay.appengine.resources.Res
import com.kglabs28.yapnplay.appengine.resources.bg_game_arena
import com.kglabs28.yapnplay.appengine.resources.bg_game_table_arena
import com.kglabs28.yapnplay.appengine.resources.bg_home_friend_zone_card
import com.kglabs28.yapnplay.appengine.resources.img_mono_card_back
import com.kglabs28.yapnplay.appengine.resources.bg_home_game_zone_card
import com.kglabs28.yapnplay.appengine.resources.bg_home_main
import com.kglabs28.yapnplay.appengine.resources.bg_leader_board
import com.kglabs28.yapnplay.appengine.resources.ic_app_logo
import com.kglabs28.yapnplay.appengine.resources.ic_avatar_01
import com.kglabs28.yapnplay.appengine.resources.ic_avatar_02
import com.kglabs28.yapnplay.appengine.resources.ic_avatar_03
import com.kglabs28.yapnplay.appengine.resources.ic_avatar_04
import com.kglabs28.yapnplay.appengine.resources.ic_avatar_05
import com.kglabs28.yapnplay.appengine.resources.ic_avatar_06
import com.kglabs28.yapnplay.appengine.resources.ic_avatar_07
import com.kglabs28.yapnplay.appengine.resources.ic_avatar_08
import com.kglabs28.yapnplay.appengine.resources.ic_avatar_09
import com.kglabs28.yapnplay.appengine.resources.ic_avatar_10
import com.kglabs28.yapnplay.appengine.resources.ic_avatar_11
import com.kglabs28.yapnplay.appengine.resources.ic_avatar_12
import com.kglabs28.yapnplay.appengine.resources.ic_avatar_13
import com.kglabs28.yapnplay.appengine.resources.ic_avatar_14
import com.kglabs28.yapnplay.appengine.resources.ic_avatar_15
import com.kglabs28.yapnplay.appengine.resources.ic_avatar_16
import com.kglabs28.yapnplay.appengine.resources.ic_avatar_17
import com.kglabs28.yapnplay.appengine.resources.ic_avatar_18
import com.kglabs28.yapnplay.appengine.resources.ic_game_city_royale
import com.kglabs28.yapnplay.appengine.resources.ic_game_mono
import com.kglabs28.yapnplay.appengine.resources.ic_game_mono_chaos
import com.kglabs28.yapnplay.appengine.resources.ic_game_royal_ludo
import org.jetbrains.compose.resources.painterResource

/** The bundled picture for [image] (commonMain/composeResources/drawable). */
@Composable
fun builtInPainter(image: BuiltInImage): Painter = painterResource(
    when (image) {
        BuiltInImage.APP_LOGO -> Res.drawable.ic_app_logo
        BuiltInImage.HOME_BACKGROUND -> Res.drawable.bg_home_main
        BuiltInImage.GAME_ZONE_CARD -> Res.drawable.bg_home_game_zone_card
        BuiltInImage.FRIEND_ZONE_CARD -> Res.drawable.bg_home_friend_zone_card
        BuiltInImage.LEADERBOARD_BACKGROUND -> Res.drawable.bg_leader_board
        BuiltInImage.ROYAL_LUDO_ART -> Res.drawable.ic_game_royal_ludo
        BuiltInImage.CITY_BAZAAR_ART -> Res.drawable.ic_game_city_royale
        BuiltInImage.MONO_ART -> Res.drawable.ic_game_mono
        BuiltInImage.MONO_CHAOS_ART -> Res.drawable.ic_game_mono_chaos
        BuiltInImage.GAME_TABLE_ARENA -> Res.drawable.bg_game_table_arena
        BuiltInImage.GAME_ARENA -> Res.drawable.bg_game_arena
        BuiltInImage.MONO_CARD_BACK -> Res.drawable.img_mono_card_back
    },
)

/** Preset avatar number [index] (0-based, see AvatarPresets). */
@Composable
fun avatarPainter(index: Int): Painter = painterResource(avatarDrawables[index % avatarDrawables.size])

private val avatarDrawables = listOf(
    Res.drawable.ic_avatar_01, Res.drawable.ic_avatar_02, Res.drawable.ic_avatar_03,
    Res.drawable.ic_avatar_04, Res.drawable.ic_avatar_05, Res.drawable.ic_avatar_06,
    Res.drawable.ic_avatar_07, Res.drawable.ic_avatar_08, Res.drawable.ic_avatar_09,
    Res.drawable.ic_avatar_10, Res.drawable.ic_avatar_11, Res.drawable.ic_avatar_12,
    Res.drawable.ic_avatar_13, Res.drawable.ic_avatar_14, Res.drawable.ic_avatar_15,
    Res.drawable.ic_avatar_16, Res.drawable.ic_avatar_17, Res.drawable.ic_avatar_18,
)
