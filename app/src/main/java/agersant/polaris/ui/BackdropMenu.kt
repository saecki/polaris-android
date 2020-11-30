package agersant.polaris.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.customview.widget.Openable

class BackdropMenu(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs), Openable {
    private var isOpen = false

    init {
        visibility = View.GONE
    }

    override fun isOpen(): Boolean {
        return isOpen
    }

    override fun open() {
        isOpen = true
        visibility = View.VISIBLE
    }

    override fun close() {
        isOpen = false
        visibility = View.GONE
    }
}