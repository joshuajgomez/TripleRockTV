package com.joshgm3z.triplerocktv.ui.common

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.databinding.ViewDelayedTextBinding
import com.joshgm3z.triplerocktv.util.setVisible
import androidx.core.content.withStyledAttributes
import com.joshgm3z.triplerocktv.core.util.Logger

class DelayedTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = ViewDelayedTextBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    init {
        attrs?.let {
            context.withStyledAttributes(it, R.styleable.DelayedTextView) {
                getColor(
                    R.styleable.DelayedTextView_color,
                    0
                ).let { tintColor ->
                    if (tintColor == 0) return@let
                    binding.placeholder.placeHolderTint = tintColor
                    binding.tvText.setTextColor(tintColor)
                }
                getString(R.styleable.DelayedTextView_text).let { defaultText ->
                    text = defaultText
                }
                getDimensionPixelSize(
                    R.styleable.DelayedTextView_placeholderWidth,
                    0
                ).let { dimen ->
                    if (dimen == 0) return@let
                    binding.placeholder.layoutParams.width = dimen
                }
                getDimensionPixelSize(
                    R.styleable.DelayedTextView_placeholderHeight,
                    0
                ).let { dimen ->
                    if (dimen == 0) return@let
                    binding.placeholder.layoutParams.height = dimen
                }
            }
        }
    }

    var text: String? = null
        set(value) {
            binding.placeholder.setVisible(value == null)
            binding.tvText.text = value
            field = value
        }
}