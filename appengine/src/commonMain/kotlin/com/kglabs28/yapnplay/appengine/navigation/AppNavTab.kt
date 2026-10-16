package com.kglabs28.yapnplay.appengine.navigation

/**
 * Dashboard tabs. Home, Games and Rooms share one scaffold and top bar;
 * everything else navigates outside the scaffold.
 *
 * [frostedBackground]: the tab asks the scaffold to blur and darken the
 * whole background art (behind the top bar too), so busy screens like
 * the Game Zone read as one glass surface. Tabs whose own cards are
 * glass keep the art clear.
 */
enum class AppNavTab(val frostedBackground: Boolean) {
    HOME(frostedBackground = false),
    GAMES(frostedBackground = true),
    ROOMS(frostedBackground = false),
}
