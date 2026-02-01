package com.joshgm3z.triplerocktv.ui.browse

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.repository.room.StreamEntity

class StreamAdapter : ListAdapter<StreamEntity, StreamAdapter.StreamViewHolder>(StreamDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StreamViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stream, parent, false)
        return StreamViewHolder(view)
    }

    override fun onBindViewHolder(holder: StreamViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class StreamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.streamName)
        private val iconImageView: ImageView = itemView.findViewById(R.id.streamIcon)

        init {
            itemView.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    v.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200).start()
                    v.elevation = 10f
                } else {
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
                    v.elevation = 0f
                }
            }
        }

        fun bind(stream: StreamEntity) {
            nameTextView.text = stream.name
            Glide.with(itemView.context)
                .load(stream.streamIcon)
                .placeholder(android.R.drawable.ic_menu_report_image)
                .into(iconImageView)
        }
    }

    class StreamDiffCallback : DiffUtil.ItemCallback<StreamEntity>() {
        override fun areItemsTheSame(oldItem: StreamEntity, newItem: StreamEntity): Boolean {
            return oldItem.streamId == newItem.streamId
        }

        override fun areContentsTheSame(oldItem: StreamEntity, newItem: StreamEntity): Boolean {
            return oldItem == newItem
        }
    }
}