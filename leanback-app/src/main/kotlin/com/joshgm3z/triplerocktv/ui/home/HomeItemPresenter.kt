package com.joshgm3z.triplerocktv.ui.home

import android.graphics.drawable.Animatable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.joshgm3z.triplerocktv.databinding.ViewHomeCardBinding

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

        binding.root.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) return@OnFocusChangeListener
            binding.ivIcon.drawable.let {
                if (it is Animatable) it.start()
            }
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val binding = ViewHomeCardBinding.bind(viewHolder.view)
        binding.ivIcon.drawable.let {
            if (it is Animatable) it.stop()
        }
        binding.ivIcon.setImageResource(-1)
    }
}