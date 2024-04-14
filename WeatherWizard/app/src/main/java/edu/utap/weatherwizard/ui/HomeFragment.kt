package edu.utap.weatherwizard.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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
//            val action = HomeFragmentDirections.actionHomeFragmentToOnePostFragment(it)
//            findNavController().navigate(action)

        }
        // XXX Write me, observe posts
        val rv = binding.dailyRV
        rv.adapter = postRowAdapter
        rv.layoutManager = LinearLayoutManager(requireContext())
        viewModel.observeNetWeatherDaily().observe(viewLifecycleOwner) { postList ->
            postRowAdapter.submitList(postList)
            Log.d("XXX", "OBSERVING net weather")
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

        viewModel.observeNetWeatherDaily().observe(viewLifecycleOwner){
            binding.tb.text = it
        }
        initAdapter(binding)
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