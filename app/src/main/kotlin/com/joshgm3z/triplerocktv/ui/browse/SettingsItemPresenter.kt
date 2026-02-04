package com.joshgm3z.triplerocktv.ui.browse

import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.joshgm3z.triplerocktv.ui.settings.SettingCardView

class SettingsItemPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = SettingCardView(parent.context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            // Set dimensions for the card
        }
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val cardView = viewHolder.view as SettingCardView
        val settingItem = item as SettingItem
        cardView.titleView.text = settingItem.title

        cardView.iconView.setImageResource(settingItem.iconRes)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val cardView = viewHolder.view as SettingCardView
        cardView.iconView.setImageResource(-1)
    }
}
