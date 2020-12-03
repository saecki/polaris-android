package agersant.polaris

import android.animation.Animator
import android.content.Context
import android.content.SharedPreferences
import android.view.ViewPropertyAnimator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.view.postDelayed
import java.util.*

fun SharedPreferences.intString(key: String, default: Int): Int {
    return try {
        val str = this.getString(key, null)
        str!!.toInt()
    } catch (e: Exception) {
        default
    }
}

fun EditText.showKeyboard() {
    requestFocus()
    postDelayed(150) {
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun ViewPropertyAnimator.setOnFinished(onFinished: () -> Unit): ViewPropertyAnimator {
    this.setListener(object : Animator.AnimatorListener {
        private var canceled = false
        override fun onAnimationCancel(animation: Animator?) {
            canceled = true
        }

        override fun onAnimationEnd(animation: Animator?) {
            if (!canceled) onFinished()
        }

        override fun onAnimationStart(animation: Animator?) = Unit
        override fun onAnimationRepeat(animation: Animator?) = Unit
    })

    return this
}
