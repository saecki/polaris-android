package agersant.polaris.features

import agersant.polaris.R
import agersant.polaris.features.browse.CollectionActivity
import agersant.polaris.features.player.PlayerActivity
import agersant.polaris.features.queue.QueueActivity
import agersant.polaris.features.settings.SettingsActivity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

abstract class PolarisActivity(private val title: Int, private val navigationItem: Int) : AppCompatActivity() {
    @JvmField protected var toolbar: Toolbar? = null
    private var navigationView: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar = findViewById(R.id.toolbar)
        toolbar?.setTitle(title)
        setSupportActionBar(toolbar)
        val that = this
        navigationView = findViewById(R.id.navigation)
        navigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item: MenuItem -> that.onNavigationItemSelected(item) })
    }

    public override fun onResume() {
        super.onResume()
        highlightNavigationTab()
    }

    private fun highlightNavigationTab() {
        val menu = navigationView!!.menu
        for (i in 0 until menu.size()) {
            menu.getItem(i).isChecked = false
        }
        menu.findItem(navigationItem).isChecked = true
    }

    public override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        overridePendingTransition(0, 0)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    private fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_collection -> {
                openCollection()
                return true
            }
            R.id.nav_queue -> {
                openQueue()
                return true
            }
            R.id.nav_now_playing -> {
                openPlayer()
                return true
            }
        }
        return false
    }

    private fun openCollection() {
        val intent = Intent(this, CollectionActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
    }

    private fun openQueue() {
        val intent = Intent(this, QueueActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
    }

    private fun openPlayer() {
        val intent = Intent(this, PlayerActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}