package com.joshgm3z.triplerocktv.ui.details

import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.leanback.widget.Presenter
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.repository.data.Episode
import javax.inject.Inject

class EpisodePresenter
@Inject constructor() : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val textView = TextView(parent.context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            // Basic styling - adjust as needed
            width = 300
            height = 200
            setPadding(24, 16, 24, 16)
            setTextColor(ContextCompat.getColorStateList(context, R.color.episode_text_color))
            textSize = 12f
            setBackgroundResource(R.drawable.episode_bg_selector)
        }
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val episode = item as Episode
        (viewHolder.view as TextView).text = viewHolder.view.context.getString(
            R.string.episode_title,
            episode.episode_num,
            episode.title
        )
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        (viewHolder.view as TextView).text = null
    }
}
