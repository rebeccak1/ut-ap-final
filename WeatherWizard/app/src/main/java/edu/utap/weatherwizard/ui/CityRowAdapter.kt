package edu.utap.weatherwizard.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utap.weatherwizard.databinding.CityRowBinding
import edu.utap.weatherwizard.R
import edu.utap.weatherwizard.model.CityMeta
import com.google.android.gms.maps.model.LatLng
import androidx.navigation.NavController

class CityRowAdapter(private val viewModel: MainViewModel,
                     private val navController: NavController )
    : ListAdapter<CityMeta, CityRowAdapter.VH>(CityMetaDiff()) {

    inner class VH(val cityRowBinding: CityRowBinding)
        : RecyclerView.ViewHolder(cityRowBinding.root) {
        init {
            //fix these to navigate 8 day
            cityRowBinding.rowCity.setOnClickListener {
                val cityMeta = getItem(bindingAdapterPosition)
                viewModel.setCityMeta(cityMeta)
//                viewModel.setCity(cityMeta.city)
//                viewModel.setState(cityMeta.state)
//                viewModel.setLatLon(LatLng(cityMeta.latitude.toDouble(), cityMeta.longitude.toDouble()))
                navController.popBackStack()
            }
            cityRowBinding.rowComma.setOnClickListener {
                val cityMeta = getItem(bindingAdapterPosition)
                viewModel.setCityMeta(cityMeta)
                navController.popBackStack()

            }
            cityRowBinding.rowState.setOnClickListener {
                val cityMeta = getItem(bindingAdapterPosition)
                viewModel.setCityMeta(cityMeta)
                navController.popBackStack()
            }
            cityRowBinding.rowIcon.setOnClickListener {
                val cityMeta = getItem(bindingAdapterPosition)
                if(cityMeta.favorite){
                    cityMeta.favorite = false
                    cityRowBinding.rowIcon.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                }
                else{
                    cityMeta.favorite = true
                    cityRowBinding.rowIcon.setImageResource(R.drawable.ic_favorite_black_24dp)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val rowBinding = CityRowBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return VH(rowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        //XXX Write me.
        val rowBinding = holder.cityRowBinding
        val item = getItem(position)

        rowBinding.rowCity.text = item.city
        rowBinding.rowState.text = item.state
        if(item.home){
            rowBinding.rowIcon.setImageResource(R.drawable.baseline_home_24)
        }
        if(item.favorite){
            rowBinding.rowIcon.setImageResource(R.drawable.ic_favorite_black_24dp)
        }

    }

    class CityMetaDiff : DiffUtil.ItemCallback<CityMeta>() {
        override fun areItemsTheSame(oldItem: CityMeta, newItem: CityMeta): Boolean {
            return (oldItem.city == newItem.city) && (oldItem.state == newItem.state)
        }
        override fun areContentsTheSame(oldItem: CityMeta, newItem: CityMeta): Boolean {
            return (oldItem.city == newItem.city) && (oldItem.state == newItem.state)
        }
    }
}