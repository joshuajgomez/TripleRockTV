package com.joshgm3z.triplerocktv.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.joshgm3z.triplerocktv.databinding.ViewHomeCardBinding
import com.joshgm3z.triplerocktv.ui.browse.SettingItem
import com.joshgm3z.triplerocktv.ui.browse.settings.SettingCardView

data class HomeItem(
    val title: String,
    val iconRes: Int,
)

class HomeItemPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val binding = ViewHomeCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val binding = ViewHomeCardBinding.bind(viewHolder.view)
        val homeItem = item as HomeItem

        binding.tvTitle.text = homeItem.title
        binding.ivIcon.setImageResource(homeItem.iconRes)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val binding = ViewHomeCardBinding.bind(viewHolder.view)

        binding.ivIcon.setImageResource(-1)
    }
}