package com.joshgm3z.triplerocktv.ui.livetv

import android.annotation.SuppressLint
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.core.repository.data.XmlTvProgram
import com.joshgm3z.triplerocktv.core.viewmodel.Channel
import com.joshgm3z.triplerocktv.databinding.ItemChannelBinding
import com.joshgm3z.triplerocktv.databinding.ItemProgramBinding
import com.joshgm3z.triplerocktv.util.GlideUtil
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class ChannelAdapter @Inject constructor(
    private val glideUtil: GlideUtil
) : RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder>() {
    var onChannelClicked: (Channel) -> Unit = {}
    var onChannelFocused: (Channel) -> Unit = {}

    private lateinit var firstTimeFrame: ZonedDateTime
    private lateinit var lastTimeFrame: ZonedDateTime
    private lateinit var currentTime: ZonedDateTime

    private var channels: List<Channel> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun setChannels(
        firstTimeFrame: ZonedDateTime,
        lastTimeFrame: ZonedDateTime,
        currentTime: ZonedDateTime,
        channels: List<Channel>
    ) {
        this.firstTimeFrame = firstTimeFrame
        this.lastTimeFrame = lastTimeFrame
        this.currentTime = currentTime
        this.channels = channels
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChannelViewHolder {
        val binding = ItemChannelBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val nonScrollingLayoutManager =
            object : LinearLayoutManager(parent.context, HORIZONTAL, false) {
                override fun canScrollVertically(): Boolean = false
                override fun canScrollHorizontally(): Boolean = false
            }
        binding.rvPrograms.layoutManager = nonScrollingLayoutManager
        return ChannelViewHolder(binding.root)
    }

    override fun onBindViewHolder(
        holder: ChannelViewHolder,
        position: Int
    ) {
        val binding = ItemChannelBinding.bind(holder.view)
        val channel = channels[position]
        binding.tvTitle.text = channel.name
        binding.tvIndex.text = "${position + 1}"
        glideUtil.loadImage(
            channel.logo,
            binding.ivLogo,
            com.joshgm3z.triplerocktv.core.R.drawable.movie_avd
        )
        binding.rvPrograms.adapter = ProgramAdapter(
            channel.programs,
            firstTimeFrame = firstTimeFrame,
            lastTimeFrame = lastTimeFrame,
            currentTime = currentTime
        )
        binding.root.setOnClickListener {
            onChannelClicked(channel)
        }
        binding.root.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                onChannelFocused(channel)
            }
        }
    }

    override fun getItemCount() = channels.size

    class ChannelViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}

class ProgramAdapter(
    private val programs: List<XmlTvProgram>,
    private val firstTimeFrame: ZonedDateTime,
    private val lastTimeFrame: ZonedDateTime,
    private val currentTime: ZonedDateTime,
) : RecyclerView.Adapter<ProgramAdapter.ProgramViewHolder>() {

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
        val program = programs[position]

        val binding = ItemProgramBinding.bind(holder.view)
        binding.tvProgramName.text = program.title
        val programWidth = getProgramWidth(program, binding.root.context.resources)
        binding.root.layoutParams.width = programWidth
        val isNowPlaying = currentTime.isAfter(program.start) && currentTime.isBefore(program.stop)
        binding.root.isSelected = isNowPlaying
    }

    private fun getProgramWidth(program: XmlTvProgram, resources: Resources): Int {
        val startTime = program.start
        val stopTime = program.stop
        val width30min = resources.getDimensionPixelSize(R.dimen.WIDTH_30_MIN)

        // 1. Calculate the total duration of the program
        val totalDurationMinutes = ChronoUnit.MINUTES.between(startTime, stopTime)

        // 2. Calculate how many minutes to "cut off" from the left
        // If the program started at 11:30 and the guide starts at 12:00, offset is 30.
        val offsetMinutes = if (startTime.isBefore(firstTimeFrame))
            ChronoUnit.MINUTES
                .between(
                    startTime,
                    firstTimeFrame
                )
        else 0L


        // 3. Calculate visible width: (Total - Cutoff) / 30 * width_per_unit
        val visibleMinutes = totalDurationMinutes - offsetMinutes
        val calculatedWidth = ((visibleMinutes / 30.0) * width30min).toInt()
        return calculatedWidth
    }

    override fun getItemCount() = programs.size

    class ProgramViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}