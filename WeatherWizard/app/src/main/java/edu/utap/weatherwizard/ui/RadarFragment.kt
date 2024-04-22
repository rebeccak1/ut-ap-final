package edu.utap.weatherwizard.ui

import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlay
import edu.utap.weatherwizard.databinding.FragmentRadarBinding
import kotlin.math.ln
import kotlin.math.sin
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.android.gms.maps.model.UrlTileProvider
import com.google.android.gms.maps.model.TileProvider
import edu.utap.weatherwizard.R
import java.net.MalformedURLException
import java.net.URL
import java.util.Locale

class RadarFragment: Fragment(), OnMapReadyCallback {
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var map: GoogleMap
    private lateinit var mMoonTiles: TileOverlay
    val currentMap = "Precipitation"
    private val radarMaps: Array<String> by lazy {
        resources.getStringArray(R.array.radarmaps_array)
    }
    companion object {
        var mapFragment : SupportMapFragment?=null
        fun newInstance() = RadarFragment()
        private var MOON_MAP_URL_FORMAT = "https://tile.openweathermap.org/map/precipitation_new/%d/%d/%d.png?appid=1e014bfae9d273d95b456a0e8b290034"

    }
    private var view: View? = null
    private var _binding: FragmentRadarBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
//    private val args: RadarFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_radar, container, false);

//        _binding = FragmentRadarBinding.inflate(inflater, container, false)
//        return binding.root
        mapFragment =
            childFragmentManager.findFragmentById(R.id.mapFrag) as? SupportMapFragment
//        if (mapFragment == null) {
//            FragmentManager fragmentManager = getFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            mSupportMapFragment = SupportMapFragment.newInstance();
//            fragmentTransaction.replace(R.id.mapwhere, mSupportMapFragment).commit();
//        }
//        if (mSupportMapFragment != null)
//        {
//            googleMap = mSupportMapFragment.getMap();
        mapFragment?.getMapAsync(this)
        return view
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

    private fun createAdapterFromResource(arrayResource: Int):
            ArrayAdapter<CharSequence> {
        val adapter = ArrayAdapter.createFromResource(requireActivity().applicationContext,
            arrayResource,
            android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState) //?
        initMenu()
//        viewModel.setTitle("One Post")

//        viewModel.hideActionBarFavorites()
        Log.d(javaClass.simpleName, "onViewCreated")
        val radarSpinner = view.findViewById<Spinner>(R.id.radar_spinner)
        val goBut = view.findViewById<Button>(R.id.goBut)

        radarSpinner.adapter = createAdapterFromResource(R.array.radarmaps_array)
        radarSpinner.setSelection(0)

        var radarMap = currentMap

        goBut.setOnClickListener {
            if (radarMap != currentMap) {
                if(radarMap == "Precipitation") {
                    MOON_MAP_URL_FORMAT =
                        "https://tile.openweathermap.org/map/precipitation_new/%d/%d/%d.png?appid=1e014bfae9d273d95b456a0e8b290034"
                }
                else {
                    MOON_MAP_URL_FORMAT =
                        "https://tile.openweathermap.org/map/temp_new/%d/%d/%d.png?appid=1e014bfae9d273d95b456a0e8b290034"
                }
                mMoonTiles.clearTileCache()
            }
        }
        radarSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val spinnerPos = radarSpinner.selectedItemPosition
                radarMap =  radarMaps[spinnerPos]
            }

        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        val tileProvider: TileProvider = object : UrlTileProvider(256, 256) {
            @Synchronized
            override fun getTileUrl(x: Int, y: Int, zoom: Int): URL? {
                // The moon tile coordinate system is reversed.  This is not normal.
                val reversedY = (1 shl zoom) - y - 1
                val s = String.format(Locale.US, MOON_MAP_URL_FORMAT, zoom, x, y)
                var url: URL? = null
                url = try {
                    URL(s)
                } catch (e: MalformedURLException) {
                    throw AssertionError(e)
                }
                Log.d("XXX", "returning URL " + url)
                return url
            }
        }

        // Go to initial location
        val cm = viewModel.observeCurrentCM().value

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(cm!!.latitude.toDouble(), cm.longitude.toDouble()), 5.0f))
        mMoonTiles = map.addTileOverlay(TileOverlayOptions().tileProvider(tileProvider))!!
        mMoonTiles.transparency = 0.1f


    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}