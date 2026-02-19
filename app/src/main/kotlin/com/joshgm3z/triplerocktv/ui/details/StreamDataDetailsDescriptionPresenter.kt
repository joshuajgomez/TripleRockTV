package com.joshgm3z.triplerocktv.ui.details

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter

class DetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter() {
    override fun onBindDescription(vh: ViewHolder, item: Any) {
        val uiState = item as DetailsUiState
        vh.title.text = uiState.title
//        vh.subtitle.text = "Stream ID: ${stream.streamId}"
//        vh.body.text = "Category ID: ${stream.categoryId}\nAdded: ${stream.added}"
    }
}
