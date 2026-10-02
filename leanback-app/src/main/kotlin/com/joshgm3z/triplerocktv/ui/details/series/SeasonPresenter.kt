package com.joshgm3z.triplerocktv.ui.details.series

import android.view.LayoutInflater
import android.view.ViewGroup

import android.widget.TextView
import androidx.leanback.widget.Presenter
import com.joshgm3z.triplerocktv.core.repository.room.series.Season
import com.joshgm3z.triplerocktv.databinding.ItemSeasonChipBinding

class SeasonPresenter : Presenter() {

    var selectedSeasonNumber: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val binding = ItemSeasonChipBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Any?) {
        val binding = ItemSeasonChipBinding.bind(holder.view)
        val season = item as Season
        binding.tvText.text = "Season ${season.number}"
        binding.root.isSelected = selectedSeasonNumber == season.number
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        (viewHolder.view as TextView).text = null
    }
}