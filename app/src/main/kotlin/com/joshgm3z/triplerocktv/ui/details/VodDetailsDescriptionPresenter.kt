package com.joshgm3z.triplerocktv.ui.details

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import com.joshgm3z.triplerocktv.repository.room.vod.VodStream

class VodDetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter() {
    override fun onBindDescription(vh: ViewHolder, item: Any) {
        val stream = item as VodStream
        vh.title.text = stream.name
//        vh.subtitle.text = "Stream ID: ${stream.streamId}"
//        vh.body.text = "Category ID: ${stream.categoryId}\nAdded: ${stream.added}"
    }
}
