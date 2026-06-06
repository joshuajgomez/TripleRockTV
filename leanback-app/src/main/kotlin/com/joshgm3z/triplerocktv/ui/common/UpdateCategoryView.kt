package com.joshgm3z.triplerocktv.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.withStyledAttributes
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.databinding.ViewCategoryItemBinding
import com.joshgm3z.triplerocktv.util.setVisible

class UpdateCategoryView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val binding = ViewCategoryItemBinding.inflate(
        LayoutInflater.from(context),
        this,
        true,
    )

    init {
        attrs?.let {
            context.withStyledAttributes(it, R.styleable.UpdateCategoryView) {
                getString(
                    R.styleable.UpdateCategoryView_title,
                ).let { text -> title = text }
                getString(
                    R.styleable.UpdateCategoryView_subtitle,
                ).let { text -> subtitle = text }
                getBoolean(
                    R.styleable.UpdateCategoryView_showUpdateButton,
                    false
                ).let { show ->
                    showUpdateButton = show
                }
                getBoolean(
                    R.styleable.UpdateCategoryView_showPlaceholder,
                    false
                ).let { show ->
                    showPlaceholder = show
                }
            }
        }
        binding.root.setOnClickListener {
            callOnClick()
        }
        binding.root.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            binding.tvUpdate.setVisible(hasFocus)
        }
    }

    private var showUpdateButton: Boolean = false
        set(value) {
            binding.tvUpdate.setVisible(value)
        }

    var showPlaceholder: Boolean = false
        set(value) {
            binding.placeholder.setVisible(value)
            binding.tvTitle.setVisible(!value)
            binding.tvSubtitle.setVisible(!value)
            field = value
        }

    var subtitle: String?
        get() = binding.tvSubtitle.text.toString()
        set(value) {
            binding.tvSubtitle.text = value
        }

    var title: String?
        get() = binding.tvTitle.text.toString()
        set(value) {
            binding.tvTitle.text = value
        }

    var showLoading: Boolean = false
        set(value) {
            binding.progressBar.setVisible(value)
            field = value
        }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        binding.root.isClickable = enabled
        binding.root.isFocusable = enabled
    }

    var showErrorStatus: Boolean = false
        set(value) {
            val res = if (value) com.joshgm3z.triplerocktv.core.R.color.color_error
            else com.joshgm3z.triplerocktv.core.R.color.color_card_content
            val color = resources.getColor(res, null)
            binding.tvSubtitle.setTextColor(color)
        }
}
