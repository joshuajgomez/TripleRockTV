package com.joshgm3z.triplerocktv.ui.player.track

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joshgm3z.triplerocktv.core.util.Logger
import com.joshgm3z.triplerocktv.databinding.ItemTrackInfoBinding
import com.joshgm3z.triplerocktv.core.util.languageName
import com.joshgm3z.triplerocktv.core.viewmodel.TrackInfo
import com.joshgm3z.triplerocktv.util.setVisible

class TrackListAdapter : RecyclerView.Adapter<TrackListViewHolder>() {

    var clickListener: TrackListClickListener? = null

    private var selectedPosition = -1
    private var overrideChecked = false

    var trackList: List<TrackInfo> = emptyList()
        set(value) {
            Logger.debug("trackList = $value")
            field = value
            selectedPosition = -1
            overrideChecked = false
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
        if (data.label == "Disabled") {
            holder.language = "Disabled"
            holder.text = ""
        } else {
            holder.text = data.label ?: data.language ?: "Unknown"
            holder.language = data.language.languageName()
        }
        holder.checked = !overrideChecked && data.isSelected
        if (holder.checked) selectedPosition = holder.bindingAdapterPosition
        holder.listenClickEvent {
            overrideChecked = true
            holder.checked = true
            notifyItemChanged(selectedPosition)
            clickListener?.onTrackClicked(data)
        }
    }

    override fun getItemCount(): Int = trackList.size
}

class TrackListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding = ItemTrackInfoBinding.bind(itemView)

    var text: String = ""
        set(value) {
            field = value
            binding.tvTitle.text = value
            binding.tvTitle.setVisible(value.isNotEmpty())
        }

    var language: String = ""
        set(value) {
            field = value
            binding.tvLanguage.text = value
            binding.tvLanguage.setVisible(value.isNotEmpty())
        }

    var checked: Boolean = false
        set(value) {
            field = value
            binding.rbTrack.isChecked = value
        }

    fun listenClickEvent(onClick: () -> Unit) {
        itemView.setOnClickListener { onClick() }
    }
}

interface TrackListClickListener {
    fun onTrackClicked(trackInfo: TrackInfo)
}
