package agersant.polaris.ui

import agersant.polaris.R
import agersant.polaris.anim.CollapseAnimation
import agersant.polaris.anim.ExpandAnimation
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.postDelayed
import androidx.core.view.contains
import androidx.customview.widget.Openable
import androidx.navigation.NavController

class BackdropLayout(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {

    inner class OverlayView(context: Context) : View(context) {
        init {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            alpha = 0.5f
            elevation = 100f
            background = ResourcesCompat.getDrawable(resources, R.drawable.content_background, context.theme)

            setOnClickListener { backdropMenu?.close() }
        }
    }

    private var backdropMenu: BackdropMenu? = null
    private val backdropOverlay: OverlayView by lazy { OverlayView(context) }

    fun attachBackdropMenu(backdropMenu: BackdropMenu) {
        this.backdropMenu = backdropMenu
    }

    fun open(animate: Boolean = true) {
        if (backdropOverlay !in this) {
            addView(backdropOverlay)
        }
        backdropOverlay.visibility = View.VISIBLE
        if (animate) {
            backdropOverlay.animate().setDuration(300).alpha(0.5f).start()
        } else {
            backdropOverlay.alpha = 0.5f
        }
    }

    fun close(animate: Boolean = true) {
        if (animate) {
            backdropOverlay.animate().setDuration(300).alpha(0f).start()
            handler.postDelayed(300) {
                backdropOverlay.visibility = View.GONE
            }
        } else {
            backdropOverlay.alpha = 0f
            backdropOverlay.visibility = View.GONE
        }
    }
}


class BackdropMenu(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs), Openable {

    private var isOpen = false
    private var navController: NavController? = null

    private var backdropLayoutId: Int? = null
    private val backdropLayout: BackdropLayout? by lazy {
        backdropLayoutId ?: return@lazy null
        val layout: BackdropLayout? = (parent as ViewGroup).findViewById(backdropLayoutId!!) ?: null
        layout?.attachBackdropMenu(this)
        Log.d("TESTING", "layout: $layout")

        layout
    }

    private val collapse: CollapseAnimation by lazy { CollapseAnimation(this).apply { duration = 300 } }
    private val expand: ExpandAnimation by lazy { ExpandAnimation(this).apply { duration = 300 } }

    init {
        visibility = GONE

        val arr = context.obtainStyledAttributes(attrs, R.styleable.BackdropMenu)
        backdropLayoutId = arr.getResourceId(R.styleable.BackdropMenu_backdropLayout, -1)
        arr.recycle()

        Log.d("TESTING", "backdropLayoutId: $backdropLayoutId")
    }

    fun setUpWithNavController(navController: NavController) {
        this.navController = navController
        navController.addOnDestinationChangedListener { _, _, _ ->
            closeBackdrop()
        }
    }

    override fun isOpen(): Boolean {
        return isOpen
    }

    override fun open() {
        openBackdrop()
    }

    fun openBackdrop(animate: Boolean = true) { //TODO: animate
        if (isOpen) {
            closeBackdrop()
        } else {
            isOpen = true

            visibility = View.VISIBLE
            if (animate) {
                startAnimation(expand)
            } else {
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            }
            backdropLayout?.open(animate)
        }
    }

    override fun close() {
        closeBackdrop()
    }

    fun closeBackdrop(animate: Boolean = true) { //TODO: animate
        if (!isOpen) return

        isOpen = false

        if (animate) {
            startAnimation(collapse)
        } else {
            layoutParams.height = 0
            visibility = View.GONE
        }
        backdropLayout?.close(animate)
    }
}
