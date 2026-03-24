package com.joshgm3z.triplerocktv.ui.details.series

import android.view.ViewGroup

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.leanback.widget.Presenter
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.repository.room.series.Season

class SeasonPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val textView = TextView(parent.context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            // Basic styling - adjust as needed
            setPadding(24, 16, 24, 16)
            setTextColor(ContextCompat.getColor(context, android.R.color.white))
            setBackgroundResource(R.color.gray)
        }
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val season = item as Season
        (viewHolder.view as TextView).text = season.name
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        (viewHolder.view as TextView).text = null
    }
}