package com.joshgm3z.triplerocktv.ui.player.subtitle

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.databinding.ViewFindButtonBinding

class FindButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    val binding = ViewFindButtonBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        isFocusable = true
        isClickable = true
        isFocusableInTouchMode = true
        setBackgroundResource(R.color.button_bg_color_selector)
    }
}