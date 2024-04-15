package edu.utap.weatherwizard

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import edu.utap.weatherwizard.databinding.ActivityMainBinding
import edu.utap.weatherwizard.ui.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var authUser: AuthUser
    private val viewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        setSupportActionBar(activityMainBinding.toolbar)

        val extras = intent.extras
        if (extras != null) {
            val username = extras.getString("username")
            val useremail = extras.getString("useremail")
            val uuid = extras.getString("userid")

            val user = User(username, useremail, uuid!!)
            viewModel.setCurrentAuthUser(user)
            Log.d("XXX", username.toString() +" " + useremail.toString() +" " + uuid.toString())
        }


        // Set up our nav graph
        navController = findNavController(R.id.main_frame)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        // If we have a toolbar (not actionbar) we don't need to override
        // onSupportNavigateUp().
//        activityMainBinding.toolbar.setupWithNavController(navController, appBarConfiguration)
        //setupActionBarWithNavController(navController, appBarConfiguration)
    }
}