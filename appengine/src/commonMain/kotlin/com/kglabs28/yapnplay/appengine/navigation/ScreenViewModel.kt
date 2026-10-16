package com.kglabs28.yapnplay.appengine.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.NavBackStackEntry
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModel
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModelHolder
import org.koin.compose.getKoin
import kotlin.reflect.KClass

/**
 * The view model for a screen, scoped to its back-stack entry: created
 * by Koin the first time, then the SAME instance every time the screen
 * comes back (e.g. after Back from a room), cleared when popped.
 * Use this in the nav host instead of koinInject() for screen VMs.
 */
@Composable
inline fun <reified VM : MviViewModel<*, *, *>> NavBackStackEntry.rememberScreenViewModel(): VM {
    val koin = getKoin()
    return remember(this) {
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T =
                MviViewModelHolder(koin.get<VM>()) as T
        }
        val holder = ViewModelProvider.create(this, factory)[
            VM::class.qualifiedName.orEmpty(),
            MviViewModelHolder::class,
        ]
        holder.viewModel as VM
    }
}
