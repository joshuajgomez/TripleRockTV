package com.joshgm3z.triplerocktv.ui.livetv

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.viewmodel.Program
import com.joshgm3z.triplerocktv.databinding.ItemProgramBinding
import com.joshgm3z.triplerocktv.util.setVisible

class ProgramAdapter : RecyclerView.Adapter<ProgramAdapter.ProgramViewHolder>() {
    var programs: List<Program> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProgramViewHolder {
        val binding = ItemProgramBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProgramViewHolder(binding.root)
    }

    override fun onBindViewHolder(
        holder: ProgramViewHolder,
        position: Int
    ) {
        val binding = ItemProgramBinding.bind(holder.itemView)
        val program = programs[position]

        binding.tvProgramName.text = binding.root.context.resources.getString(
            R.string.dot_before_text,
            program.title
        )
        binding.tvProgramDescription.text = program.description
        val time = "${program.start} - ${program.stop}"
        val nowPlayingTime = binding.root.context.resources.getString(
            R.string.dot_between_text, "Now playing", time
        )
        binding.tvProgramTime.text = if (program.isNowPlaying) nowPlayingTime else time
        binding.root.isSelected = program.isNowPlaying
        binding.ivPlay.setVisible(program.isNowPlaying)
    }

    override fun getItemCount() = programs.size

    class ProgramViewHolder(view: View) : RecyclerView.ViewHolder(view)
}