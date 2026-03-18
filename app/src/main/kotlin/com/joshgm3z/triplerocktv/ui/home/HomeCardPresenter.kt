package com.joshgm3z.triplerocktv.ui.home

import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.joshgm3z.triplerocktv.ui.browse.settings.SettingCardView

class HomeCardPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = HomeCardView(parent.context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            // Set dimensions for the card
        }
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        if (item !is HomeCard) return
        val cardView = viewHolder.view as HomeCardView
        cardView.titleView.text = item.title
        cardView.iconView.setImageResource(item.iconRes)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val cardView = viewHolder.view as SettingCardView
        cardView.iconView.setImageResource(-1)
    }

}
