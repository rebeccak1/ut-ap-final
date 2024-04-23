package edu.utap.weatherwizard.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.utap.weatherwizard.R
import edu.utap.weatherwizard.databinding.FragmentHomeBinding
import edu.utap.weatherwizard.invalidUser

class HomeFragment: Fragment() {
    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private fun initRecyclerViewDividers(rv: RecyclerView) {
        val dividerItemDecoration = DividerItemDecoration(
            rv.context, LinearLayoutManager.HORIZONTAL
        )
        rv.addItemDecoration(dividerItemDecoration)
    }
    // Set up the adapter and recycler view
    private fun initAdapter(binding: FragmentHomeBinding) {
        val postRowAdapter = DailyRowAdapter(viewModel) {
            val action = HomeFragmentDirections.actionHomeFragmentToOneDayFragment(it)
            findNavController().navigate(action)

        }
        val rv = binding.dailyRV
        rv.adapter = postRowAdapter
        rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        initRecyclerViewDividers(rv)
        viewModel.observeNetWeatherDaily().observe(viewLifecycleOwner) { postList ->
            postRowAdapter.submitList(postList)
            Log.d("XXX", "OBSERVING net weather")
        }

        viewModel.observeCurrentCM().observe(viewLifecycleOwner){
            binding.city.text = it.city
            binding.state.text = it.state
            if(viewModel.getCurrentUser() != invalidUser) {
                if (it.favorite) {
                    binding.favIcon.setImageResource(R.drawable.ic_favorite_black_24dp)
                } else {
                    binding.favIcon.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                }
                if (it.home) {
                    binding.homeIcon.setImageResource(R.drawable.baseline_home_24)
                } else {
                    binding.homeIcon.setImageResource(R.drawable.outline_home_24)
                }
            }
            else{
                binding.homeIcon.visibility=View.GONE
            }
        }

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

        binding.radar.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToRadarFragment()
            findNavController().navigate(action)
        }
        binding.star.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToRatingsFragment()
            findNavController().navigate(action)
        }

        binding.favIcon.setOnClickListener {
            if(viewModel.getCurrentUser() != invalidUser) {

                val cm = viewModel.observeCurrentCM().value
                val home = cm?.home
                val fav = cm?.favorite
                if (!home!!) {
                    if (!fav!!) {
                        Log.d("XXX", "home fragment clicked on city " + cm.city)
                        Log.d("XXX", "not a favorite")

                        //need this?
                        binding.favIcon.setImageResource(R.drawable.ic_favorite_black_24dp)
                        cm.favorite = true
                        viewModel.saveCityMeta(cm)

                    } else {
                        Log.d("XXX", "home fragment clicked on city " + cm.city)
                        Log.d("XXX", "a favorite")
                        binding.favIcon.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                        viewModel.remove(cm)
                        cm.favorite = false
                    }
                }
            }
        }
        binding.homeIcon.setOnClickListener {
            if(viewModel.getCurrentUser() != invalidUser) {
                val cm = viewModel.observeCurrentCM().value
                val home = cm?.home
                val fav = cm?.favorite
                if(home!!){
                    Log.d("XXX", "home fragment clicked on city " + cm.city)
                    Log.d("XXX", "home")

                    cm.home = false
                    cm.favorite = true
                    viewModel.updateCityMeta(cm, false, true)
                    binding.homeIcon.setImageResource(R.drawable.outline_home_24)
                    binding.favIcon.setImageResource(R.drawable.ic_favorite_black_24dp)

                }
                else{
                    Log.d("XXX", "home fragment clicked on city " + cm.city)
                    Log.d("XXX", "not home")
                    val prevHome = viewModel.getHome()
                    cm.home = true
                    if(prevHome != null){
                        viewModel.updateCityMeta(prevHome, false, true)
                    }
                    if(cm.favorite) {
                        viewModel.updateCityMeta(cm, true, false)
                        cm.favorite = false
                        binding.favIcon.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                    }
                    else{
                        viewModel.saveCityMeta(cm)
                    }
                    binding.homeIcon.setImageResource(R.drawable.baseline_home_24)

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