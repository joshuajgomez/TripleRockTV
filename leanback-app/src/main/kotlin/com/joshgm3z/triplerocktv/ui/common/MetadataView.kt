package com.joshgm3z.triplerocktv.ui.common

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

    var noOfSeasons: Int? = null
        set(value) {
            field = value
            updateMetadata()
        }

    var subtitleDownloaded: Boolean = false
        set(value) {
            field = value
            updateMetadata()
        }

    var timeLeft: String? = null
        set(value) {
            field = value
            updateMetadata()
        }

    var episodeLabel: String? = null
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

        addMetadata(episodeLabel, episodeLabel != null)
        addMetadata(genre)
        addMetadata(rating.toString(), rating.isNonZero(), R.drawable.ic_star)
        addMetadata(duration)
        addMetadata("My list", showMyList, R.drawable.baseline_playlist_add_check_14)
        addMetadata("$episodeCount episodes", episodeCount.isNonZero())
        addMetadata(
            if (noOfSeasons == 1) "1 season" else "$noOfSeasons seasons",
            noOfSeasons.isNonZero()
        )
        addMetadata("Subtitle downloaded", subtitleDownloaded)
        addMetadata(timeLeft, timeLeft != null)
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
        tv.setTextColor(context.getColorFromAttr(R.attr.colorForegroundMid))

        tv.layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            25.dpToPx() // Custom extension or hardcoded px
        )
        tv.gravity = android.view.Gravity.CENTER_VERTICAL

        icon?.let { tv.setDrawable(it) }
        addView(tv)
    }

    private fun Int.dpToPx(): Int = (this * context.resources.displayMetrics.density).toInt()

    private fun Any?.isNonZero(): Boolean = when (this) {
        is Int -> this > 0
        is Long -> this > 0
        is Float -> this > 0
        else -> false
    }

    private fun addDot() {
        TextView(context).let {
            it.text = context.getString(R.string.dot)
            it.setTextColor(context.getColorFromAttr(R.attr.colorForegroundPrimary))
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