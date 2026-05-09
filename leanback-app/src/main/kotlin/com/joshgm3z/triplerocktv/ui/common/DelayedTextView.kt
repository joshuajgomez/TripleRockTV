package com.joshgm3z.triplerocktv.ui.common

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.AppCompatTextView
import com.joshgm3z.triplerocktv.R

class DelayedTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        setBackgroundResource(R.drawable.placeholder_bg)
        setPadding(0, 0, 0, 0)
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.DelayedTextView)
            val tintColor = typedArray.getColor(
                R.styleable.DelayedTextView_color,
                context.getColor(com.joshgm3z.triplerocktv.core.R.color.color_card_content)
            )
            if (tintColor != 0) {
                backgroundTintList = ColorStateList.valueOf(tintColor)
                setTextColor(tintColor)
            }

            typedArray.recycle()
        }

        val animation = AnimationUtils.loadAnimation(context, R.anim.placeholder_fade_in)
        startAnimation(animation)
    }

    var text: String? = null
        set(value) {
            if (value != null) {
                background = null
                clearAnimation()
            }
            setText(value)
        }
}