package com.joshgm3z.triplerocktv.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joshgm3z.triplerocktv.databinding.ItemTextBinding

class SimpleTextAdapter(
    private val items: List<String>,
    private val onTextClick: (String) -> Unit,
) : RecyclerView.Adapter<SimpleTextViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SimpleTextViewHolder {
        val binding = ItemTextBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SimpleTextViewHolder(binding.root)
    }

    override fun onBindViewHolder(
        holder: SimpleTextViewHolder,
        position: Int
    ) {
        val binding = ItemTextBinding.bind(holder.itemView)
        binding.root.text = items[position]
        binding.root.setOnClickListener {
            onTextClick(items[position])
        }
    }

    override fun getItemCount() = items.size
}

class SimpleTextViewHolder(view: View) : RecyclerView.ViewHolder(view)
