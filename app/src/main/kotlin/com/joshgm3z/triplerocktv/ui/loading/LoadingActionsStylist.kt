package com.joshgm3z.triplerocktv.ui.loading

import androidx.leanback.widget.GuidedAction
import androidx.leanback.widget.GuidedActionsStylist

class LoadingActionsStylist : GuidedActionsStylist() {
    override fun onBindViewHolder(vh: ViewHolder, action: GuidedAction) {
        super.onBindViewHolder(vh, action)
        setIconSize(vh, 18)
    }

    private fun setIconSize(vh: ViewHolder, size: Int) = vh.iconView?.let {
        val sizeInPx = (size * it.context.resources.displayMetrics.density).toInt()
        val params = it.layoutParams
        params.width = sizeInPx
        params.height = sizeInPx
        it.layoutParams = params
    }

}