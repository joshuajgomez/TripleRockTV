package com.joshgm3z.triplerocktv.ui.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.databinding.ViewDetailsDescriptionBinding
import com.joshgm3z.triplerocktv.repository.room.StreamData
import com.joshgm3z.triplerocktv.repository.room.toTextTime

class DetailsDescriptionPresenter : Presenter() {
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
    ) {
        val binding = ViewDetailsDescriptionBinding.bind(vh.view)
        val streamData = item as StreamData
        binding.tvTitle.text = streamData.name
        binding.tvRating.text = streamData.rating.toString()
        binding.progressBar.progress = streamData.progressPercent()

        binding.progressBar.visibility = visibleIf((streamData.progressPercent() > 0))
        binding.llRatingContainer.visibility = visibleIf(streamData.rating > 0)

        binding.tvDuration.text = binding.root.context.getString(
            R.string.text_after_dot,
            streamData.totalDuration.toTextTime()
        )
        binding.tvDuration.visibility = visibleIf(streamData.totalDuration > 0)
        binding.llMyListContainer.visibility = visibleIf(streamData.inMyList)
    }

    private fun visibleIf(visible: Boolean) = if (visible) View.VISIBLE else View.GONE

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {}
}
