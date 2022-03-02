package com.stfalcon.sample.features.demo.grid

import android.os.Bundle
import android.widget.ImageView
import com.stfalcon.imageviewer.StfalconImageViewer
import com.stfalcon.sample.R
import com.stfalcon.sample.common.extensions.getDrawableCompat
import com.stfalcon.sample.common.models.Demo
import com.stfalcon.sample.common.models.Poster
import com.stfalcon.sample.common.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_demo_posters_grid.*

class PostersGridDemoActivity : BaseActivity() {

    private lateinit var viewer: StfalconImageViewer<Poster>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo_posters_grid)

        postersGridView.apply {
            imageLoader = ::loadPosterImage
            onPosterClick = ::openViewer
        }
    }

    private fun openViewer(startPosition: Int, target: ImageView) {
        viewer = StfalconImageViewer.Builder(this, Demo.posters, ::loadPosterImage)
            .withStartPosition(startPosition)
            .withTransitionFrom(target)
            .withImageChangeListener {
                viewer.updateTransitionImage(postersGridView.imageViews[it])
            }
            .show()
    }

    override fun loadPosterImage(imageView: ImageView, poster: Poster?) {
        imageView.background = getDrawableCompat(R.drawable.shape_placeholder)
        super.loadPosterImage(imageView, poster)
    }
}
