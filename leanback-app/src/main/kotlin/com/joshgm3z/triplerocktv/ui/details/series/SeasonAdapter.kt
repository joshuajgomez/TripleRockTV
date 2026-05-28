package com.joshgm3z.triplerocktv.ui.details.series

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joshgm3z.triplerocktv.core.repository.room.series.Season
import com.joshgm3z.triplerocktv.databinding.ItemTextChipBinding

class SeasonAdapter(
    private val onSeasonClick: (Season) -> Unit
) : RecyclerView.Adapter<SeasonViewHolder>() {

    var seasons: List<Season> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var selectedSeasonNumber: Int? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SeasonViewHolder {
        val binding = ItemTextChipBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return SeasonViewHolder(binding.root)
    }

    override fun onBindViewHolder(
        holder: SeasonViewHolder,
        position: Int
    ) {
        val binding = ItemTextChipBinding.bind(holder.itemView)
        val season = seasons[position]
        binding.tvText.text = "Season ${season.number}"
        binding.root.setOnClickListener {
            onSeasonClick(season)
        }
        binding.root.isSelected = selectedSeasonNumber == season.number
    }

    override fun getItemCount() = seasons.size
}

class SeasonViewHolder(view: View) : RecyclerView.ViewHolder(view)