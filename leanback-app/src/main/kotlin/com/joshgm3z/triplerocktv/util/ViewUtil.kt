package com.joshgm3z.triplerocktv.util

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.joshgm3z.triplerocktv.BuildConfig
import com.joshgm3z.triplerocktv.core.util.isDevBuild
import kotlinx.coroutines.launch

fun View.setVisible(visible: Boolean?) {
    visibility = if (visible == true) View.VISIBLE else View.GONE
}

fun String.orIfDebug(secretText: String) = if (isDevBuild) secretText else this

fun Fragment.fragmentScope(block: suspend () -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            block()
        }
    }
}