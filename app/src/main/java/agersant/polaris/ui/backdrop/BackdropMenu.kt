package agersant.polaris.ui.backdrop

import agersant.polaris.R
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import androidx.customview.widget.Openable
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.navigation.NavController
import com.google.android.material.navigation.NavigationView

class BackdropMenu(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs), Openable, BackdropDragListener {

    private val wrapContentMeasureSpec = MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    private var isOpen = false
    private var toolbar: Toolbar? = null
    private var toolbarIcon: Drawable? = null

    private var backdropLayoutId: Int? = null
    private val backdropLayout: BackdropLayout? by lazy {
        backdropLayoutId ?: return@lazy null
        val layout: BackdropLayout? = (parent.parent as ViewGroup).findViewById(backdropLayoutId!!) ?: null
        layout?.attachBackdropMenu(this)
        layout
    }

    init {
        alpha = 0f

        val arr = context.obtainStyledAttributes(attrs, R.styleable.BackdropMenu)
        backdropLayoutId = arr.getResourceId(R.styleable.BackdropMenu_backdropLayout, -1)
        arr.recycle()

        addBackdropDragListener(this)
    }

    override fun onViewAdded(child: View?) {
        if (child is NavigationView) {
            child.children.forEach {
                it.addBackdropDragListener(this)
            }
        }
    }

    fun setUpWith(navController: NavController, toolbar: Toolbar) {
        this.toolbar = toolbar
        toolbar.addBackdropDragListener(this)
        navController.addOnDestinationChangedListener { _, _, _ ->
            close()
        }
    }

    private val springAnim: SpringAnimation by lazy {
        SpringAnimation(backdropLayout, DynamicAnimation.TRANSLATION_Y, 0f).apply {
            spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
            spring.stiffness = 500f
            addUpdateListener { _, value, _ -> updatePos(value / measuredHeight) }
        }
    }
    private lateinit var velocityTracker: VelocityTracker
    private var initialValue = 0f
    private var initialPos = 0f

    override fun onDragStart(event: MotionEvent) {
        velocityTracker = VelocityTracker.obtain()
        initialValue = alpha
        initialPos = event.y
        measure(wrapContentMeasureSpec, wrapContentMeasureSpec)
    }

    override fun onDrag(event: MotionEvent) {
        springAnim.cancel()
        velocityTracker.addMovement(event)
        val distance = event.y - initialPos + initialValue * measuredHeight

        when {
            distance < 0 -> setPos(0f)
            distance > measuredHeight -> setPos(1f)
            else -> setPos(distance / measuredHeight.toFloat())
        }
    }

    override fun onDragEnd(event: MotionEvent) {
        if (alpha == 0f) return setClosed()
        else if (alpha == 1f) return setOpened()

        val distance = event.y - initialPos
        val bias = if (isOpen) 0.05f else -0.05f
        val relativeDistance = (distance / measuredHeight.toFloat()) - 0.5f
        velocityTracker.computeCurrentVelocity(1000)
        val relativeVelocity = (velocityTracker.yVelocity / measuredHeight.toFloat()) * 0.1

        if (bias + relativeDistance + relativeVelocity > 0) {
            animateToOpenedPos(velocityTracker.yVelocity)
        } else {
            animateToClosedPos(velocityTracker.yVelocity)
        }
    }

    override fun isOpen(): Boolean {
        return isOpen
    }

    override fun open() {
        if (isOpen) {
            animateToClosedPos()
        } else {
            animateToOpenedPos()
        }
    }

    override fun close() {
        if (!isOpen) return

        animateToClosedPos()
    }

    private fun setOpened() {
        toolbarIcon = toolbar?.navigationIcon
        toolbar?.setNavigationIcon(R.drawable.baseline_close_24)
        isOpen = true
    }

    private fun animateToOpenedPos(initialVelocity: Float = 0f) {
        setOpened()

        if (initialVelocity != 0f) springAnim.setStartVelocity(initialVelocity)
        springAnim.animateToFinalPosition(measuredHeight.toFloat())
    }

    private fun setClosed() {
        toolbarIcon?.let { toolbar?.navigationIcon = it }
        isOpen = false
    }

    private fun animateToClosedPos(initialVelocity: Float = 0f) {
        setClosed()

        if (initialVelocity != 0f) springAnim.setStartVelocity(initialVelocity)
        springAnim.animateToFinalPosition(0f)
    }

    private fun setPos(interpolatedValue: Float) {
        visibility = if (interpolatedValue == 0f) GONE
        else View.VISIBLE

        alpha = interpolatedValue
        backdropLayout?.setPos(interpolatedValue)
    }

    private fun updatePos(interpolatedValue: Float) {
        visibility = if (interpolatedValue == 0f) GONE
        else View.VISIBLE

        alpha = interpolatedValue
        backdropLayout?.updatePos(interpolatedValue)
    }
}
