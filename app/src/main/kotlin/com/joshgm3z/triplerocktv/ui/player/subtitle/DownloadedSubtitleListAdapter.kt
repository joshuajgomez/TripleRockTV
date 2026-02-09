package com.joshgm3z.triplerocktv.ui.player.subtitle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joshgm3z.triplerocktv.databinding.ItemDownloadedSubtitleBinding
import com.joshgm3z.triplerocktv.repository.SubtitleData

class DownloadedSubtitleListAdapter : RecyclerView.Adapter<DownloadedSubtitleListViewHolder>() {

    var clickListener: DownloadedSubtitleListClickListener? = null

    var subtitleList: List<SubtitleData> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DownloadedSubtitleListViewHolder {
        ItemDownloadedSubtitleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).let { return DownloadedSubtitleListViewHolder(it.root) }
    }

    override fun onBindViewHolder(
        holder: DownloadedSubtitleListViewHolder,
        position: Int
    ) {
        holder.text = subtitleList[position].title
        holder.listenClickEvent {
            clickListener?.onSubtitleClicked(subtitleList[position])
        }
    }

    override fun getItemCount(): Int = subtitleList.size

}

class DownloadedSubtitleListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding = ItemDownloadedSubtitleBinding.bind(itemView)

    var text: String = ""
        set(value) {
            binding.tvTitle.text = value
            field = value
        }

    fun listenClickEvent(onClick: () -> Unit) {
        itemView.setOnClickListener { onClick() }
    }
}

interface DownloadedSubtitleListClickListener {
    fun onSubtitleClicked(subtitleData: SubtitleData)
}
