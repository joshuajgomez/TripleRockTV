package com.joshgm3z.triplerocktv.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.withStyledAttributes
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.databinding.ViewButtonBinding
import com.joshgm3z.triplerocktv.databinding.ViewTileButtonBinding
import com.joshgm3z.triplerocktv.util.setVisible

class TileButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val binding = ViewTileButtonBinding.inflate(
        LayoutInflater.from(context),
        this
    )

    init {
        attrs?.let {
            context.withStyledAttributes(it, R.styleable.TileButtonView) {
                getString(
                    R.styleable.TileButtonView_android_text,
                ).let { text ->
                    if (text.isNullOrEmpty()) return@let
                    binding.tvTitle.text = text
                }
                getDrawable(
                    R.styleable.TileButtonView_android_drawable,
                ).let { drawable ->
                    if (drawable == null) return@let
                    binding.ivIcon.setImageDrawable(drawable)
                }
                getInt(
                    R.styleable.TileButtonView_progress,
                    0
                ).let { progressValue ->
                    progress = progressValue
                }
            }
        }
        binding.root.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            binding.progressBar.setVisible(hasFocus && progress > 0)
        }
    }

    var text: String
        get() = binding.tvTitle.text.toString()
        set(value) {
            binding.tvTitle.text = value
        }

    var progress: Int
        get() = binding.progressBar.progress
        set(value) {
            binding.progressBar.progress = value
            binding.progressBar.setVisible(value > 0)
        }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        binding.tvTitle.isEnabled = enabled
    }
}