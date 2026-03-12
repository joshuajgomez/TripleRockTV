package com.joshgm3z.triplerocktv.ui.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.joshgm3z.triplerocktv.databinding.ViewDetailsDescriptionBinding
import com.joshgm3z.triplerocktv.repository.room.StreamData

class DetailsDescriptionPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        // You can use the standard lb_details_description layout or a custom one
        // For a progress bar, it's easier to use a custom layout file
        val view = ViewDetailsDescriptionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DescriptionViewHolder(view.root)
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
        binding.progressBar.visibility =
            if (streamData.progressPercent() > 0) View.VISIBLE else View.GONE
        binding.llRatingContainer.visibility =
            if (streamData.rating > 0) View.VISIBLE else View.GONE
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {

    }

    private class DescriptionViewHolder(view: View) : ViewHolder(view)
}
