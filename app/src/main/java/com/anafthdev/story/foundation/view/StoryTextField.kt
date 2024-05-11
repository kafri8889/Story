package com.anafthdev.story.foundation.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.core.widget.addTextChangedListener
import com.anafthdev.story.foundation.common.Validator
import com.google.android.material.textfield.TextInputEditText


class StoryTextField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
): TextInputEditText(context, attrs), OnTouchListener {

    private val validators: ArrayList<Pair<String, Validator<String>>> = arrayListOf()

    private var _trailingIcon: (() -> Drawable)? = null
    private var onTextChanged: OnTextChanged? = null
    private var onTrailingIconClickListener: OnClickListener? = null

    private val trailingIcon: Drawable?
        get() = _trailingIcon?.invoke()

    init {
        setOnTouchListener(this)
        addTextChangedListener(
            onTextChanged = { s, _, _, _ ->
                if (s == null) return@addTextChangedListener

                onTextChanged?.onTextChanged(s)

                runValidators(s)
            }
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null && trailingIcon != null) {
            val trailingIconStart: Float
            val trailingIconEnd: Float
            var isTrailingIconClicked = false

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                trailingIconEnd = (trailingIcon!!.intrinsicWidth + paddingStart).toFloat()
                if (event.x < trailingIconEnd) isTrailingIconClicked = true
            } else {
                trailingIconStart = (width - paddingEnd - trailingIcon!!.intrinsicWidth).toFloat()
                if (event.x > trailingIconStart) isTrailingIconClicked = true
            }

            if (isTrailingIconClicked && error == null) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> onTrailingIconClickListener?.onClick(this)
                }
            } else return false
        }

        return false
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText:Drawable? = null,
        endOfTheText:Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    /**
     * Run all [validators] to check if the input is valid or not
     */
    private fun runValidators(s: CharSequence) {
        for (validator in validators) {
            validator.second.validate(s.toString()).let { result ->
                error = if (result.isFailure) result.parseErrorMessage(context) else null
            }
        }
    }

    fun hideTrailingIcon() = setButtonDrawables()

    fun showTrailingIcon() {
        setButtonDrawables(endOfTheText = trailingIcon)
    }

    fun addOnTextChangedListener(listener: OnTextChanged) {
        onTextChanged = listener
    }

    fun setOnTrailingIconClickListener(listener: OnClickListener) {
        onTrailingIconClickListener = listener
    }

    fun setTrailingIcon(drawable: () -> Drawable) {
        _trailingIcon = drawable
    }

    fun addValidator(tag: String, validator: Validator<String>) {
        validators.add(tag to validator)
    }

    fun interface OnTextChanged {
        fun onTextChanged(s: CharSequence)
    }


}
