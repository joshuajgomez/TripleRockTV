package com.joshgm3z.triplerocktv.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.databinding.ViewDetailsDescriptionBinding
import com.joshgm3z.triplerocktv.databinding.ViewSeriesDetailsDescriptionBinding
import com.joshgm3z.triplerocktv.repository.impl.helper.parseToFloat
import com.joshgm3z.triplerocktv.repository.room.StreamData
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.repository.room.toTextTime
import com.joshgm3z.triplerocktv.util.visibleIf

class SeriesDetailsDescriptionPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        // You can use the standard lb_details_description layout or a custom one
        // For a progress bar, it's easier to use a custom layout file
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
    ) = with(ViewDetailsDescriptionBinding.bind(vh.view)) {
        val streamData = item as SeriesStream
        streamData.let {
            tvTitle.text = it.name

//            progressBar.progress = it.progressPercent()
//            tvTimeRemaining.text = it.timeRemainingText()
//            llProgressbarContainer.visibility = visibleIf((it.startedWatching))

            tvRating.text = it.rating.toString()
            llRatingContainer.visibility = visibleIf(it.rating.parseToFloat() > 0)

//            tvDuration.text = root.context.getString(
//                R.string.text_after_dot,
//                it.movieMetadata?.totalDurationMs?.toTextTime()
//            )
//            tvDuration.visibility = visibleIf((it.movieMetadata?.totalDurationMs ?: 0) > 0)

//            llMyListContainer.visibility = visibleIf(it.inMyList)

            tvDescription.text = it.plot
            tvDescription.visibility = visibleIf(!it.plot.isNullOrEmpty())

//            tvSubtitleStatus.visibility = visibleIf(!it.subtitleUrl.isNullOrEmpty())

            tvGenre.visibility = visibleIf(!it.genre.isNullOrEmpty())
            tvGenre.text = it.genre
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {}
}
