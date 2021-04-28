package com.stfalcon.sample.common.ui.base

import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.stfalcon.sample.common.models.Poster

abstract class BaseActivity : AppCompatActivity() {

    protected open fun loadPosterImage(imageView: ImageView, poster: Poster?) {
        imageView.load(poster?.url)
    }
}