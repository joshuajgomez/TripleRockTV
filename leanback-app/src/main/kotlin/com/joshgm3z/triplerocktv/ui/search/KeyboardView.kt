package com.joshgm3z.triplerocktv.ui.search

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.joshgm3z.triplerocktv.databinding.ViewKeyboardBinding

class KeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = ViewKeyboardBinding.inflate(
        LayoutInflater.from(context),
        this, true
    )
}

