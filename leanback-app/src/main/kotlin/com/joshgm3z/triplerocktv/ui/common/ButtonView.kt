package com.joshgm3z.triplerocktv.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.withStyledAttributes
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.databinding.ViewButtonBinding

class ButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val binding = ViewButtonBinding.inflate(
        LayoutInflater.from(context),
        this
    )

    init {
        attrs?.let {
            context.withStyledAttributes(it, R.styleable.ButtonView) {
                getString(
                    R.styleable.ButtonView_android_text,
                ).let { text ->
                    if (text.isNullOrEmpty()) return@let
                    binding.tvTitle.text = text
                }
                getDimensionPixelSize(
                    R.styleable.ButtonView_android_textSize,
                    0
                ).let { dimen ->
                    if (dimen == 0) return@let
                    binding.tvTitle.textSize = dimen.toFloat()
                }
                getDrawable(
                    R.styleable.ButtonView_android_drawable,
                ).let { drawable ->
                    if (drawable == null) return@let
                    binding.tvTitle.setCompoundDrawablesWithIntrinsicBounds(
                        drawable,
                        null,
                        null,
                        null
                    )
                }
                getInt(
                    R.styleable.ButtonView_android_textAlignment,
                    TEXT_ALIGNMENT_CENTER
                ).let { alignment ->
                    binding.tvTitle.textAlignment = alignment
                }
                getInt(
                    R.styleable.ButtonView_type,
                    0
                ).let { type ->
                    binding.tvTitle.background = when (type) {
                        0 -> R.drawable.button_view_background_filled
                        1 -> R.drawable.button_view_background_transparent
                        else -> R.drawable.button_view_background_active
                    }.let {
                        AppCompatResources.getDrawable(context, it)
                    }
                    when (type) {
                        2 -> com.joshgm3z.triplerocktv.core.R.color.color_background
                        else -> R.color.button_fg_color_selector
                    }.let { colorRes ->
                        binding.tvTitle.setTextColor(
                            AppCompatResources.getColorStateList(context, colorRes)
                        )
                    }
                }
            }
        }
        binding.tvTitle.setOnClickListener {
            callOnClick()
        }
    }

    var text: String
        get() = binding.tvTitle.text.toString()
        set(value) {
            binding.tvTitle.text = value
        }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        binding.tvTitle.isEnabled = enabled
    }
}