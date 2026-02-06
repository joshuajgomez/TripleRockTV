package com.joshgm3z.triplerocktv.ui.login

import androidx.leanback.widget.GuidedAction
import androidx.leanback.widget.GuidedActionsStylist
import com.joshgm3z.triplerocktv.R

class LoginActionsStylist : GuidedActionsStylist() {
    override fun onBindViewHolder(vh: ViewHolder, action: GuidedAction) {
        super.onBindViewHolder(vh, action)
        when (action.id) {
            GuidedLoginFragment.idLogin -> {
                setIconSize(vh, 18)
                vh.itemView.setBackgroundResource(R.drawable.bg_login_button)
            }

            GuidedLoginFragment.idStatus -> setIconSize(vh, 28)
            else -> vh.itemView.background = null
        }
    }

    private fun setIconSize(vh: ViewHolder, size: Int) = vh.iconView?.let {
        val sizeInPx = (size * it.context.resources.displayMetrics.density).toInt()
        val params = it.layoutParams
        params.width = sizeInPx
        params.height = sizeInPx
        it.layoutParams = params
    }

}