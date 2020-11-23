package agersant.polaris

import agersant.polaris.databinding.ActivityMainBinding
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.instance = this
        App.state = PolarisState(this)
        App.resources = resources

        val playbackServiceIntent = Intent(this, PolarisPlaybackService::class.java)
        playbackServiceIntent.action = PolarisPlaybackService.APP_INTENT_COLD_BOOT
        startService(playbackServiceIntent)
        startService(Intent(this, PolarisDownloadService::class.java))

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_collection,
                R.id.nav_queue,
                R.id.nav_now_playing,
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navigation.setupWithNavController(navController)
    }
}