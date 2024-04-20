//package edu.utap.weatherwizard.ui
//
//import android.graphics.Point
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.Menu
//import android.view.MenuInflater
//import android.view.MenuItem
//import androidx.core.view.MenuProvider
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.activityViewModels
//import androidx.navigation.fragment.navArgs
//import com.google.android.gms.maps.model.LatLng
//import edu.utap.weatherwizard.R
//import edu.utap.weatherwizard.databinding.FragmentOneDayBinding
//import edu.utap.weatherwizard.glide.Glide
//import java.text.SimpleDateFormat
//import kotlin.math.floor
//import kotlin.math.ln
//import kotlin.math.sin
//
//// XXX Write most of this file
//class RadarFragment: Fragment() {
//    // XXX initialize viewModel
//    private val viewModel: MainViewModel by activityViewModels()
//
//    private var _binding: FragmentRadarBinding? = null
//    // This property is only valid between onCreateView and onDestroyView.
//    private val binding get() = _binding!!
//    private val args: RadarFragmentArgs by navArgs()
//
//    val TILE_SIZE = 256
//
//    fun project(latLng: LatLng): Point {
//        var siny = sin((latLng.latitude * Math.PI) / 180)
//
//        siny = siny.coerceAtLeast(-0.9999).coerceAtMost(0.9999)
//        return Point(
//            (TILE_SIZE * (0.5 + latLng.longitude / 360)).toInt(),
//            (TILE_SIZE * (0.5 - ln((1 + siny) / (1 - siny)) / (4 * Math.PI))).toInt(),
//        )
//    }
//
//    fun getTileCoords(latLng: LatLng): Point {
//        val zoom = 10
//        val scale = 1 shl zoom
//        val worldCoordinate = project(latLng)
//
//        return Point(
//            (worldCoordinate.x * scale) / TILE_SIZE,
//            (worldCoordinate.y * scale) / TILE_SIZE,
//        )
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentOneDayBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    private fun initMenu() {
//        requireActivity().addMenuProvider(object : MenuProvider {
//            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                // Clear menu because we don't want settings icon
//                menu.clear()
//            }
//            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//                // Do nothing
//                return false
//            }
//        }, viewLifecycleOwner)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState) //?
//        initMenu()
////        viewModel.setTitle("One Post")
//
////        viewModel.hideActionBarFavorites()
//        Log.d(javaClass.simpleName, "onViewCreated")
//        val date = java.util.Date(args.daily.dt.toLong()*1000)
//        val simpleDateformat = SimpleDateFormat("EEEE")
//        val dow = simpleDateformat.format(date)
//        val calendar = java.util.Calendar.getInstance()
//        calendar.setTime(date)
//
//        val dom = calendar.get(java.util.Calendar.DAY_OF_MONTH).toString()
//        val month = SimpleDateFormat("MMMM").format(date)
//
//        binding.dailyDay.text = dow +", " + month + " " + dom
//
//
//
//    }
//    override fun onDestroyView() {
//        _binding = null
//        super.onDestroyView()
//    }
//}