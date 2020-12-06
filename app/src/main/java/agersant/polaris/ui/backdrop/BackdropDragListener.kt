package agersant.polaris.ui.backdrop

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View

interface BackdropDragListener {
    fun onDragStart(event: MotionEvent)
    fun onDrag(event: MotionEvent)
    fun onDragEnd(event: MotionEvent)
}

@SuppressLint("ClickableViewAccessibility")
fun View.addBackdropDragListener(listener: BackdropDragListener) {
    setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> listener.onDragStart(event)
            MotionEvent.ACTION_MOVE -> listener.onDrag(event)
            MotionEvent.ACTION_UP -> listener.onDragEnd(event)
            MotionEvent.ACTION_CANCEL -> listener.onDragEnd(event)
            else -> Unit
        }
        false
    }
}
