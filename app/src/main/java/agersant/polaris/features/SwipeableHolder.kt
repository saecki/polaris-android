package agersant.polaris.features

import android.graphics.Canvas

interface SwipeableHolder {
    fun onSwiped(direction: Int)

    fun onChildDraw(canvas: Canvas, dX: Float, actionState: Int)
}
