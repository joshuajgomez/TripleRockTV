package com.joshgm3z.triplerocktv.ui.common

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.withStyledAttributes
import com.joshgm3z.triplerocktv.R
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
        onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            this.hasFocus = hasFocus
            setProgressVisibility()
        }
    }

    private var hasFocus = false

    var text: String
        get() = binding.tvTitle.text.toString()
        set(value) {
            binding.tvTitle.text = value
        }

    var drawable: Drawable
        get() = binding.ivIcon.drawable
        set(value) {
            binding.ivIcon.setImageDrawable(value)
        }

    var progress: Int = 0
        set(value) {
            field = value
            setProgressVisibility()
        }

    private fun setProgressVisibility() {
        val visible = hasFocus && progress > 0
        binding.progressBar.setVisible(visible)
        binding.progressBar.progress = progress
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        binding.tvTitle.isEnabled = enabled
    }
}