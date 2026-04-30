package com.joshgm3z.triplerocktv.ui.search

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.joshgm3z.triplerocktv.databinding.ItemKeyboardBinding
import com.joshgm3z.triplerocktv.databinding.ViewKeyboardBinding

class KeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var listener: KeyboardViewListener? = null

    private val binding = ViewKeyboardBinding.inflate(
        LayoutInflater.from(context),
        this, true
    )

    var text = ""
        set(value) {
            field = value
            listener?.onTextChange(value)
        }

    init {
        binding.rvKeyboard.adapter = KeyboardAdapter {
            text = text.plus(it)
        }
        binding.ivSpace.setOnClickListener {
            if (text.trim().isEmpty()) return@setOnClickListener
            text = text.plus(" ")
        }
        binding.ivBackspace.setOnClickListener {
            if (text.isNotEmpty()) {
                text = text.dropLast(1)
            }
        }
    }
}

interface KeyboardViewListener {
    fun onTextChange(text: String)
}

private class KeyboardAdapter(
    private val onKeyClick: (String) -> Unit
) : RecyclerView.Adapter<KeyViewHolder>() {
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
        binding.tvKey.setOnClickListener {
            onKeyClick(keys[position])
        }
        if (position == 0) binding.tvKey.requestFocus()
    }

    override fun getItemCount() = keys.size

}

private class KeyViewHolder(view: View) : RecyclerView.ViewHolder(view)
