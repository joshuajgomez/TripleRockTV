package com.joshgm3z.triplerocktv.ui.browse

import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.leanback.widget.Presenter
import com.joshgm3z.triplerocktv.R

class SearchPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val textView = TextView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(300, 150)
            isFocusable = true
            isFocusableInTouchMode = true
            background = ContextCompat.getDrawable(context, android.R.drawable.btn_default)
            gravity = android.view.Gravity.CENTER
            setTextColor(ContextCompat.getColor(context, R.color.white))
            text = "Search"
        }
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        (viewHolder.view as TextView).text = item as String
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        // No-op
    }
}