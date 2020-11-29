package agersant.polaris

import agersant.polaris.databinding.ActivityMainBinding
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController


class MainActivity : AppCompatActivity() {

    private var currentNavController: LiveData<NavController>? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        App.appBarLayout = binding.appBarLayout
        App.toolbar = binding.toolbar

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        if (savedInstanceState == null) setupNavigation()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupNavigation()
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    private fun setupNavigation() {
        val navGraphIds = listOf(
            R.navigation.collection,
            R.navigation.playlists,
            R.navigation.now_playing,
            R.navigation.queue,
            R.navigation.search,
            R.navigation.settings,
        )



        val controller = binding.bottomNavigation.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent,
        )

        controller.observe(this) { navController ->
            binding.toolbar.setupWithNavController(navController)
            binding.drawerNavigation.setupWithNavController(navController)
        }
        currentNavController = controller
    }
}