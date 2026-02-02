package com.joshgm3z.triplerocktv.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.repository.LoadingState
import com.joshgm3z.triplerocktv.repository.LoadingStatus
import androidx.core.content.withStyledAttributes
import com.joshgm3z.triplerocktv.databinding.LayoutProgressBarWithTextBinding

class ProgressBarWithText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = LayoutProgressBarWithTextBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    init {
        attrs?.let { it ->
            context.withStyledAttributes(it, R.styleable.ProgressBarWithText) {

                val text = getString(R.styleable.ProgressBarWithText_android_text)
                binding.tvText.text = text

                val progress = getInt(R.styleable.ProgressBarWithText_android_progress, -1)
                val isError = getBoolean(R.styleable.ProgressBarWithText_isError, false)
                val isComplete = getBoolean(R.styleable.ProgressBarWithText_isComplete, false)
                when {
                    isError -> LoadingState(0, LoadingStatus.Error)
                    isComplete -> LoadingState(100, LoadingStatus.Complete)
                    progress != -1 -> LoadingState(progress, LoadingStatus.Ongoing)
                    else -> LoadingState()
                }.let { loadingState = it }
                if (progress != -1) {
                    loadingState = LoadingState(progress, LoadingStatus.Ongoing)
                }
            }
        }
    }

    var loadingState: LoadingState = LoadingState()
        set(value) {
            when (value.status) {
                LoadingStatus.Ongoing -> R.drawable.icon_download
                LoadingStatus.Error -> R.drawable.ic_error
                LoadingStatus.Complete -> R.drawable.ic_check_circle_green
                else -> R.drawable.ic_pending
            }.let {
                binding.ivIcon.setImageResource(it)
            }
            updateProgress(value.percent)
            field = value
        }

    private fun updateProgress(progress: Int) {
        val progressDrawable = binding.llContainer.background
        progressDrawable?.let {
            it.level = progress * 100
        }
    }
}