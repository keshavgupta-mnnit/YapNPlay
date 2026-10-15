package com.kglabs28.yapnplay.appengine.core.mvi

/**
 * MVI markers. One immutable [UiState] per screen, user actions as
 * [UiIntent], one-way data flow. No library types above the data layer.
 */
interface UiState
interface UiIntent

/**
 * One-off occurrences (navigation, snackbars). Delivered exactly once
 * through a channel — never stored in state.
 */
interface UiEffect
