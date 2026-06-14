package com.joshgm3z.triplerocktv.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.withStyledAttributes
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.databinding.ViewButtonBinding
import com.joshgm3z.triplerocktv.databinding.ViewTimeMarkerBinding

class TimeMarkerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    val binding = ViewTimeMarkerBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    init {
        context.withStyledAttributes(attrs, R.styleable.TimeMarkerView) {
            getInt(R.styleable.TimeMarkerView_minutesOffset, 0).let { offset ->
                minutesOffset = offset
            }
            getString(R.styleable.TimeMarkerView_time).let { time ->
                timeText = time ?: ""
            }
        }
    }

    var timeText: String = ""
        set(value) {
            binding.tvTime.text = value
        }

    var minutesOffset: Int = 0
        set(value) {
            val width30Min = context.resources.getDimensionPixelSize(R.dimen.WIDTH_30_MIN)
            val translation = (value / 30.0) * width30Min
            this.translationX = translation.toFloat()
        }
}