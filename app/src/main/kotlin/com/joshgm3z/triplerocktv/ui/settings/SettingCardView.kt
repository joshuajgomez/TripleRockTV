package com.joshgm3z.triplerocktv.ui.settings

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.leanback.widget.BaseCardView
import com.joshgm3z.triplerocktv.R

class SettingCardView(context: Context) : BaseCardView(context) {
    val iconView: ImageView
    val titleView: TextView

    init {
        // 1. Set card focus properties
        isFocusable = true
        isFocusableInTouchMode = true

        // 2. Inflate your custom layout
        LayoutInflater.from(context).inflate(R.layout.view_setting_card, this)

        iconView = findViewById(R.id.iv_icon)
        titleView = findViewById(R.id.tv_title)

        // Optional: Set a specific card type for behavior
        cardType = CARD_TYPE_MAIN_ONLY
    }
}