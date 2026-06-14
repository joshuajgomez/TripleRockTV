package com.joshgm3z.triplerocktv.ui.livetv

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joshgm3z.triplerocktv.core.util.toTextTime
import com.joshgm3z.triplerocktv.databinding.ItemTimeBinding
import java.time.ZonedDateTime

class TimeFrameAdapter : RecyclerView.Adapter<TimeFrameAdapter.TimeFrameViewHolder>() {
    var timeFrames: List<ZonedDateTime> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TimeFrameViewHolder {
        val binding = ItemTimeBinding.inflate(
            LayoutInflater
                .from(parent.context),
            parent,
            false
        )
        return TimeFrameViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: TimeFrameViewHolder, position: Int) {
        val binding = ItemTimeBinding.bind(holder.view)
        binding.tvTime.text = timeFrames[position].toTextTime("hh:mm")
    }

    override fun getItemCount() = timeFrames.size

    data class TimeFrameViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}