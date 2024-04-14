package edu.utap.weatherwizard

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import edu.utap.weatherwizard.databinding.ActivityMainBinding

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


        // Set up our nav graph
        navController = findNavController(R.id.main_frame)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        // If we have a toolbar (not actionbar) we don't need to override
        // onSupportNavigateUp().
//        activityMainBinding.toolbar.setupWithNavController(navController, appBarConfiguration)
        //setupActionBarWithNavController(navController, appBarConfiguration)
    }
}


//import android.R
//import android.os.Bundle
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import edu.utap.weatherwizard.databinding.ActivityMainBinding
//import edu.utap.weatherwizard.databinding.ActivityRegistrationBinding
//
//class MainActivity : AppCompatActivity() {
//    private var geeksforgeeks: TextView? = null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        val binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)

//
//        // initialising all views through id defined above
//        geeksforgeeks = binding.gfg
//        geeksforgeeks!!.setText(
//            "GeeksForGeeks(Firebase Authentication)"
//        )
//}
//}