package com.joshgm3z.triplerocktv.ui.common

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.joshgm3z.triplerocktv.R
import com.joshgm3z.triplerocktv.databinding.ViewLoginButtonBinding

class LoginButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    enum class State {
        Initial,
        SigningIn,
        Success,
    }

    val binding: ViewLoginButtonBinding = ViewLoginButtonBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    var state: State = State.Initial
        set(value) {
            when (value) {
                State.Initial -> {
                    binding.tvText.text = "Sign in"
                    binding.progressIndicator.visibility = GONE
                    binding.ivIcon.visibility = VISIBLE
                    binding.ivIcon.setImageResource(R.drawable.ic_arrow_forward)
                }

                State.SigningIn -> {
                    binding.tvText.text = "Signing in"
                    binding.progressIndicator.visibility = VISIBLE
                    binding.ivIcon.visibility = GONE
                }

                State.Success -> {
                    binding.tvText.text = "Signed in"
                    binding.tvText.setTextColor(Color.BLACK)
                    binding.llLoginButton.setBackgroundColor(Color.WHITE)
                    binding.progressIndicator.visibility = GONE
                    binding.ivIcon.visibility = VISIBLE
                    binding.ivIcon.setImageResource(R.drawable.ic_check_circle_green)
                }
            }
            field = value
        }

    init {
        isFocusable = true
        isFocusableInTouchMode = true

        readAttributes(attrs)

        setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setBackgroundColor(Color.BLUE)
            } else {
                setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    private fun readAttributes(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoginButton)
        val stateValue = typedArray.getInt(R.styleable.LoginButton_loginState, 0)
        state = when (stateValue) {
            1 -> State.SigningIn
            2 -> State.Success
            else -> State.Initial
        }
        typedArray.recycle()
    }

}