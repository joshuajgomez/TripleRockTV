package com.joshgm3z.triplerocktv.ui.player.subtitle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joshgm3z.triplerocktv.databinding.ItemDownloadedSubtitleBinding
import com.joshgm3z.triplerocktv.databinding.ItemSubtitleBinding
import com.joshgm3z.triplerocktv.util.languageName

class SubtitleListAdapter : RecyclerView.Adapter<SubtitleListViewHolder>() {

    var clickListener: SubtitleListClickListener? = null

    var subtitleList: List<SubtitleInfo> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SubtitleListViewHolder {
        ItemSubtitleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).let { return SubtitleListViewHolder(it.root) }
    }

    override fun onBindViewHolder(
        holder: SubtitleListViewHolder,
        position: Int
    ) {
        val data = subtitleList[position]
        holder.text = data.label ?: data.language ?: "Unknown"
        holder.language = data.language.languageName()
        holder.checked = data.isSelected
        holder.listenClickEvent {
            clickListener?.onSubtitleClicked(data)
        }
    }

    override fun getItemCount(): Int = subtitleList.size

}

class SubtitleListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding = ItemSubtitleBinding.bind(itemView)

    var text: String = ""
        set(value) {
            binding.tvTitle.text = value
            field = value
        }

    var language: String = ""
        set(value) {
            binding.tvLanguage.text = value
            field = value
        }

    var checked: Boolean = false
        set(value) {
            binding.rbSubtitle.isChecked = value
            field = value
        }

    fun listenClickEvent(onClick: () -> Unit) {
        itemView.setOnClickListener { onClick() }
    }
}

interface SubtitleListClickListener {
    fun onSubtitleClicked(subtitleInfo: SubtitleInfo)
}
