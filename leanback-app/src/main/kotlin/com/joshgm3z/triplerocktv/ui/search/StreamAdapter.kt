package com.joshgm3z.triplerocktv.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.util.isRecent
import com.joshgm3z.triplerocktv.databinding.ViewStreamCardBinding
import com.joshgm3z.triplerocktv.util.GlideUtil
import com.joshgm3z.triplerocktv.util.setVisible
import javax.inject.Inject

class StreamAdapter
@Inject constructor(
    private val glideUtil: GlideUtil,
    private val onClick: (Any) -> Unit,
) : RecyclerView.Adapter<StreamAdapter.ViewHolder>() {

    var items: List<Any> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ViewStreamCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val binding = ViewStreamCardBinding.bind(holder.itemView)
        val item = items[position]

        when (item) {
            is StreamData -> item.name
            is SeriesStream -> item.name
            else -> null
        }.let {
            binding.streamTitle.text = it
        }

        when (item) {
            is StreamData -> when (item.streamType) {
                StreamType.LiveTV -> glideUtil.loadImage(
                    item.streamIcon,
                    binding.ivIcon,
                    centerCrop = false,
                    error = R.drawable.ic_video_file
                )

                else -> glideUtil.loadImage(
                    item.streamIcon,
                    binding.posterImage
                )
            }

            is SeriesStream -> glideUtil.loadImage(
                item.coverImageUrl,
                binding.posterImage
            )
        }

        when (item) {
            is StreamData -> item.added.isRecent()
            is SeriesStream -> item.lastModified.isRecent()
            else -> null
        }.let {
            binding.tvNew.setVisible(it)
        }

        binding.root.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            binding.ivGo.setVisible(hasFocus)
        }

        binding.root.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}