package com.joshgm3z.triplerocktv.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joshgm3z.triplerocktv.core.repository.impl.helper.parseToFloat
import com.joshgm3z.triplerocktv.core.repository.room.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
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

        val title = when (item) {
            is StreamData -> item.name
            is SeriesStream -> item.name
            else -> "Unknown"
        }
        val imageUri = when (item) {
            is StreamData -> item.streamIcon
            is SeriesStream -> item.coverImageUrl
            else -> "Unknown"
        }
        val rating = when (item) {
            is StreamData -> item.rating
            is SeriesStream -> item.rating.parseToFloat()
            else -> null
        }

        binding.tvRating.text = rating.toString()
        binding.tvRating.setVisible(rating != null && rating > 0)

        binding.streamTitle.text = title
        glideUtil.loadImage(imageUri, binding.posterImage)

        binding.root.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}