/*
 * Copyright 2018 stfalcon.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stfalcon.imageviewer

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.IntRange
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import com.stfalcon.imageviewer.listeners.OnDismissListener
import com.stfalcon.imageviewer.listeners.OnImageChangeListener
import com.stfalcon.imageviewer.loader.ImageLoader
import com.stfalcon.imageviewer.viewer.builder.BuilderData
import com.stfalcon.imageviewer.viewer.dialog.ImageViewerDialog
import kotlin.math.roundToInt

@Suppress("MemberVisibilityCanBePrivate", "unused")
class StfalconImageViewer<T> private constructor(
    context: Context,
    private val builderData: BuilderData<T>
) {
    private val dialog = ImageViewerDialog(context, builderData)

    /**
     * Displays the built viewer if passed list of images is not empty
     *
     * @param animate whether the passed transition view should be animated on open. Useful for screen rotation handling.
     */
    @JvmOverloads
    fun show(animate: Boolean = true) {
        if (builderData.images.isNotEmpty()) {
            dialog.show(animate)
        }
    }

    /**
     * Closes the viewer with suitable close animation
     */
    fun close() {
        dialog.close()
    }

    /**
     * Dismisses the dialog with no animation
     */
    fun dismiss() {
        dialog.dismiss()
    }

    /**
     * Updates an existing images list if a new list is not empty, otherwise closes the viewer
     */
    fun updateImages(images: Array<T>) {
        updateImages(images.toList())
    }

    /**
     * Updates an existing images list if a new list is not empty, otherwise closes the viewer
     */
    fun updateImages(images: List<T>) {
        if (images.isNotEmpty()) {
            dialog.updateImages(images)
        } else {
            dialog.close()
        }
    }

    fun currentPosition(): Int {
        return dialog.getCurrentPosition()
    }

    fun setCurrentPosition(@IntRange(from = 0) position: Int): Int {
        return dialog.setCurrentPosition(position)
    }

    /**
     * Updates transition image view.
     * Useful for a case when image position has changed and you want to update the transition animation target.
     */
    fun updateTransitionImage(imageView: ImageView?) {
        dialog.updateTransitionImage(imageView)
    }

    /**
     * Builder class for [StfalconImageViewer]
     */
    class Builder<T>(private val context: Context, images: List<T>, imageLoader: ImageLoader<T>) {
        private val data: BuilderData<T> = BuilderData(images, imageLoader)

        constructor(context: Context, images: Array<T>, imageLoader: ImageLoader<T>) : this(
            context, images.toList(), imageLoader
        )

        /**
         * Sets a position to start viewer from.
         *
         * @return This Builder object to allow calls chaining
         */
        fun withStartPosition(@IntRange(from = 0) position: Int): Builder<T> = apply {
            data.startPosition = position
        }

        /**
         * Sets a background color value for the viewer
         *
         * @return This Builder object to allow calls chaining
         */
        fun withBackgroundColor(@ColorInt color: Int): Builder<T> = apply {
            data.backgroundColor = color
        }

        /**
         * Sets a background color resource for the viewer
         *
         * @return This Builder object to allow calls chaining
         */
        fun withBackgroundColorResource(@ColorRes color: Int): Builder<T> =
            withBackgroundColor(ContextCompat.getColor(context, color))

        /**
         * Sets custom overlay view to be shown over the viewer.
         * Commonly used for image description or counter displaying.
         *
         * @return This Builder object to allow calls chaining
         */
        fun withOverlayView(view: View?): Builder<T> = apply {
            data.overlayView = view
        }

        /**
         * Sets space between the images using dimension.
         *
         * @return This Builder object to allow calls chaining
         */
        fun withImagesMargin(@DimenRes dimen: Int): Builder<T> = apply {
            data.imageMarginPixels = context.resources.getDimension(dimen).roundToInt()
        }

        /**
         * Sets space between the images in pixels.
         *
         * @return This Builder object to allow calls chaining
         */
        fun withImageMarginPixels(@Px marginPixels: Int): Builder<T> = apply {
            data.imageMarginPixels = marginPixels
        }

        /**
         * Sets overall padding for zooming and scrolling area using dimension.
         *
         * @return This Builder object to allow calls chaining
         */
        fun withContainerPadding(@DimenRes padding: Int): Builder<T> {
            @Px val paddingPx = context.resources.getDimension(padding).roundToInt()
            return withContainerPaddingPixels(paddingPx, paddingPx, paddingPx, paddingPx)
        }

        /**
         * Sets `start`, `top`, `end` and `bottom` padding for zooming and scrolling area using dimension.
         *
         * @return This Builder object to allow calls chaining
         */
        fun withContainerPadding(
            @DimenRes start: Int,
            @DimenRes top: Int,
            @DimenRes end: Int,
            @DimenRes bottom: Int
        ): Builder<T> = withContainerPaddingPixels(
            context.resources.getDimension(start).roundToInt(),
            context.resources.getDimension(top).roundToInt(),
            context.resources.getDimension(end).roundToInt(),
            context.resources.getDimension(bottom).roundToInt()
        )

        /**
         * Sets overall padding for zooming and scrolling area in pixels.
         *
         * @return This Builder object to allow calls chaining
         */
        fun withContainerPaddingPixels(@Px padding: Int): Builder<T> = apply {
            data.containerPaddingPixels = intArrayOf(padding, padding, padding, padding)
        }

        /**
         * Sets `start`, `top`, `end` and `bottom` padding for zooming and scrolling area in pixels.
         *
         * @return This Builder object to allow calls chaining
         */
        fun withContainerPaddingPixels(
            @Px start: Int,
            @Px top: Int,
            @Px end: Int,
            @Px bottom: Int
        ): Builder<T> = apply {
            data.containerPaddingPixels = intArrayOf(start, top, end, bottom)
        }

        /**
         * Sets status bar visibility. True by default.
         *
         * @return This Builder object to allow calls chaining
         */
        fun withHiddenStatusBar(value: Boolean): Builder<T> = apply {
            data.shouldStatusBarHide = value
        }

        /**
         * Enables or disables zooming. True by default.
         *
         * @return This Builder object to allow calls chaining
         */
        fun allowZooming(value: Boolean): Builder<T> = apply {
            data.isZoomingAllowed = value
        }

        /**
         * Enables or disables the "Swipe to Dismiss" gesture. True by default.
         *
         * @return This Builder object to allow calls chaining
         */
        fun allowSwipeToDismiss(value: Boolean): Builder<T> = apply {
            data.isSwipeToDismissAllowed = value
        }

        /**
         * Sets a target [ImageView] to be part of transition when opening or closing the viewer/
         *
         * @return This Builder object to allow calls chaining
         */
        fun withTransitionFrom(imageView: ImageView?): Builder<T> = apply {
            data.transitionView = imageView
        }

        /**
         * Sets [OnImageChangeListener] for the viewer.
         *
         * @return This Builder object to allow calls chaining
         */
        fun withImageChangeListener(imageChangeListener: OnImageChangeListener?): Builder<T> =
            apply {
                data.imageChangeListener = imageChangeListener
            }

        /**
         * Sets [OnDismissListener] for viewer.
         *
         * @return This Builder object to allow calls chaining
         */
        fun withDismissListener(onDismissListener: OnDismissListener?): Builder<T> = apply {
            data.onDismissListener = onDismissListener
        }

        /**
         * Creates a [StfalconImageViewer] with the arguments supplied to this builder. It does not
         * show the dialog. This allows the user to do any extra processing
         * before displaying the dialog. Use [.show] if you don't have any other processing
         * to do and want this to be created and displayed.
         */
        fun build(): StfalconImageViewer<T> = StfalconImageViewer(context, data)

        /**
         * Creates the [StfalconImageViewer] with the arguments supplied to this builder and
         * shows the dialog.
         *
         * @param animate whether the passed transition view should be animated on open. Useful for screen rotation handling.
         */
        @JvmOverloads
        fun show(animate: Boolean = true): StfalconImageViewer<T> = build().also {
            it.show(animate)
        }
    }
}