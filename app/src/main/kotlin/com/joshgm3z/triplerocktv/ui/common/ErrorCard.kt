package com.joshgm3z.triplerocktv.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.databinding.ViewErrorCardBinding

class ErrorCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = ViewErrorCardBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.ErrorCard)
            val errorText = typedArray.getString(R.styleable.ErrorCard_android_text)
            text = errorText
            typedArray.recycle()
        }
    }

    var text: String? = "Something went wrong"
        set(value) {
            binding.tvError.text = value
            binding.root.visibility = if (value.isNullOrEmpty()) GONE else VISIBLE
            field = value
        }
}