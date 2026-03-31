package com.joshgm3z.triplerocktv.ui.details.stream

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.databinding.ViewDetailsDescriptionBinding
import com.joshgm3z.triplerocktv.core.repository.room.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.toTextTime
import com.joshgm3z.triplerocktv.core.util.visibleIf

class StreamDataDetailsDescriptionPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        // You can use the standard lb_details_description layout or a custom one
        // For a progress bar, it's easier to use a custom layout file
        val view = ViewDetailsDescriptionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(view.root)
    }

    override fun onBindViewHolder(
        vh: ViewHolder,
        item: Any?
    ) = with(ViewDetailsDescriptionBinding.bind(vh.view)) {
        val streamData = item as StreamData
        streamData.let {
            tvTitle.text = it.name

            progressBar.progress = it.progressPercent()
            tvTimeRemaining.text = it.timeRemainingText()
            llProgressbarContainer.visibility = visibleIf((it.startedWatching))

            tvRating.text = it.rating.toString()
            llRatingContainer.visibility = visibleIf(it.rating > 0)

            tvDuration.text = root.context.getString(
                R.string.text_after_dot,
                it.movieMetadata?.totalDurationMs?.toTextTime()
            )
            tvDuration.visibility = visibleIf((it.movieMetadata?.totalDurationMs ?: 0) > 0)

            llMyListContainer.visibility = visibleIf(it.inMyList)

            tvDescription.text = it.movieMetadata?.description
            tvDescription.visibility = visibleIf(!it.movieMetadata?.description.isNullOrEmpty())

            tvSubtitleStatus.visibility = visibleIf(!it.subtitleUrl.isNullOrEmpty())

            tvGenre.visibility = visibleIf(!it.movieMetadata?.genre.isNullOrEmpty())
            tvGenre.text = it.movieMetadata?.genre
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {}
}
