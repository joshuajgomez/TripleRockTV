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

    init {
        ViewFindButtonBinding.inflate(LayoutInflater.from(context), this, true)

        isFocusable = true
        isClickable = true
        isFocusableInTouchMode = true
        setBackgroundResource(R.drawable.button_bg_color_selector)
    }
}