package com.joshgm3z.triplerocktv.ui.browse.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.joshgm3z.triplerocktv.databinding.ViewSettingCardBinding

data class SettingItem(
    val title: String,
    val iconRes: Int,
)

class SettingsItemPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val binding = ViewSettingCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val binding = ViewSettingCardBinding.bind(viewHolder.view)
        val settingItem = item as SettingItem
        binding.tvTitle.text = settingItem.title
        binding.ivIcon.setImageResource(settingItem.iconRes)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val binding = ViewSettingCardBinding.bind(viewHolder.view)
        binding.ivIcon.setImageResource(-1)
    }
}