package edu.utap.weatherwizard.ui

import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import edu.utap.weatherwizard.R
import edu.utap.weatherwizard.databinding.FragmentCityBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import com.google.android.gms.maps.model.LatLng


// XXX Write most of this file
class CityFragment: Fragment() {
    // XXX initialize viewModel
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var geocoder: Geocoder
    var latLng: LatLng? = null
    var city: String? = null
    var state: String? = null

    private var _binding: FragmentCityBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
//    private val args: CityFragmentArgs by navArgs()

    private suspend fun processAddresses(addresses: List<Address>){
        // XXX Write me.  Note: suspend fun, so withContext is wise.  move the camera
        withContext(Dispatchers.Main) {
            Log.d("XXX", addresses.toString()+" process address")
            if(addresses.isNotEmpty()) {
                val address = addresses[0]
                if(address.hasLatitude() && address.hasLongitude()) {
                    latLng = LatLng(address.latitude, address.longitude)
                    viewModel.setCity(address.locality)
                    viewModel.setState(address.adminArea)
                    viewModel.setLatLon(latLng!!)
                    viewModel.setHomeBool(false)
                    viewModel.setFavBool(false)
                    viewModel.setPos(-1)

//                    viewModel.setLat(address.latitude)
//                    viewModel.setLat(address.longitude)
                }
            }

        }
    }

    private fun initAdapter(binding: FragmentCityBinding) {
        val postRowAdapter = CityRowAdapter(viewModel, findNavController())
        // XXX Write me, observe posts
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState) //?
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