package com.joshgm3z.triplerocktv.ui.details.series

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.joshgm3z.triplerocktv.databinding.ViewSeasonInfoBinding
import com.joshgm3z.triplerocktv.repository.room.series.Season
import com.joshgm3z.triplerocktv.util.GlideUtil
import javax.inject.Inject

class SeasonInfoPresenter
@Inject constructor(
    private val glideUtil: GlideUtil
) : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = ViewSeasonInfoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ).root
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        viewHolder: ViewHolder,
        item: Any?
    ) {
        val season = item as Season
        ViewSeasonInfoBinding.bind(viewHolder.view).apply {
            tvDescription.text = season.overview
            metadataView.apply {
                episodeCount = season.episodes.size
                rating = season.voteAverage
            }
            glideUtil.loadImage(season.coverImageUrl, ivPoster)
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {}
}