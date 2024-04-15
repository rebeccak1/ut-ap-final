package edu.utap.weatherwizard.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import edu.utap.weatherwizard.databinding.FragmentOneDayBinding
import edu.utap.weatherwizard.glide.Glide

// XXX Write most of this file
class OneDayFragment: Fragment() {
    // XXX initialize viewModel
    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentOneDayBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private val args: OneDayFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOneDayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState) //?
//        viewModel.setTitle("One Post")

//        viewModel.hideActionBarFavorites()
        Log.d(javaClass.simpleName, "onViewCreated")

    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}