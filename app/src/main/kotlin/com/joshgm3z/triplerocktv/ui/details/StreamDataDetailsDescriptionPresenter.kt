package com.joshgm3z.triplerocktv.ui.details

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import com.joshgm3z.triplerocktv.repository.room.StreamData

class DetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter() {
    override fun onBindDescription(vh: ViewHolder, item: Any) {
        val streamData = item as StreamData
        vh.title.text = streamData.name
        vh.subtitle.text = "Rating: ${streamData.rating}"
    }
}
