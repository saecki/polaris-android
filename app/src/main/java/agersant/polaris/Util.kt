package agersant.polaris

import android.content.SharedPreferences
import java.lang.Exception

fun SharedPreferences.intString(key: String, default: Int): Int {
    return try {
        val str = this.getString(key, null)
        str!!.toInt()
    } catch (e: Exception) {
        default
    }
}


