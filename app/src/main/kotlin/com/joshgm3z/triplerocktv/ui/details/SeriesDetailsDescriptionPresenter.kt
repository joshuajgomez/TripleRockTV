package com.joshgm3z.triplerocktv.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.joshgm3z.triplerocktv.databinding.ViewSeriesDetailsDescriptionBinding
import com.joshgm3z.triplerocktv.util.visibleIf

class SeriesDetailsDescriptionPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = ViewSeriesDetailsDescriptionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(view.root)
    }

    override fun onBindViewHolder(
        vh: ViewHolder,
        item: Any?
    ) = with(ViewSeriesDetailsDescriptionBinding.bind(vh.view)) {
        val uiState = item as SeriesDetailsUiState
        uiState.let {
            tvTitle.text = it.episodeTitle

            progressBar.progress = it.progressPercent
            tvTimeRemaining.text = it.timeLeft
            llProgressbarContainer.visibility = visibleIf((it.progressPercent > 0))

            metadataView.rating = it.rating
            metadataView.duration = it.duration
            metadataView.genre = it.genre

            tvDescription.text = it.description
            tvDescription.visibility = visibleIf(it.description.isNotEmpty())
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {}
}
