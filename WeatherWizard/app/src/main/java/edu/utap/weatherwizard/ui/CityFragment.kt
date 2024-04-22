package edu.utap.weatherwizard.ui

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
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import edu.utap.weatherwizard.R
import edu.utap.weatherwizard.databinding.FragmentCityBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import edu.utap.weatherwizard.invalidUser

class CityFragment: Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var geocoder: Geocoder

    private var _binding: FragmentCityBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private suspend fun processAddresses(addresses: List<Address>){
        withContext(Dispatchers.Main) {
            Log.d("XXX", addresses.toString()+" process address")
            if(addresses.isNotEmpty()) {
                val address = addresses[0]
                if(address.hasLatitude() && address.hasLongitude()) {
                    //check if already exists
                    if(viewModel.getCurrentUser() != invalidUser) {
                        var existsCM = viewModel.getCityState(address.locality, address.adminArea)
                        if (existsCM != null) {
                            viewModel.setCityMeta(existsCM)
                        } else {
                            var cm = viewModel.createCityMeta(
                                address.locality,
                                address.adminArea,
                                "Fahrenheit",
                                false,
                                false,
                                address.latitude.toString(),
                                address.longitude.toString()
                            )
                            viewModel.setCityMeta(cm)
                        }
                    }
                    else{
                        var cm = viewModel.createCityMeta(
                            address.locality,
                            address.adminArea,
                            "Fahrenheit",
                            false,
                            false,
                            address.latitude.toString(),
                            address.longitude.toString()
                        )
                        viewModel.setCityMeta(cm)
                    }


                }
            }

        }
    }

    private fun initAdapter(binding: FragmentCityBinding) {
        val postRowAdapter = CityRowAdapter(viewModel, findNavController())
        val rv = binding.cityRV
        rv.adapter = postRowAdapter
        rv.layoutManager = LinearLayoutManager(requireContext())
        viewModel.observeCityMeta().observe(viewLifecycleOwner) { postList ->
            postRowAdapter.submitList(postList)
            Log.d("XXX", "OBSERVING city meta")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCityBinding.inflate(inflater, container, false)
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
        geocoder = Geocoder(requireContext())
        binding.goBut.setOnClickListener {
            view1->
            val locationName = binding.mapET.text.toString()
            Log.d("Geocoding ", locationName)
            // call getFromLocationName on a background thread
            if (Build.VERSION.SDK_INT >= 33) {
                // Use this function, with a lambda for the Geocoder.GeocodeListener
                geocoder.getFromLocationName(locationName, 1) {
                    MainScope().launch {
                        if (it.isNotEmpty()) {
                            Log.d("XXX", "geocoding process address")

                            processAddresses(it)

                            findNavController().popBackStack()

                        } else {
                            Log.d("XXX", "geocoding no results")
                            Snackbar.make(view1,
                                "No results",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                }

            } else {
                // Use the deprecated API

                Log.d("XXX", "api 32")
                MainScope().launch {
                    val add = geocoder.getFromLocationName(locationName, 1)
                    if (add != null) {
                        if (add.isNotEmpty()) {

                            processAddresses(add)
                            findNavController().popBackStack()
                        } else {
                            Snackbar.make(view1,
                                "no results",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Snackbar.make(view1, "no results", Snackbar.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}