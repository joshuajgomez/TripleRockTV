package com.joshgm3z.triplerocktv.ui.home

import android.widget.TextView
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.impl.helper.FirestoreLogger
import com.joshgm3z.triplerocktv.core.util.Logger
import com.joshgm3z.triplerocktv.util.setVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

private const val VIEW_TIMEOUT = 800L
private const val MIN_DURATION = 10
private const val MAX_DURATION = 130

class SkipFeedbackHandler(
    private val scope: CoroutineScope,
    private val tvSkipForward: TextView,
    private val tvSkipBack: TextView,
    private val firestoreLogger: FirestoreLogger
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
        if (roundedSec !in MIN_DURATION..MAX_DURATION) {
            firestoreLogger.log(
                mapOf(
                    "event" to "skip_feedback_ignored",
                    "roundedSec" to roundedSec
                )
            )
            Logger.debug("Ignored skip feedback: $roundedSec seconds")
            return
        }

        job?.cancel()
        durationCache += roundedSec

        job = scope.launch {
            showText(durationCache)
            delay(VIEW_TIMEOUT)
            durationCache = 0
            clearText()
        }
    }

    private fun showText(duration: Long) {
        val context = tvSkipForward.context
        tvSkipForward.text = context.getString(R.string.skip_forward_text, duration)
        tvSkipBack.text = context.getString(R.string.skip_back_text, duration)

        tvSkipForward.setVisible(isForward)
        tvSkipBack.setVisible(!isForward)
    }

    private fun clearText() {
        tvSkipForward.setVisible(false)
        tvSkipBack.setVisible(false)
    }
}