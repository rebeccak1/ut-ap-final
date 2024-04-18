package edu.utap.weatherwizard

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import edu.utap.weatherwizard.databinding.ActivityMainBinding
import edu.utap.weatherwizard.ui.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var authUser: AuthUser
    private val viewModel: MainViewModel by viewModels()

    private fun initMenu() {
        addMenuProvider(object : MenuProvider {
            // XXX Write me, menu provider overrides
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_settings -> {
                        // XXX Write me
                        val navController = findNavController(R.id.main_frame)
                        navController.navigate(R.id.settingsFragment)
                        return true
                    }
                    else -> false
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        initMenu()
//        setSupportActionBar(activityMainBinding.toolbar)

        val extras = intent.extras
        if (extras != null) {
            val username = extras.getString("username")
            val useremail = extras.getString("useremail")
            val uuid = extras.getString("userid")

            val user = User(username, useremail, uuid!!)
            viewModel.setCurrentAuthUser(user)
            Log.d("XXX", username.toString() +" " + useremail.toString() +" " + uuid.toString())

            viewModel.fetchCityMeta() {
                viewModel.setHome()
//                viewModel.repoFetch()
                Log.d("XXX","city meta posted size " + viewModel.observeCityMeta().value?.size.toString())
            }
        }


        // Set up our nav graph
        navController = findNavController(R.id.main_frame)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        // If we have a toolbar (not actionbar) we don't need to override
        // onSupportNavigateUp().
        setupActionBarWithNavController(navController, appBarConfiguration)

//        activityMainBinding.toolbar.setupWithNavController(navController, appBarConfiguration)

    }

//    override fun onSupportNavigateUp(): Boolean {
//        // XXX Write me
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
//    }
}