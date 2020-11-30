package agersant.polaris.anim

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

class ExpandAnimation(private vararg val views: View) : Animation() {

    private val wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    private val initialHeights = IntArray(views.size)
    private val targetHeights = IntArray(views.size)

    init {
        views.forEachIndexed { index, view ->
            view.measure(wrapContentMeasureSpec, wrapContentMeasureSpec)
            initialHeights[index] = view.layoutParams.height
            targetHeights[index] = view.measuredHeight
        }
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        for (i in views.indices) {
            val difference: Int = targetHeights[i] - initialHeights[i]
            views[i].layoutParams.height =
                initialHeights[i] + (difference * interpolatedTime).toInt()
            views[i].requestLayout()
        }
        views.forEachIndexed { index, view ->
            val difference: Int = targetHeights[index] - initialHeights[index]
            view.layoutParams.height = initialHeights[index] + (difference * interpolatedTime).toInt()
            view.requestLayout()
        }
    }

    override fun willChangeBounds(): Boolean {
        return true
    }
}

class CollapseAnimation(private vararg val views: View) : Animation() {

    private var initialHeights = views.map { it.measuredHeight }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        if (interpolatedTime == 1f) {
            views.forEach {
                it.visibility = View.GONE
                it.layoutParams.height = 0
            }
        } else {
            views.forEachIndexed { index, view ->
                view.layoutParams.height = (initialHeights[index] * (1 - interpolatedTime)).toInt()
            }
        }
        views.forEach { it.requestLayout() }
    }

    override fun willChangeBounds(): Boolean {
        return true
    }
}
