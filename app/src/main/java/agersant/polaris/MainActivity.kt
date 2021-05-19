package agersant.polaris

import agersant.polaris.databinding.ActivityMainBinding
import agersant.polaris.ui.BackdropLayout
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var backdropLayout: BackdropLayout
    private lateinit var backdropNav: NavigationView
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        toolbar = binding.toolbar
        backdropLayout = binding.backdropLayout
        backdropNav = binding.backdropNav
        bottomNav = binding.bottomNav

        setContentView(binding.root)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_collection,
                R.id.nav_queue,
                R.id.nav_now_playing,
            ),
            backdropLayout,
        )

        toolbar.setupWithNavController(navController, appBarConfiguration)
        bottomNav.setupWithNavController(navController)
        backdropNav.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, _, _ ->
            backdropLayout.close()
        }
    }
}
