package com.joshgm3z.triplerocktv.ui.player.subtitle

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.joshgm3z.triplerocktv.databinding.ViewSubtitleDownloaderBinding
import com.joshgm3z.triplerocktv.repository.SubtitleData

class SubtitleDownloaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = ViewSubtitleDownloaderBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    fun registerClickListener(onClick: () -> Unit) {
        binding.llFindButton.setOnClickListener {
            onClick()
        }
    }

    var subtitleList: List<SubtitleData>? = null
        set(value) {
            when {
                value == null -> {}
                value.isEmpty() -> {
                    binding.rvDefaultSubtitleList.visibility = GONE
                    binding.tvError.visibility = VISIBLE
                }

                else -> {
                    binding.rvDefaultSubtitleList.visibility = VISIBLE
                    binding.tvError.visibility = GONE
                }
            }
            field = value
        }
}