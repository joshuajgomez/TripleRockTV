package com.joshgm3z.triplerocktv.ui.livetv

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joshgm3z.triplerocktv.core.viewmodel.Program
import com.joshgm3z.triplerocktv.databinding.ItemProgramBinding

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

        binding.tvProgramName.text = program.title
        binding.tvProgramDescription.text = program.description
        binding.tvProgramTime.text = "${program.start} to ${program.stop}"
    }

    override fun getItemCount() = programs.size

    class ProgramViewHolder(view: View) : RecyclerView.ViewHolder(view)
}