package com.joshgm3z.triplerocktv.ui.livetv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.leanback.widget.Presenter
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.databinding.ViewChannelBinding
import com.joshgm3z.triplerocktv.util.GlideUtil
import com.joshgm3z.triplerocktv.util.setVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChannelPresenter
@Inject constructor(
    private val glideUtil: GlideUtil,
    private val scope: CoroutineScope
) : Presenter() {

    var setFavorite: (suspend (StreamData, Boolean) -> Boolean)? = null

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val binding = ViewChannelBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(
        viewHolder: ViewHolder,
        item: Any?
    ) {
        val streamData = item as? StreamData ?: return
        val binding = ViewChannelBinding.bind(viewHolder.view)

        binding.tvProgramName.text = streamData.name
        binding.ivStar.setVisible(streamData.inMyList)
        binding.root.setOnLongClickListener {
            scope.launch {
                val add = !binding.ivStar.isVisible
                val success = setFavorite?.invoke(streamData, !binding.ivStar.isVisible) ?: false
                val message = when (success) {
                    true -> when (add) {
                        true -> "Added to favorites"
                        else -> "Removed from favorites"
                    }

                    else -> "Error updating favorites"
                }
                binding.updateFavIconAfterDelay(if (success) add else !add, message)
            }
            true
        }
        glideUtil.loadImage(
            streamData.streamIcon,
            binding.ivLogo,
            error = R.drawable.baseline_ondemand_video_24
        )
    }

    fun ViewChannelBinding.updateFavIconAfterDelay(showFavIcon: Boolean, message: String) {
        scope.launch(Dispatchers.Main) {
            // Prepare the view
            tvAddedToFavorites.text = message
            tvAddedToFavorites.alpha = 0f
            tvAddedToFavorites.setVisible(true)
            ivStar.setVisible(false)

            // Fade In
            tvAddedToFavorites.animate()
                .alpha(1f)
                .setDuration(300)
                .start()

            delay(1500)

            // Fade Out
            tvAddedToFavorites.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction {
                    tvAddedToFavorites.setVisible(false)
                    ivStar.setVisible(showFavIcon)
                    // Reset alpha for next time
                    ivStar.alpha = 1f
                }
                .start()
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val binding = ViewChannelBinding.bind(viewHolder.view)
        binding.ivLogo.setImageDrawable(null)
    }
}