package com.joshgm3z.triplerocktv.ui.player.track

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joshgm3z.triplerocktv.core.util.Logger
import com.joshgm3z.triplerocktv.core.util.languageName
import com.joshgm3z.triplerocktv.databinding.ItemTextChipBinding
import kotlin.collections.map

class TextAdapter(
    private val onTextClick: (String) -> Unit
) : RecyclerView.Adapter<TextViewHolder>() {

    var texts: List<String> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var selectedText: String? = null
        set(value) {
            field?.let {
                notifyItemChanged(it.position())
            }
            value?.let {
                notifyItemChanged(it.position())
            }
            field = value
        }

    private fun String.position() = texts.indexOfFirst { it == this }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TextViewHolder {
        val binding = ItemTextChipBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return TextViewHolder(binding.root)
    }

    override fun onBindViewHolder(
        holder: TextViewHolder,
        position: Int
    ) {
        val binding = ItemTextChipBinding.bind(holder.itemView)
        val text = texts[position]
        binding.tvText.text = text.languageName()
        binding.root.setOnClickListener {
            onTextClick(text)
        }
        binding.root.isSelected = selectedText == text
    }

    override fun getItemCount() = texts.size
}

class TextViewHolder(view: View) : RecyclerView.ViewHolder(view)