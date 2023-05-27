package com.example.wegarb.presentation.view.fragments.history

import android.view.LayoutInflater
import androidx.recyclerview.widget.ListAdapter
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.wegarb.R
import com.example.wegarb.data.history.local.history.entity.HistoryDayEntity
import com.example.wegarb.databinding.ItemDayBinding
import com.example.wegarb.domain.models.history.HistoryDay

class HistoryAdapter(private val listener: Listener) : ListAdapter<HistoryDay, HistoryAdapter.ItemHolderDays>(ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolderDays {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false)
      return ItemHolderDays(view, listener)
    }

    override fun onBindViewHolder(itemHolderDays: ItemHolderDays, position: Int) {
        itemHolderDays.setData(getItem(position))
    }

    class ItemHolderDays(view: View, private val listener: Listener) : RecyclerView.ViewHolder(view) {
        private val binding = ItemDayBinding.bind(view)

        fun setData(historyDay: HistoryDay) = with(binding){
            val cDate = historyDay.date
            val cCity = historyDay.cityName
            val cTemp = "${historyDay.temperature}°C"
            val cCond = "Direction: ${historyDay.description}"
            val cSearchWindDto = "Wind speed: ${historyDay.windSpeed} m/c"


            tvDateDays.text = cDate
            tvCurrentTemperatureDays.text = cTemp
            tvConditionDays.text = cCond
            tvWindDays.text = cSearchWindDto
            tvCityDays.text = cCity
            tvStatus.text = historyDay.status

            binding.buttonDelete.setOnClickListener {
                listener.onClickViewOnItem(historyDay)
            }
            itemView.setOnClickListener {
                listener.onClickViewOnItemAll(historyDay)
            }
        }
    }

    class ItemComparator() : DiffUtil.ItemCallback<HistoryDay>() {
        override fun areItemsTheSame(oldItem: HistoryDay, newItem: HistoryDay): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HistoryDay, newItem: HistoryDay): Boolean {
            return oldItem == newItem
        }
    }

    interface Listener {
        fun onClickViewOnItem(historyDay: HistoryDay)
        fun onClickViewOnItemAll(historyDay: HistoryDay)
    }


}