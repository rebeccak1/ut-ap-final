package edu.utap.weatherwizard.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utap.weatherwizard.databinding.RatingsRowBinding
import edu.utap.weatherwizard.R
import androidx.navigation.NavController
import edu.utap.weatherwizard.model.CityRating

class RatingsRowAdapter(private val viewModel: MainViewModel,
                        private val navController: NavController )
    : ListAdapter<CityRating, RatingsRowAdapter.VH>(CityRatingDiff()) {

    inner class VH(val ratingsRowBinding: RatingsRowBinding)
        : RecyclerView.ViewHolder(ratingsRowBinding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val rowBinding = RatingsRowBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return VH(rowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val rowBinding = holder.ratingsRowBinding
        val item = getItem(position)

        rowBinding.rowCity.text = item.city
        rowBinding.rowState.text = item.state
        rowBinding.rowRating.text = String.format("%1.2f",item.avgRating)
    }

    class CityRatingDiff : DiffUtil.ItemCallback<CityRating>() {
        override fun areItemsTheSame(oldItem: CityRating, newItem: CityRating): Boolean {
            return (oldItem.city == newItem.city) && (oldItem.state == newItem.state) &&
            (oldItem.avgRating == newItem.avgRating)
        }
        override fun areContentsTheSame(oldItem: CityRating, newItem: CityRating): Boolean {
            return (oldItem.city == newItem.city) && (oldItem.state == newItem.state) &&
                    (oldItem.avgRating == newItem.avgRating)
        }
    }
}