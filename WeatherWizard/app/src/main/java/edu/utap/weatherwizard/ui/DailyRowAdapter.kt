package edu.utap.weatherwizard.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utap.weatherwizard.R
import edu.utap.weatherwizard.api.WeatherDaily
import edu.utap.weatherwizard.api.WeatherWeather
import edu.utap.weatherwizard.databinding.DailyRowBinding
import edu.utap.weatherwizard.glide.Glide

class DailyRowAdapter(private val viewModel: MainViewModel,
                     private val navigateToOnePost: (WeatherDaily)->Unit )
    : ListAdapter<WeatherDaily, DailyRowAdapter.VH>(WeatherDailyDiff()) {

    inner class VH(val dailyRowBinding: DailyRowBinding)
        : RecyclerView.ViewHolder(dailyRowBinding.root) {
        init {
//            rowPostBinding.selfText.setOnClickListener {
//                navigateToOnePost(getItem(bindingAdapterPosition))
//            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val rowBinding = DailyRowBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return VH(rowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        //XXX Write me.
        val rowBinding = holder.dailyRowBinding
        val item = getItem(position)

        rowBinding.title.text = item.title

//        Glide.glideFetch(item.imageURL, item.thumbnailURL, rowBinding.image)

//        Log.d("XXX", "on bind currentindex position "+ position.toString() + " " + item.title)
    }


    class WeatherDailyDiff : DiffUtil.ItemCallback<WeatherDaily>() {
        override fun areItemsTheSame(oldItem: WeatherDaily, newItem: WeatherDaily): Boolean {
//            return oldItem.key == newItem.key
            return true
        }
        override fun areContentsTheSame(oldItem: WeatherDaily, newItem: WeatherDaily): Boolean {
//            return RedditPost.spannableStringsEqual(oldItem.title, newItem.title) &&
//                    RedditPost.spannableStringsEqual(oldItem.selfText, newItem.selfText) &&
//                    RedditPost.spannableStringsEqual(oldItem.publicDescription, newItem.publicDescription) &&
//                    RedditPost.spannableStringsEqual(oldItem.displayName, newItem.displayName)
            return true
        }
    }
}

