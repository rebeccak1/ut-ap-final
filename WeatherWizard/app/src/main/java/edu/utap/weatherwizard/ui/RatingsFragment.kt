package edu.utap.weatherwizard.ui

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import edu.utap.weatherwizard.R
import edu.utap.weatherwizard.databinding.FragmentRatingsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import edu.utap.weatherwizard.invalidUser
import edu.utap.weatherwizard.model.CityRating
import edu.utap.weatherwizard.model.Rating

class RatingsFragment: Fragment() {
    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentRatingsBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    fun processRatings(ratingsList: List<Rating>): List<CityRating>{
        val ratingsMap = mutableMapOf<String, Double>()
        val countMap = mutableMapOf<String, Int>()

        for(r in ratingsList){
            val name = r.city +","+ r.state
            if(name in ratingsMap && name in countMap){
                countMap[name] = countMap[name]!! + 1

                ratingsMap[name] = ratingsMap[name]!! + (r.rating - ratingsMap[name]!!)/ countMap[name]!!
            }
            else{
                countMap[name] = 1
                ratingsMap[name] = r.rating.toDouble()
            }
        }
        val cityRating = mutableListOf<CityRating>()
        for(r in ratingsMap){
            val citystate : List<String> = r.key.split(",")

            cityRating.add(CityRating(r.value, citystate[0], citystate[1]))
        }
        return cityRating

    }

//    override fun onResume() {
//        super.onResume()
//        viewModel.observeRatings()
//    }

    private fun initAdapter(binding: FragmentRatingsBinding) {
        val postRowAdapter = RatingsRowAdapter(viewModel, findNavController())
        val rv = binding.cityRV
        rv.adapter = postRowAdapter
        rv.layoutManager = LinearLayoutManager(requireContext())
        viewModel.observeRatings().observe(viewLifecycleOwner) { ratingsList ->

            postRowAdapter.submitList(processRatings(ratingsList))
            Log.d("XXX", "OBSERVING ratings")
        }
        binding.currentCity.text = viewModel.observeCurrentCM().value?.city
        binding.currentState.text = viewModel.observeCurrentCM().value?.state
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRatingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun initMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Clear menu because we don't want settings icon
                menu.clear()
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Do nothing
                return false
            }
        }, viewLifecycleOwner)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState) //?
        initMenu()
//        viewModel.setTitle("One Post")

//        viewModel.hideActionBarFavorites()
        initAdapter(binding)
        binding.goBut.setOnClickListener {
            val et = binding.mapET.text.toString()
            if(et.isDigitsOnly() && et.toInt() <=5 && et.toInt() >= 1) {
                viewModel.createRating(et.toInt())
            }
            else{
                Snackbar.make(view,
                    "Enter an integer 1 to 5 to rate current city's weather",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}