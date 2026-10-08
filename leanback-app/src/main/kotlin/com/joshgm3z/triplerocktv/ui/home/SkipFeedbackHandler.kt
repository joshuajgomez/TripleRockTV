package com.joshgm3z.triplerocktv.ui.home

import android.widget.TextView
import com.joshgm3z.triplerocktv.core.util.Logger
import com.joshgm3z.triplerocktv.util.setVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

class SkipFeedbackHandler(
    private val scope: CoroutineScope,
    private val tvSkipForward: TextView,
    private val tvSkipBack: TextView,
) {
    private var job: Job? = null
    private var durationCache = 0L
    private var isForward = true

    fun accumulateSkip(diff: Long) {
        // Reset cache if direction changes
        val currentIsForward = diff > 0
        if (isForward != currentIsForward) {
            durationCache = 0
        }
        isForward = currentIsForward

        // Calculate rounded seconds (e.g., 10, 20, 30...)
        val roundedSec = ((abs(diff) / 1000 + 5) / 10) * 10
        if (roundedSec !in 10..130) return

        job?.cancel()
        durationCache += roundedSec

        job = scope.launch {
            showText(durationCache)
            delay(800L)
            durationCache = 0
            clearText()
        }
    }

    private fun showText(duration: Long) {
        tvSkipForward.text = "+${duration}s"
        tvSkipBack.text = "-${duration}s"

        tvSkipForward.setVisible(isForward)
        tvSkipBack.setVisible(!isForward)
    }

    private fun clearText() {
        tvSkipForward.setVisible(false)
        tvSkipBack.setVisible(false)
    }
}