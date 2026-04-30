package com.joshgm3z.triplerocktv.ui.search

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.leanback.widget.ItemBridgeAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joshgm3z.triplerocktv.databinding.ItemKeyboardBinding
import com.joshgm3z.triplerocktv.databinding.ViewKeyboardBinding

class KeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = ViewKeyboardBinding.inflate(
        LayoutInflater.from(context),
        this, true
    )

    init {
        binding.rvKeyboard.layoutManager = GridLayoutManager(context, 6)
        binding.rvKeyboard.adapter = KeyboardAdapter()
    }
}

private class KeyboardAdapter : RecyclerView.Adapter<KeyViewHolder>() {
    private val keys = listOf(
        "a", "b", "c", "d", "e", "f",
        "g", "h", "i", "j", "k", "l",
        "m", "n", "o", "p", "q", "r",
        "s", "t", "u", "v", "w", "x",
        "y", "z", "1", "2", "3", "4",
        "5", "6", "7", "8", "9", "0"
    )

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): KeyViewHolder {
        val binding = ItemKeyboardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return KeyViewHolder(binding.root)
    }

    override fun onBindViewHolder(
        holder: KeyViewHolder,
        position: Int
    ) {
        val binding = ItemKeyboardBinding.bind(holder.itemView)
        binding.tvKey.text = keys[position]
    }

    override fun getItemCount() = keys.size

}

private class KeyViewHolder(view: View) : RecyclerView.ViewHolder(view)