package agersant.polaris.ui.backdrop

import agersant.polaris.R
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat

class BackdropLayout(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {

    inner class OverlayView(context: Context) : View(context) {
        init {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            alpha = 0f
            visibility = GONE
            z = 100f
            background = ResourcesCompat.getDrawable(resources, R.drawable.content_background, context.theme)

            setOnClickListener { backdropMenu?.close() }
        }
    }

    private var backdropMenu: BackdropMenu? = null
    val backdropOverlay = OverlayView(context)

    init {
        addView(backdropOverlay)
        backdropOverlay.z
    }

    fun attachBackdropMenu(backdropMenu: BackdropMenu) {
        this.backdropMenu = backdropMenu
    }

    fun updatePos(interpolatedValue: Float) {
        backdropOverlay.visibility = if (interpolatedValue == 0f) GONE
        else VISIBLE

        backdropOverlay.alpha = interpolatedValue * 0.5f
    }

    fun setPos(interpolatedValue: Float) {
        updatePos(interpolatedValue)
        translationY = interpolatedValue * backdropMenu!!.measuredHeight
    }
}