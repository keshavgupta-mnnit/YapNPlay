package com.kglabs28.yapnplay.appengine.core.common

/**
 * Index math for looping lists (the game carousel): a long strip of
 * pages repeats a short list, so page N shows item `N mod size`.
 */
object LoopIndex {

    /** The item shown on [page] of a strip repeating [size] items. */
    fun itemAt(page: Int, size: Int): Int = if (size == 0) 0 else page.mod(size)

    /** A page near the middle of a [pageCount] strip that shows [item]. */
    fun middlePageFor(item: Int, size: Int, pageCount: Int): Int {
        if (size == 0) return 0
        val middle = pageCount / 2
        return middle - middle.mod(size) + item
    }

    /**
     * Fewest pages to move from [page] so it shows [target], going either
     * way round the loop (e.g. from the first game left to the last).
     */
    fun stepsTo(page: Int, target: Int, size: Int): Int {
        if (size == 0) return 0
        val forward = (target - itemAt(page, size)).mod(size)
        return if (forward <= size / 2) forward else forward - size
    }
}
