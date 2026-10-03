package com.joshgm3z.triplerocktv.ui.details.series

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.leanback.widget.Presenter
import com.joshgm3z.triplerocktv.core.repository.room.series.Season
import com.joshgm3z.triplerocktv.databinding.ItemSeasonChipBinding

class SeasonPresenter : Presenter() {

    var selectedSeasonNumber: Int? = null

    private lateinit var selectedTextColorList: ColorStateList

    private lateinit var textColorList: ColorStateList

    private var textColor: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val binding = ItemSeasonChipBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        val context = binding.root.context
        selectedTextColorList = context.getColorStateList(
            com.joshgm3z.triplerocktv.core.R.color.color_foreground_primary_selector
        )
        textColorList = context.getColorStateList(
            com.joshgm3z.triplerocktv.core.R.color.color_foreground_mid_selector
        )
        textColor = context.getColor(
            com.joshgm3z.triplerocktv.core.R.color.color_foreground_mid
        )
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Any?) {
        val binding = ItemSeasonChipBinding.bind(holder.view)
        val season = item as Season
        binding.tvText.text = "Season ${season.number}"
        if (selectedSeasonNumber != season.number) binding.tvText.setTextColor(textColorList)
        else binding.tvText.setTextColor(selectedTextColorList)
    }

    override fun onUnbindViewHolder(holder: ViewHolder) {}
}
