package com.kglabs28.yapnplay.appengine.core.mvi

import androidx.lifecycle.ViewModel

/**
 * Parks a plain [MviViewModel] in a navigation back-stack entry's
 * ViewModelStore, so the screen keeps its state while another screen
 * sits on top, and is cleared exactly once when the entry is popped.
 */
class MviViewModelHolder(val viewModel: MviViewModel<*, *, *>) : ViewModel() {
    override fun onCleared() {
        viewModel.clear()
    }
}
