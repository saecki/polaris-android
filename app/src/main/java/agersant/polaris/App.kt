package agersant.polaris

import android.content.SharedPreferences
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.AppBarLayout

object App {
    lateinit var preferences: SharedPreferences
    lateinit var resources: Resources
    lateinit var instance: PolarisApplication
    lateinit var state: PolarisState
    lateinit var appBarLayout: AppBarLayout
    lateinit var toolbar: Toolbar

    var theme: Theme
        get() {
            val value = preferences.getString(resources.getString(R.string.pref_key_theme), null)
            return Theme.tryFrom(value)
        }
        set(value) {
            when (value) {
                Theme.System -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                Theme.Dark -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Theme.Light -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

    enum class Theme {
        System,
        Dark,
        Light;

        companion object {
            fun tryFrom(value: Any?): Theme {
                return try {
                    Theme.values()[value.toString().toInt()]
                } catch (e: Throwable) {
                    Theme.System
                }
            }
        }
    }
}

