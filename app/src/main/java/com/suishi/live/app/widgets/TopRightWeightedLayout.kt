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

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.suishi.live.app.widgets.MultiToggleImageButton
import java.util.*

/**
 * TopRightWeightedLayout is a LinearLayout that reorders its
 * children such that the right most child is the top most child
 * on an orientation change.
 */
class TopRightWeightedLayout(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    public override fun onFinishInflate() {
        super.onFinishInflate()
        val configuration = context.resources.configuration
        checkOrientation(configuration.orientation)
    }

    public override fun onConfigurationChanged(configuration: Configuration) {
        super.onConfigurationChanged(configuration)
        checkOrientation(configuration.orientation)
    }

    /**
     * Set the orientation of this layout if it has changed,
     * and center the elements based on the new orientation.
     */
    private fun checkOrientation(orientation: Int) {
        val isHorizontal = HORIZONTAL == getOrientation()
        val isPortrait = Configuration.ORIENTATION_PORTRAIT == orientation
        if (isPortrait && !isHorizontal) {
            // Portrait orientation is out of sync, setting to horizontal
            // and reversing children
            fixGravityAndPadding(HORIZONTAL)
            setOrientation(HORIZONTAL)
            reverseChildren()
            requestLayout()
        } else if (!isPortrait && isHorizontal) {
            // Landscape orientation is out of sync, setting to vertical
            // and reversing children
            fixGravityAndPadding(VERTICAL)
            setOrientation(VERTICAL)
            reverseChildren()
            requestLayout()
        }
    }

    /**
     * Reverse the ordering of the children in this layout.
     * Note: bringChildToFront produced a non-deterministic ordering.
     */
    private fun reverseChildren() {
        val children: MutableList<View> = ArrayList()
        for (i in childCount - 1 downTo 0) {
            children.add(getChildAt(i))
        }
        for (v in children) {
            bringChildToFront(v)
        }
    }

    /**
     * Swap gravity:
     * left for bottom
     * right for top
     * center horizontal for center vertical
     * etc
     *
     * also swap left|right padding for bottom|top
     */
    private fun fixGravityAndPadding(direction: Int) {
        for (i in 0 until childCount) {
            // gravity swap
            val v = getChildAt(i)
            val layoutParams = v.layoutParams as LayoutParams
            var gravity = layoutParams.gravity
            if (direction == VERTICAL) {
                if (gravity and Gravity.LEFT != 0) {   // if gravity left is set . . .
                    gravity = gravity and Gravity.LEFT.inv() // unset left
                    gravity = gravity or Gravity.BOTTOM // and set bottom
                }
            } else {
                if (gravity and Gravity.BOTTOM != 0) { // etc
                    gravity = gravity and Gravity.BOTTOM.inv()
                    gravity = gravity or Gravity.LEFT
                }
            }
            if (direction == VERTICAL) {
                if (gravity and Gravity.RIGHT != 0) {
                    gravity = gravity and Gravity.RIGHT.inv()
                    gravity = gravity or Gravity.TOP
                }
            } else {
                if (gravity and Gravity.TOP != 0) {
                    gravity = gravity and Gravity.TOP.inv()
                    gravity = gravity or Gravity.RIGHT
                }
            }

            // don't mess with children that are centered in both directions
            if (gravity and Gravity.CENTER != Gravity.CENTER) {
                if (direction == VERTICAL) {
                    if (gravity and Gravity.CENTER_VERTICAL != 0) {
                        gravity = gravity and Gravity.CENTER_VERTICAL.inv()
                        gravity = gravity or Gravity.CENTER_HORIZONTAL
                    }
                } else {
                    if (gravity and Gravity.CENTER_HORIZONTAL != 0) {
                        gravity = gravity and Gravity.CENTER_HORIZONTAL.inv()
                        gravity = gravity or Gravity.CENTER_VERTICAL
                    }
                }
            }
            layoutParams.gravity = gravity

            // padding swap
            val paddingLeft = v.paddingLeft
            val paddingTop = v.paddingTop
            val paddingRight = v.paddingRight
            val paddingBottom = v.paddingBottom
            v.setPadding(paddingBottom, paddingRight, paddingTop, paddingLeft)
        }
    }

    public override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val mIsPortrait = resources.configuration.orientation ==
                Configuration.ORIENTATION_PORTRAIT
        val size = if (mIsPortrait) height else width
        for (i in 0 until childCount) {
            val button = getChildAt(i)
            if (button is MultiToggleImageButton) {
                val toggleButton = button
                toggleButton.setParentSize(size)
                toggleButton.setAnimDirection(if (mIsPortrait) MultiToggleImageButton.Companion.ANIM_DIRECTION_VERTICAL else MultiToggleImageButton.Companion.ANIM_DIRECTION_HORIZONTAL)
            }
        }
    }
}