package agersant.polaris

import android.content.SharedPreferences
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar

object App {
    lateinit var instance: PolarisApplication
    lateinit var preferences: SharedPreferences
    lateinit var resources: Resources
    lateinit var state: PolarisState
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
                    values()[value.toString().toInt()]
                } catch (e: Throwable) {
                    System
                }
            }
        }
    }

    enum class StartScreen(val id: Int) {
        Collection(R.id.collection),
        Playlists(R.id.playlists),
        NowPlaying(R.id.now_playing),
        Queue(R.id.queue),
        Search(R.id.search);
    }
}

