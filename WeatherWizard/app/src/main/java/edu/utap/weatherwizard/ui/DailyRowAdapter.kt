package edu.utap.weatherwizard.ui

import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utap.weatherwizard.api.WeatherDaily
import edu.utap.weatherwizard.databinding.DailyRowBinding
import java.text.SimpleDateFormat
import edu.utap.weatherwizard.R

class DailyRowAdapter(private val viewModel: MainViewModel,
                     private val navigateToOneDay: (WeatherDaily)->Unit )
    : ListAdapter<WeatherDaily, DailyRowAdapter.VH>(WeatherDailyDiff()) {



    inner class VH(val dailyRowBinding: DailyRowBinding)
        : RecyclerView.ViewHolder(dailyRowBinding.root) {
        init {
            dailyRowBinding.rowDay.setOnClickListener {
                navigateToOneDay(getItem(bindingAdapterPosition))
            }
            dailyRowBinding.rowIcon.setOnClickListener {
                navigateToOneDay(getItem(bindingAdapterPosition))
            }
            dailyRowBinding.rowLow.setOnClickListener {
                navigateToOneDay(getItem(bindingAdapterPosition))
            }
            dailyRowBinding.rowHigh.setOnClickListener {
                navigateToOneDay(getItem(bindingAdapterPosition))
            }
            dailyRowBinding.rowDate.setOnClickListener {
                navigateToOneDay(getItem(bindingAdapterPosition))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val rowBinding = DailyRowBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return VH(rowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val rowBinding = holder.dailyRowBinding
        val item = getItem(position)
        val date = java.util.Date(item.dt.toLong()*1000)
        val simpleDateformat = SimpleDateFormat("E") // the day of the week abbreviated
        val dow = simpleDateformat.format(date)
        val calendar = java.util.Calendar.getInstance()
        calendar.setTime(date)

        val maxMaxTemp = viewModel.observeMaxMaxTemp()
        val maxMinTemp = viewModel.observeMaxMinTemp()

        val minMaxTemp = viewModel.observeMinMaxTemp()
        val minMinTemp = viewModel.observeMinMinTemp()

        rowBinding.rowDay.text = dow
        rowBinding.rowDate.text = calendar.get(java.util.Calendar.DAY_OF_MONTH).toString()
        rowBinding.rowHigh.text = String.format("%2.0f",item.temp.max)
        rowBinding.rowLow.text = String.format("%2.0f",item.temp.min)
        var ratio = (item.temp.max-maxMinTemp)/(maxMaxTemp - maxMinTemp)
        var w = .2 - .1*ratio

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            weight =  w.toFloat()
        }
        params.gravity = Gravity.CENTER

        ratio = (item.temp.min-minMinTemp)/(minMaxTemp - minMinTemp)

        var w1 = .1 + .1*ratio

        val params1 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            weight =  w1.toFloat()
        }
        params1.gravity = Gravity.CENTER
//        Log.d("XXX", w.toString() +" "+ w1.toString() +" " + rowBinding.rowIcon.layoutParams.width)
        rowBinding.rowHigh.setLayoutParams(params)
        rowBinding.rowLow.setLayoutParams(params1)

        val width = holder.itemView.getContext().getResources().getDisplayMetrics().density

        val params2 = LinearLayout.LayoutParams(
            20*width.toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            weight =  (1-w-w1).toFloat()
        }
        params2.gravity = Gravity.CENTER
        rowBinding.colorbar.setLayoutParams(params2)

        rowBinding.rowIcon.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        rowBinding.rowIcon.requestLayout()
//        rowBinding.colorbar.layoutParams = LayoutParams(20*width.toInt(), LayoutParams.WRAP_CONTENT,
//            (1-w-w1).toFloat())



//        rowBinding.rowHigh.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
//            (.2*(1-item.temp.max/100)).toFloat()
//        rowBinding.colorbar.layoutParams = LayoutParams(rowBinding.colorbar.width, LayoutParams.WRAP_CONTENT,
//            (weight).toFloat())
//
//        rowBinding.rowLow.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
//            (weight).toFloat())
//
//        rowBinding.rowIcon.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0F)

        when(item.weather[0].icon){
            "01d" -> rowBinding.rowIcon.setImageResource(R.drawable.clearsky)
            "02d" -> rowBinding.rowIcon.setImageResource(R.drawable.few_clouds)
            "03d" -> rowBinding.rowIcon.setImageResource(R.drawable.scattered_clouds)
            "04d" -> rowBinding.rowIcon.setImageResource(R.drawable.broken_clouds)
            "09d" -> rowBinding.rowIcon.setImageResource(R.drawable.shower_rain)
            "10d" -> rowBinding.rowIcon.setImageResource(R.drawable.rain)
            "11d" -> rowBinding.rowIcon.setImageResource(R.drawable.thunderstorm)
            "13d" -> rowBinding.rowIcon.setImageResource(R.drawable.snow)
            "50d" -> rowBinding.rowIcon.setImageResource(R.drawable.mist)
        }

//        val url = "https://openweathermap.org/img/wn/" + item.weather[0].icon + "2x.png"
//        Glide.glideFetch(url, url, rowBinding.rowIcon)

//        Log.d("XXX", "on bind currentindex position "+ position.toString() + " " + item.title)
    }


    class WeatherDailyDiff : DiffUtil.ItemCallback<WeatherDaily>() {
        override fun areItemsTheSame(oldItem: WeatherDaily, newItem: WeatherDaily): Boolean {
            return (oldItem.dt == newItem.dt) && (oldItem.weather[0] == newItem.weather[0])
                    && (oldItem.temp.max == newItem.temp.max) && (oldItem.temp.min == newItem.temp.min)
        }
        override fun areContentsTheSame(oldItem: WeatherDaily, newItem: WeatherDaily): Boolean {
            return (oldItem.dt == newItem.dt) && (oldItem.weather[0] == newItem.weather[0])
                    && (oldItem.temp.max == newItem.temp.max) && (oldItem.temp.min == newItem.temp.min)

        }
    }
}