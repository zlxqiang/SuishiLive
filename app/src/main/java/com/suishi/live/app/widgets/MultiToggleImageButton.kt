/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.suishi.live.app.widgets

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.os.AsyncTask
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import com.suishi.live.app.R

/*
 * A toggle button that supports two or more states with images rendererd on top
 * for each state.
 * The button is initialized in an XML layout file with an array reference of
 * image ids (e.g. imageIds="@array/camera_flashmode_icons").
 * Each image in the referenced array represents a single integer state.
 * Every time the user touches the button it gets set to next state in line,
 * with the corresponding image drawn onto the face of the button.
 * State wraps back to 0 on user touch when button is already at n-1 state.
 */
class MultiToggleImageButton : AppCompatImageButton {
    /*
     * Listener interface for button state changes.
     */
    interface OnStateChangeListener {
        /*
         * @param view the MultiToggleImageButton that received the touch event
         * @param state the new state the button is in
         */
        fun stateChanged(view: View?, state: Int)
    }

    private var mOnStateChangeListener: OnStateChangeListener? = null
    private var mState = UNSET
    private var mImageIds: IntArray?=null
    private var mLevel = 0
    private var mClickEnabled = true
    private var mParentSize = 0
    private var mAnimDirection = 0
    private val mMatrix = Matrix()
    private var mAnimator: ValueAnimator? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
        parseAttributes(context, attrs)
        state = 0
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
        parseAttributes(context, attrs)
        state = 0
    }

    /*
     * Set the state change listener.
     *
     * @param onStateChangeListener the listener to set
     */
    fun setOnStateChangeListener(onStateChangeListener:OnStateChangeListener) {
        mOnStateChangeListener = onStateChangeListener as OnStateChangeListener
    }

    /*
     * Get the current button state.
     *
     *//*
     * Set the current button state, thus causing the state change listener to
     * get called.
     *
     * @param state the desired state
     */
    var state: Int
        get() = mState
        set(state) {
            setState(state, true)
        }

    /*
     * Set the current button state.
     *
     * @param state the desired state
     * @param callListener should the state change listener be called?
     */
    fun setState(state: Int, callListener: Boolean) {
        setStateAnimatedInternal(state, callListener)
    }

    /**
     * Set the current button state via an animated transition.
     *
     * @param state
     * @param callListener
     */
    private fun setStateAnimatedInternal(state: Int, callListener: Boolean) {
        if (mState == state || mState == UNSET) {
            setStateInternal(state, callListener)
            return
        }
        if (mImageIds == null) {
            return
        }
        object : AsyncTask<Int?, Void?, Bitmap?>() {

            override fun onPostExecute(bitmap: Bitmap?) {
                if (bitmap == null) {
                    setStateInternal(state, callListener)
                } else {
                    setImageBitmap(bitmap)
                    val offset: Int
                    offset = if (mAnimDirection == ANIM_DIRECTION_VERTICAL) {
                        (mParentSize + height) / 2
                    } else if (mAnimDirection == ANIM_DIRECTION_HORIZONTAL) {
                        (mParentSize + width) / 2
                    } else {
                        return
                    }
                    mAnimator!!.setFloatValues(-offset.toFloat(), 0.0f)
                    val s = AnimatorSet()
                    s.play(mAnimator)
                    s.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator) {
                            setClickEnabled(false)
                        }

                        override fun onAnimationEnd(animation: Animator) {
                            setStateInternal(state, callListener)
                            setClickEnabled(true)
                        }
                    })
                    s.start()
                }
            }

            override fun doInBackground(vararg params: Int?): Bitmap? {
                return params[0]?.let { params[1]?.let { it1 -> combine(it, it1) } }
            }
        }.execute(mState, state)
    }

    /**
     * Enable or disable click reactions for this button
     * without affecting visual state.
     * For most cases you'll want to use [.setEnabled].
     * @param enabled True if click enabled, false otherwise.
     */
    fun setClickEnabled(enabled: Boolean) {
        mClickEnabled = enabled
    }

    private fun setStateInternal(state: Int, callListener: Boolean) {
        mState = state
        if (mImageIds != null) {
            setImageByState(mState)
        }
        super.setImageLevel(mLevel)
        if (callListener && mOnStateChangeListener != null) {
            mOnStateChangeListener!!.stateChanged(this@MultiToggleImageButton, state)
        }
    }

    private fun nextState() {
        var state = mState + 1
        if (state >= mImageIds!!.size) {
            state = 0
        }
        state = state
    }

    protected fun init() {
        setOnClickListener {
            if (mClickEnabled) {
                nextState()
            }
        }
        scaleType = ScaleType.MATRIX
        mAnimator = ValueAnimator.ofFloat(0.0f, 0.0f).apply {
            setDuration(ANIM_DURATION_MS.toLong())
            setInterpolator(Gusterpolator.Companion.INSTANCE)
            addUpdateListener{ animation ->
                mMatrix.reset()
                if (mAnimDirection == ANIM_DIRECTION_VERTICAL) {
                    mMatrix.setTranslate(0.0f, (animation.animatedValue as Float))
                } else if (mAnimDirection == ANIM_DIRECTION_HORIZONTAL) {
                    mMatrix.setTranslate((animation.animatedValue as Float), 0.0f)
                }
                imageMatrix = mMatrix
                invalidate()
            }
        }

    }

    private fun parseAttributes(context: Context, attrs: AttributeSet) {
        val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.MultiToggleImageButton,
                0, 0)
        val imageIds = a.getResourceId(R.styleable.MultiToggleImageButton_imageIds, 0)
        if (imageIds > 0) {
            overrideImageIds(imageIds)
        }
        a.recycle()
    }

    /**
     * Override the image ids of this button.
     */
    fun overrideImageIds(resId: Int) {
        var ids: TypedArray? = null
        try {
            ids = resources.obtainTypedArray(resId)
            mImageIds = IntArray(ids.length())
            for (i in 0 until ids.length()) {
                mImageIds!![i] = ids.getResourceId(i, 0)
            }
        } finally {
            ids?.recycle()
        }
    }

    /**
     * Set size info (either width or height, as necessary) of the view containing
     * this button. Used for offset calculations during animation.
     * @param s The size.
     */
    fun setParentSize(s: Int) {
        mParentSize = s
    }

    /**
     * Set the animation direction.
     * @param d Either ANIM_DIRECTION_VERTICAL or ANIM_DIRECTION_HORIZONTAL.
     */
    fun setAnimDirection(d: Int) {
        mAnimDirection = d
    }

    override fun setImageLevel(level: Int) {
        super.setImageLevel(level)
        mLevel = level
    }

    private fun setImageByState(state: Int) {
        if (mImageIds != null) {
            setImageResource(mImageIds!![state])
        }
        super.setImageLevel(mLevel)
    }

    private fun combine(oldState: Int, newState: Int): Bitmap? {
        // in some cases, a new set of image Ids are set via overrideImageIds()
        // and oldState overruns the array.
        // check here for that.
        if (oldState >= mImageIds!!.size) {
            return null
        }
        val width = width
        val height = height
        if (width <= 0 || height <= 0) {
            return null
        }
        val enabledState = intArrayOf(android.R.attr.state_enabled)

        // new state
        val newDrawable = resources.getDrawable(mImageIds!![newState]).mutate()
        newDrawable.state = enabledState

        // old state
        val oldDrawable = resources.getDrawable(mImageIds!![oldState]).mutate()
        oldDrawable.state = enabledState

        // combine 'em
        var bitmap: Bitmap? = null
        if (mAnimDirection == ANIM_DIRECTION_VERTICAL) {
            val bitmapHeight = height * 2 + (mParentSize - height) / 2
            val oldBitmapOffset = height + (mParentSize - height) / 2
            bitmap = Bitmap.createBitmap(width, bitmapHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            newDrawable.setBounds(0, 0, newDrawable.intrinsicWidth, newDrawable.intrinsicHeight)
            oldDrawable.setBounds(0, oldBitmapOffset, oldDrawable.intrinsicWidth, oldDrawable.intrinsicHeight + oldBitmapOffset)
            newDrawable.draw(canvas)
            oldDrawable.draw(canvas)
        } else if (mAnimDirection == ANIM_DIRECTION_HORIZONTAL) {
            val bitmapWidth = width * 2 + (mParentSize - width) / 2
            val oldBitmapOffset = width + (mParentSize - width) / 2
            bitmap = Bitmap.createBitmap(bitmapWidth, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            newDrawable.setBounds(0, 0, newDrawable.intrinsicWidth, newDrawable.intrinsicHeight)
            oldDrawable.setBounds(oldBitmapOffset, 0, oldDrawable.intrinsicWidth + oldBitmapOffset, oldDrawable.intrinsicHeight)
            newDrawable.draw(canvas)
            oldDrawable.draw(canvas)
        }
        return bitmap
    }

    companion object {
        const val ANIM_DIRECTION_VERTICAL = 0
        const val ANIM_DIRECTION_HORIZONTAL = 1
        private const val ANIM_DURATION_MS = 250
        private const val UNSET = -1
    }
}