package com.joshgm3z.triplerocktv.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.databinding.ViewEpisodeBinding
import com.joshgm3z.triplerocktv.repository.data.Episode
import com.joshgm3z.triplerocktv.repository.impl.helper.parseToFloat
import com.joshgm3z.triplerocktv.util.GlideUtil
import javax.inject.Inject

class EpisodePresenter
@Inject constructor(
    private val glideUtil: GlideUtil
) : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val binding = ViewEpisodeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val episode = item as Episode
        val context = viewHolder.view.context
        ViewEpisodeBinding.bind(viewHolder.view).apply {
            tvTitle.text = episode.title
            tvDescription.text = episode.episodeInfo?.plot
            metadata.rating = episode.episodeInfo?.rating?.parseToFloat()
            tvEpisodeNumber.text =
                context.getString(R.string.season_episode, episode.season, episode.episode_num)
            glideUtil.loadImage(episode.episodeInfo?.movie_image, ivPoster)
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {

    }
}
