package com.joshgm3z.triplerocktv.ui.common

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.databinding.ViewPlaceholderBinding

class PlaceholderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    val binding = ViewPlaceholderBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.PlaceholderView)
            val tintColor = typedArray.getColor(R.styleable.PlaceholderView_placeholderTint, 0)

            if (tintColor != 0) {
                binding.root.backgroundTintList = ColorStateList.valueOf(tintColor)
            }

            typedArray.recycle()
        }

        val animation = AnimationUtils.loadAnimation(context, R.anim.placeholder_fade_in)
        binding.root.startAnimation(animation)
    }
}