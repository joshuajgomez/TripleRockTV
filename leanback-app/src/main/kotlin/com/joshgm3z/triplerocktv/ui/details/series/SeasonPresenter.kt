package com.joshgm3z.triplerocktv.ui.details.series

import android.view.ViewGroup

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.leanback.widget.Presenter
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.room.series.Season

class SeasonPresenter(
    var highlightSeasonNumber: Int? = null
) : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val textView = TextView(parent.context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            // Basic styling - adjust as needed
            setPadding(24, 16, 24, 16)
        }
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val season = item as Season
        val view = viewHolder.view as TextView
        view.text = "Season ${season.number}"
        when (season.number) {
            highlightSeasonNumber -> android.R.color.white
            else -> R.color.gray
        }.let {
            view.setBackgroundResource(it)
        }
        when (season.number) {
            highlightSeasonNumber -> R.color.gray
            else -> android.R.color.white
        }.let {
            view.setTextColor(ContextCompat.getColor(viewHolder.view.context, it))
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        (viewHolder.view as TextView).text = null
    }
}