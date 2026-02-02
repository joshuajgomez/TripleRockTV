package com.joshgm3z.triplerocktv.ui.common

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.joshgm3z.triplerocktv.R

class DefaultButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val iconView: ImageView
    private val textView: TextView

    init {
        isFocusable = true
        isFocusableInTouchMode = true
        LayoutInflater.from(context).inflate(R.layout.layout_icon_text_button, this, true)
        iconView = findViewById(R.id.iv_icon)
        textView = findViewById(R.id.tv_text)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.DefaultButton)
            val text = typedArray.getString(R.styleable.DefaultButton_android_text)
            textView.text = text
            val src = typedArray.getDrawable(R.styleable.DefaultButton_android_src)
            iconView.setImageDrawable(src)
            typedArray.recycle()
        }

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