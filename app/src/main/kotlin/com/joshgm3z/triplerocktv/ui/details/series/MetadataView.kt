package com.joshgm3z.triplerocktv.ui.details.series

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.databinding.ViewFindButtonBinding
import com.joshgm3z.triplerocktv.databinding.ViewMetadataBinding
import com.joshgm3z.triplerocktv.repository.room.toTextTime
import com.joshgm3z.triplerocktv.util.setVisible
import com.joshgm3z.triplerocktv.util.visibleIf

class MetadataView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: ViewMetadataBinding = ViewMetadataBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    var rating: Float? = null
        set(value) {
            binding.tvRating.setVisible(rating != null)
            binding.tvRating.text = value.toString()
            field = value
        }

    var showMyList: Boolean = false
        set(value) {
            binding.tvMyList.setVisible(showMyList)
            field = value
        }

    var genre: String? = null
        set(value) {
            binding.tvGenre.setVisible(value != null)
            binding.tvGenre.text = context.getString(R.string.text_after_dot, value)
            field = value
        }

    var durationMs: Long? = null
        set(value) {
            binding.tvDuration.setVisible(value != null)
            binding.tvDuration.text =
                context.getString(R.string.text_after_dot, value?.toTextTime())
            field = value
        }

    var episodeCount: Int? = null
        set(value) {
            binding.tvEpisodeCount.setVisible(value != null)
            binding.tvEpisodeCount.text = "$value episodes"
            field = value
        }
}
