package edu.utap.weatherwizard.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import edu.utap.weatherwizard.R
import edu.utap.weatherwizard.databinding.FragmentOneDayBinding
import edu.utap.weatherwizard.glide.Glide
import java.text.SimpleDateFormat

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
        Log.d(javaClass.simpleName, "onViewCreated")
        val date = java.util.Date(args.daily.dt.toLong()*1000)
        val simpleDateformat = SimpleDateFormat("EEEE")
        val dow = simpleDateformat.format(date)
        val calendar = java.util.Calendar.getInstance()
        calendar.setTime(date)

        val dom = calendar.get(java.util.Calendar.DAY_OF_MONTH).toString()
        val month = SimpleDateFormat("MMMM").format(date)

        binding.dailyDay.text = dow +", " + month + " " + dom
        binding.cloudsEdit.text = args.daily.clouds.toString()
        binding.humidityEdit.text = args.daily.humidity.toString()
        binding.precippct.text = args.daily.pop.toString()
        binding.high.text = String.format("%2.0f",args.daily.temp.max)
        binding.summary.text = args.daily.summary
        binding.uvEdit.text = String.format("%2.0f",args.daily.uvi)

        when(args.daily.weather[0].icon){
            "01d" -> binding.icon.setImageResource(R.drawable.clearsky)
            "02d" -> binding.icon.setImageResource(R.drawable.few_clouds)
            "03d" -> binding.icon.setImageResource(R.drawable.scattered_clouds)
            "04d" -> binding.icon.setImageResource(R.drawable.broken_clouds)
            "09d" -> binding.icon.setImageResource(R.drawable.shower_rain)
            "10d" -> binding.icon.setImageResource(R.drawable.rain)
            "11d" -> binding.icon.setImageResource(R.drawable.thunderstorm)
            "13d" -> binding.icon.setImageResource(R.drawable.snow)
            "50d" -> binding.icon.setImageResource(R.drawable.mist)
        }


    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}