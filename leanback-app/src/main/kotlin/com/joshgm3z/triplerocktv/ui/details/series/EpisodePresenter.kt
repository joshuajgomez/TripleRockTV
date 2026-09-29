package com.joshgm3z.triplerocktv.ui.details.series

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.data.Episode
import com.joshgm3z.triplerocktv.core.repository.impl.helper.parseToFloat
import com.joshgm3z.triplerocktv.core.util.asTwoDigit
import com.joshgm3z.triplerocktv.core.util.toTextTime
import com.joshgm3z.triplerocktv.databinding.ItemEpisodeBinding
import com.joshgm3z.triplerocktv.util.GlideUtil
import com.joshgm3z.triplerocktv.util.setVisible
import javax.inject.Inject

class EpisodePresenter
@Inject constructor(
    private val glideUtil: GlideUtil
) : Presenter() {

    var initialSelectedEpisodeNumber: Int? = null

    var onEpisodeClick: (Episode) -> Unit = {}

    override fun onCreateViewHolder(viewGroup: ViewGroup): ViewHolder {
        val binding = ItemEpisodeBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup, false
        )
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val binding = ItemEpisodeBinding.bind(viewHolder.view)
        val episode = item as Episode
        binding.tvEpisodeTitle.text = episode.title
        binding.tvEpisodeDescription.text = episode.episodeInfo?.plot
        binding.root.setOnClickListener {
            onEpisodeClick(episode)
        }
        binding.metadataView.duration = episode.totalDurationMs().toTextTime()
        binding.metadataView.rating = episode.episodeInfo?.rating.parseToFloat()

        val progressPercent = episode.progressPercent()
        progressPercent?.let {
            binding.pbEpisodeProgress.progress = it
            binding.metadataView.timeLeft = episode.timeRemainingText()
        }
        binding.pbEpisodeProgress.setVisible(progressPercent != null)
        binding.metadataView.episodeLabel = "S${episode.season.asTwoDigit()}E${episode.episode_num}"

        glideUtil.loadImage(
            episode.episodeInfo?.movie_image,
            binding.ivEpisodePoster,
            R.drawable.default_media_poster
        )
        if (episode.episode_num == initialSelectedEpisodeNumber) binding.root.post {
            binding.root.requestFocus()
        }
        binding.root.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            binding.ivPlayIcon.setVisible(hasFocus)
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val binding = ItemEpisodeBinding.bind(viewHolder.view)
        Glide.with(binding.root.context).clear(binding.ivEpisodePoster)
    }

}
