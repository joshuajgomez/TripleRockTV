package com.joshgm3z.triplerocktv.ui.search

import android.view.ViewGroup

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.leanback.widget.Presenter
import com.joshgm3z.triplerocktv.R

class SimpleTextPresenter : Presenter() {

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
        val text = item as? String ?: item.toString()
        (viewHolder.view as TextView).text = text
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        (viewHolder.view as TextView).text = null
    }
}