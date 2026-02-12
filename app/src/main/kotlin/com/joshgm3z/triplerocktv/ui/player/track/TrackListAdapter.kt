package com.joshgm3z.triplerocktv.ui.player.track

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joshgm3z.triplerocktv.databinding.ItemTrackInfoBinding
import com.joshgm3z.triplerocktv.util.languageName

class TrackListAdapter : RecyclerView.Adapter<TrackListViewHolder>() {

    var clickListener: TrackListClickListener? = null

    var trackList: List<TrackInfo> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackListViewHolder {
        ItemTrackInfoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).let { return TrackListViewHolder(it.root) }
    }

    override fun onBindViewHolder(
        holder: TrackListViewHolder,
        position: Int
    ) {
        val data = trackList[position]
        holder.text = data.label ?: data.language ?: "Unknown"
        holder.language = data.language.languageName()
        holder.checked = data.isSelected
        holder.listenClickEvent {
            clickListener?.onTrackClicked(data)
        }
    }

    override fun getItemCount(): Int = trackList.size

}

class TrackListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding = ItemTrackInfoBinding.bind(itemView)

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
            binding.rbTrack.isChecked = value
            field = value
        }

    fun listenClickEvent(onClick: () -> Unit) {
        itemView.setOnClickListener { onClick() }
    }
}

interface TrackListClickListener {
    fun onTrackClicked(trackInfo: TrackInfo)
}
