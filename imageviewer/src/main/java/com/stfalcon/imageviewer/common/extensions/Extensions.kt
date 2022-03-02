package com.stfalcon.imageviewer.common.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.util.SparseArray
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.widget.ImageView
import androidx.transition.Transition
import androidx.viewpager.widget.ViewPager
import com.github.chrisbanes.photoview.PhotoView

internal fun ImageView.copyBitmapFrom(target: ImageView?) {
    target?.drawable?.let {
        if (it is BitmapDrawable) {
            setImageBitmap(it.bitmap)
        }
    }
}

internal fun PhotoView.resetScale(animate: Boolean) {
    setScale(minimumScale, animate)
}

internal inline fun <T> SparseArray<T>.forEach(action: (key: Int, value: T) -> Unit) {
    for (index in 0 until size()) {
        action(keyAt(index), valueAt(index))
    }
}

internal fun Transition.addListener(
    onTransitionEnd: ((Transition) -> Unit)? = null,
    onTransitionResume: ((Transition) -> Unit)? = null,
    onTransitionPause: ((Transition) -> Unit)? = null,
    onTransitionCancel: ((Transition) -> Unit)? = null,
    onTransitionStart: ((Transition) -> Unit)? = null
) = addListener(
    object : Transition.TransitionListener {
        override fun onTransitionEnd(transition: Transition) {
            onTransitionEnd?.invoke(transition)
        }

        override fun onTransitionResume(transition: Transition) {
            onTransitionResume?.invoke(transition)
        }

        override fun onTransitionPause(transition: Transition) {
            onTransitionPause?.invoke(transition)
        }

        override fun onTransitionCancel(transition: Transition) {
            onTransitionCancel?.invoke(transition)
        }

        override fun onTransitionStart(transition: Transition) {
            onTransitionStart?.invoke(transition)
        }
    }
)

internal val View?.localVisibleRect: Rect
    get() = Rect().also { this?.getLocalVisibleRect(it) }

internal val View?.globalVisibleRect: Rect
    get() = Rect().also { this?.getGlobalVisibleRect(it) }

internal val View?.hitRect: Rect
    get() = Rect().also { this?.getHitRect(it) }

internal val View?.isRectVisible: Boolean
    get() = this != null && globalVisibleRect != localVisibleRect

internal val View?.isVisible: Boolean
    get() = this != null && visibility == View.VISIBLE

internal fun View.makeVisible() {
    visibility = View.VISIBLE
}

internal fun View.makeInvisible() {
    visibility = View.INVISIBLE
}

internal fun View.makeGone() {
    visibility = View.GONE
}

internal inline fun <T : View> T.postApply(crossinline block: T.() -> Unit) {
    post { apply(block) }
}

internal inline fun <T : View> T.postDelayed(delayMillis: Long, crossinline block: T.() -> Unit) {
    postDelayed({ block() }, delayMillis)
}

internal fun View.applyMargin(
    start: Int? = null,
    top: Int? = null,
    end: Int? = null,
    bottom: Int? = null
) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        layoutParams = (layoutParams as ViewGroup.MarginLayoutParams).apply {
            marginStart = start ?: marginStart
            topMargin = top ?: topMargin
            marginEnd = end ?: marginEnd
            bottomMargin = bottom ?: bottomMargin
        }
    }
}

internal fun View.requestNewSize(width: Int, height: Int) {
    layoutParams.width = width
    layoutParams.height = height
    layoutParams = layoutParams
}

internal fun View.makeViewMatchParent() {
    applyMargin(0, 0, 0, 0)
    requestNewSize(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
}

internal fun View.animateAlpha(from: Float?, to: Float?, duration: Long) {
    alpha = from ?: 0f
    clearAnimation()
    animate()
        .alpha(to ?: 0f)
        .setDuration(duration)
        .start()
}

internal fun View.switchVisibilityWithAnimation() {
    val isVisible = visibility == View.VISIBLE
    val from = if (isVisible) 1.0f else 0.0f
    val to = if (isVisible) 0.0f else 1.0f

    ObjectAnimator.ofFloat(this, "alpha", from, to).apply {
        duration = ViewConfiguration.getDoubleTapTimeout().toLong()

        if (isVisible) {
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    makeGone()
                }
            })
        } else {
            makeVisible()
        }

        start()
    }
}

internal fun ViewPager.addOnPageChangeListener(
    onPageScrolled: ((position: Int, offset: Float, offsetPixels: Int) -> Unit)? = null,
    onPageSelected: ((position: Int) -> Unit)? = null,
    onPageScrollStateChanged: ((state: Int) -> Unit)? = null
) = object : ViewPager.OnPageChangeListener {
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        onPageScrolled?.invoke(position, positionOffset, positionOffsetPixels)
    }

    override fun onPageSelected(position: Int) {
        onPageSelected?.invoke(position)
    }

    override fun onPageScrollStateChanged(state: Int) {
        onPageScrollStateChanged?.invoke(state)
    }
}.also(::addOnPageChangeListener)

internal fun ViewPropertyAnimator.setAnimatorListener(
    onAnimationEnd: ((Animator?) -> Unit)? = null,
    onAnimationStart: ((Animator?) -> Unit)? = null
) = this.setListener(
    object : AnimatorListenerAdapter() {

        override fun onAnimationEnd(animation: Animator?) {
            onAnimationEnd?.invoke(animation)
        }

        override fun onAnimationStart(animation: Animator?) {
            onAnimationStart?.invoke(animation)
        }
    }
)
