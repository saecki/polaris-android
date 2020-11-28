package agersant.polaris.features.queue

import agersant.polaris.R
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintSet

class QueueItemView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private val IS_PLAYING = intArrayOf(R.attr.state_is_playing)
    }

    private var isPlaying = false

    init {
        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        if (attrs != null)
            readAttributes(context, attrs)
        fill()
    }

    private fun fill() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_queue_item, this, true)
    }

    private fun readAttributes(context: Context, attrs: AttributeSet?) {
        val arr = context.obtainStyledAttributes(attrs, R.styleable.QueueItemView)
        val isPlayingArg = arr.getBoolean(R.styleable.QueueItemView_state_is_playing, true)
        setIsPlaying(isPlayingArg)
        arr.recycle()
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isPlaying) {
            mergeDrawableStates(drawableState, IS_PLAYING)
        }
        return drawableState
    }

    fun setIsPlaying(isPlaying: Boolean) {
        this.isPlaying = isPlaying
        refreshDrawableState()
    }
}