package com.joshgm3z.triplerocktv.ui.common

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.joshgm3z.triplerocktv.R

class BackButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val iconView: ImageView
    private val textView: TextView

    init {
        isFocusable = true
        isFocusableInTouchMode = true
        LayoutInflater.from(context).inflate(R.layout.layout_back_button, this, true)
        iconView = findViewById(R.id.iv_back_arrow)
        textView = findViewById(R.id.tv_back_text)

        setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setBackgroundColor(Color.BLUE)
                textView.setTextColor(Color.WHITE)
                iconView.setColorFilter(Color.WHITE)
            } else {
                setBackgroundColor(Color.TRANSPARENT)
                textView.setTextColor(Color.BLACK)
                iconView.setColorFilter(Color.BLACK)
            }
        }
    }

    fun setText(text: String) {
        textView.text = text
    }

    fun setIcon(resId: Int) {
        iconView.setImageResource(resId)
    }
}