package com.joshgm3z.triplerocktv.ui.livetv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isNotEmpty
import androidx.leanback.widget.Presenter
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.databinding.ViewChannelBinding
import com.joshgm3z.triplerocktv.util.GlideUtil
import com.joshgm3z.triplerocktv.util.setVisible
import javax.inject.Inject

class ChannelPresenter
@Inject constructor(
    private val glideUtil: GlideUtil,
) : Presenter() {

    var setFavorite: ((StreamData, Boolean) -> Unit)? = null

    var launchSearchFragment: ((StreamData) -> Unit)? = null

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
        binding.ivStar.setVisible(streamData.favorite)

        binding.root.setOnLongClickListener {
            showPopupMenu(streamData, binding.root)
            true
        }
        glideUtil.loadImage(
            url = streamData.streamIcon,
            imageView = binding.ivLogo,
            centerCrop = false,
            error = R.drawable.baseline_ondemand_video_24,
            placeholder = R.drawable.baseline_ondemand_video_24,
        )
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val binding = ViewChannelBinding.bind(viewHolder.view)
        binding.ivLogo.setImageDrawable(null)
    }

    private fun showPopupMenu(
        streamData: StreamData,
        anchorView: View
    ) {
        val popup = PopupMenu(anchorView.context, anchorView)

        val makeFavorite = "Add favorite"
        val removeFavorite = "Remove favorite"
        if (setFavorite != null) {
            if (streamData.favorite) {
                popup.menu.add(removeFavorite)
            } else {
                popup.menu.add(makeFavorite)
            }
        }

        val searchSimilar = "Search similar titles"
        if (launchSearchFragment != null) {
            popup.menu.add(searchSimilar)
        }

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.title) {
                searchSimilar -> {
                    launchSearchFragment?.invoke(streamData)
                    true
                }

                makeFavorite -> {
                    setFavorite?.invoke(streamData, true)
                    true
                }

                removeFavorite -> {
                    setFavorite?.invoke(streamData, false)
                    true
                }

                else -> false
            }
        }
        if (popup.menu.isNotEmpty()) popup.show()
    }
}