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

package com.stfalcon.imageviewer.viewer.adapter

import android.content.Context
import android.view.ViewGroup
import com.github.chrisbanes.photoview.PhotoView
import com.stfalcon.imageviewer.common.extensions.resetScale
import com.stfalcon.imageviewer.common.pager.RecyclingPagerAdapter
import com.stfalcon.imageviewer.loader.ImageLoader

internal class ImagesPagerAdapter<T>(
    private val context: Context,
    private var images: List<T>,
    private val imageLoader: ImageLoader<T>,
    private val isZoomingAllowed: Boolean
) : RecyclingPagerAdapter<ImagesPagerAdapter<T>.ViewHolder>() {

    private val holders = mutableListOf<ViewHolder>()

    fun isScaled(position: Int): Boolean =
        holders.firstOrNull { it.position == position }?.isScaled == true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val photoView = PhotoView(context).apply {
            isEnabled = isZoomingAllowed
            setOnViewDragListener { _, _ -> setAllowParentInterceptOnEdge(scale == 1.0f) }
        }

        return ViewHolder(photoView).also(holders::add)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)

    override fun getItemCount(): Int = images.size

    internal fun updateImages(images: List<T>) {
        this.images = images
        notifyDataSetChanged()
    }

    internal fun resetScale(position: Int) =
        holders.firstOrNull { it.position == position }?.resetScale()

    internal inner class ViewHolder(private val photoView: PhotoView) :
        RecyclingPagerAdapter.ViewHolder(photoView) {

        internal val isScaled: Boolean
            get() = photoView.scale > 1f

        internal fun bind(position: Int) {
            this.position = position
            imageLoader.loadImage(photoView, images[position])
        }

        internal fun resetScale() = photoView.resetScale(true)
    }
}