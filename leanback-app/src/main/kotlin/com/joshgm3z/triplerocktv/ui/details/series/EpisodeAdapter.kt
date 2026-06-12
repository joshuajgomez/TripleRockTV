package com.joshgm3z.triplerocktv.ui.details.series

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.data.Episode
import com.joshgm3z.triplerocktv.core.repository.impl.helper.parseToFloat
import com.joshgm3z.triplerocktv.core.repository.room.stream.toTextTime
import com.joshgm3z.triplerocktv.core.util.asTwoDigit
import com.joshgm3z.triplerocktv.databinding.ItemEpisodeBinding
import com.joshgm3z.triplerocktv.util.GlideUtil
import com.joshgm3z.triplerocktv.util.setVisible
import javax.inject.Inject

class EpisodeAdapter
@Inject constructor(
    private val glideUtil: GlideUtil
) : RecyclerView.Adapter<EpisodeViewHolder>() {

    var initialSelectedEpisodeNumber: Int? = null

    var episodes: List<Episode> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onEpisodeClick: (Episode) -> Unit = {}

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EpisodeViewHolder {
        val binding = ItemEpisodeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return EpisodeViewHolder(binding.root)
    }

    override fun onBindViewHolder(
        holder: EpisodeViewHolder,
        position: Int
    ) {
        val binding = ItemEpisodeBinding.bind(holder.itemView)
        val episode = episodes[position]
        binding.tvEpisodeTitle.text = episode.title
        binding.tvEpisodeDescription.text = episode.episodeInfo?.plot
        binding.root.setOnClickListener {
            onEpisodeClick(episode)
        }
        binding.metadataView.duration = episode.totalDurationMs().toTextTime()
        binding.metadataView.rating = episode.episodeInfo?.rating.parseToFloat()

        val progressPercent = episode.progressPercent()
        binding.pbEpisodeProgress.progress = progressPercent
        binding.pbEpisodeProgress.setVisible(progressPercent > 0)
        if (progressPercent > 0) binding.metadataView.timeLeft = episode.timeRemainingText()
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

    override fun getItemCount() = episodes.size
}

class EpisodeViewHolder(view: View) : RecyclerView.ViewHolder(view)

