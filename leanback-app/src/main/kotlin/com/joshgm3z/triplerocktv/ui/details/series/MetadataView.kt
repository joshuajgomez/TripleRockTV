package com.joshgm3z.triplerocktv.ui.details.series

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.util.getColorFromAttr

class MetadataView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var isFirst = true

    var rating: Float? = null
        set(value) {
            field = value
            updateMetadata()
        }

    var showMyList: Boolean = false
        set(value) {
            field = value
            updateMetadata()
        }

    var genre: String? = null
        set(value) {
            field = value
            updateMetadata()
        }

    var duration: String? = null
        set(value) {
            field = value
            updateMetadata()
        }

    var episodeCount: Int? = null
        set(value) {
            field = value
            updateMetadata()
        }

    var seasonCount: Int? = null
        set(value) {
            field = value
            updateMetadata()
        }

    var subtitleDownloaded: Boolean = false
        set(value) {
            field = value
            updateMetadata()
        }

    init {
        if (isInEditMode) {
            rating = 3.5f
            duration = "1h 45m"
            showMyList = true
        }
    }

    private fun updateMetadata() {
        removeAllViews()
        isFirst = true

        addMetadata(genre)
        addMetadata(rating.toString(), rating.isNonZero(), R.drawable.ic_star_scaled)
        addMetadata(duration)
        addMetadata("My list", showMyList, R.drawable.ic_check_scaled)
        addMetadata("$episodeCount episodes", episodeCount.isNonZero())
        addMetadata("$seasonCount seasons", seasonCount.isNonZero())
        addMetadata("Subtitle downloaded", subtitleDownloaded)
    }

    private fun addMetadata(
        text: String?,
        show: Boolean = true,
        icon: Int? = null,
    ) {
        if (!show) return
        if (text.isNullOrEmpty()) return
        if (!isFirst) addDot() else isFirst = false

        val tv = TextView(context)
        tv.text = text
        tv.textSize = 12f
        tv.alpha = 0.8f
        tv.setTextColor(resources.getColor(com.joshgm3z.triplerocktv.core.R.color.color_card_content, context.theme))
        icon?.let { tv.setDrawable(it) }
        addView(tv)
    }

    private fun Any?.isNonZero(): Boolean = when (this) {
        is Int -> this > 0
        is Long -> this > 0
        is Float -> this > 0
        else -> false
    }

    private fun addDot() {
        TextView(context).let {
            it.text = context.getString(R.string.dot)
            it.setTextColor(context.getColorFromAttr(R.attr.colorPrimary))
            addView(it)
        }
    }

    private fun TextView.setDrawable(res: Int) {
        setCompoundDrawablesWithIntrinsicBounds(
            AppCompatResources.getDrawable(context, res),
            null,
            null,
            null
        )
        compoundDrawablePadding = 5
    }
}

