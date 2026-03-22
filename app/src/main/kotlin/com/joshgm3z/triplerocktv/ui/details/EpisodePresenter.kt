package com.joshgm3z.triplerocktv.ui.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.Presenter.ViewHolder
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.databinding.ViewStreamCardBinding
import com.joshgm3z.triplerocktv.repository.data.Episode
import com.joshgm3z.triplerocktv.repository.impl.helper.parseToFloat
import com.joshgm3z.triplerocktv.repository.room.StreamData
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.util.GlideUtil
import javax.inject.Inject

class EpisodePresenter
@Inject constructor(
    private val glideUtil: GlideUtil,
) : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val textView = TextView(parent.context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            // Basic styling - adjust as needed
            width = 200
            height = 200
            setPadding(24, 16, 24, 16)
            setTextColor(ContextCompat.getColor(context, android.R.color.white))
            setBackgroundResource(R.color.gray)
        }
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val episode = item as Episode
        (viewHolder.view as TextView).text = "${episode.episode_num}: ${episode.title}"
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        (viewHolder.view as TextView).text = null
    }
}
