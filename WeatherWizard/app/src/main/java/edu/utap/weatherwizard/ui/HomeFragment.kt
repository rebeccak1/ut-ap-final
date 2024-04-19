package edu.utap.weatherwizard.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import edu.utap.weatherwizard.R
import edu.utap.weatherwizard.databinding.FragmentHomeBinding

// XXX Write most of this file
class HomeFragment: Fragment() {
    // XXX initialize viewModel
    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!


    // Set up the adapter and recycler view
    private fun initAdapter(binding: FragmentHomeBinding) {
        val postRowAdapter = DailyRowAdapter(viewModel) {
//            Log.d("OnePost",
//                String.format("OnePost title %s",
//                    if (it.title.length > 32)
//                        it.title.substring(0, 31) + "..."
//                    else it.title))
//            Log.d("doOnePost", "image ${it.imageURL}")
            // XXX Write me
            val action = HomeFragmentDirections.actionHomeFragmentToOneDayFragment(it)
            findNavController().navigate(action)

        }
        // XXX Write me, observe posts
        val rv = binding.dailyRV
        rv.adapter = postRowAdapter
        rv.layoutManager = LinearLayoutManager(requireContext())
        viewModel.observeNetWeatherDaily().observe(viewLifecycleOwner) { postList ->
            postRowAdapter.submitList(postList)
            Log.d("XXX", "OBSERVING net weather")
        }

        viewModel.observeCurrentCM().observe(viewLifecycleOwner){
            binding.city.text = it.city
            binding.state.text = it.state
            if(it.favorite){
                binding.favIcon.setImageResource(R.drawable.ic_favorite_black_24dp)
            }
            else{
                binding.favIcon.setImageResource(R.drawable.ic_favorite_border_black_24dp)
            }
            if(it.home){
                binding.homeIcon.setImageResource(R.drawable.baseline_home_24)
            }
            else{
                binding.homeIcon.setImageResource(R.drawable.outline_home_24)
            }
        }

//        viewModel.observeCity().observe(viewLifecycleOwner){
//            binding.city.text = it
//        }
//
//        viewModel.observeState().observe(viewLifecycleOwner){
//            binding.state.text = it
//        }
//        viewModel.observeFavorite().observe(viewLifecycleOwner){
//            if(it){
//                binding.favIcon.setImageResource(R.drawable.ic_favorite_black_24dp)
//            }
//            else{
//                binding.favIcon.setImageResource(R.drawable.ic_favorite_border_black_24dp)
//            }
//        }
//        viewModel.observeHome().observe(viewLifecycleOwner){
//            if(it){
//                binding.homeIcon.setImageResource(R.drawable.baseline_home_24)
//            }
//            else{
//                binding.homeIcon.setImageResource(R.drawable.outline_home_24)
//            }
//        }

    }

//    private fun initSwipeLayout(swipe : SwipeRefreshLayout) {
//        // XXX Write me
//        swipe.isEnabled = true
//        swipe.setOnRefreshListener {
//            viewModel.repoFetch()
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState) //?
//        viewModel.showActionBarFavorites()
        Log.d(javaClass.simpleName, "onViewCreated")

        initAdapter(binding)
        binding.arrowdown.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToCityFragment()
            findNavController().navigate(action)
        }

        binding.favIcon.setOnClickListener {
//            val home = viewModel.observeHome().value
//            val fav = viewModel.observeCurrentCM().value?.favorite
            val cm = viewModel.observeCurrentCM().value
            val home = cm?.home
            val fav = cm?.favorite
            if(!home!!){
                if(!fav!!){
                    Log.d("XXX", "home fragment clicked on city " + cm.state)
                    Log.d("XXX", "not a favorite")
                    binding.favIcon.setImageResource(R.drawable.ic_favorite_black_24dp)
                    cm.favorite = true
                    viewModel.saveCityMeta(cm)
//                    val latlng = viewModel.observeLatLng().value
//
//                    val cm = viewModel.createCityMeta(viewModel.observeCity().value!!,
//                        viewModel.observeState().value!!, viewModel.observeUnits().value!!,
//                        false, latlng?.latitude.toString(), latlng?.longitude.toString())
                }
                else{
                    Log.d("XXX", "home fragment clicked on city " + cm.state)
                    Log.d("XXX", "not a favorite")
                    binding.favIcon.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                    cm.favorite = false
                    viewModel.remove()
                }
            }
        }
//        initSwipeLayout(binding.swipeRefreshLayout)

//        viewModel.fetchDone.observe(viewLifecycleOwner) {
//            // XXX Write me, what does fetchDone mean?
//            if (it == true) {
//                binding.swipeRefreshLayout.isRefreshing = false
////                viewModel.updateFavorites(viewModel.observePosts().value)
//
//            }
//        }

    }
}