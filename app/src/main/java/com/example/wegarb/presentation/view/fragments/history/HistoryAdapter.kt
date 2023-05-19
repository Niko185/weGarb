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

class HistoryAdapter(private val listener: Listener) : ListAdapter<HistoryDayEntity, HistoryAdapter.ItemHolderDays>(
    ItemComparator()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolderDays {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false)
      return ItemHolderDays(view, listener)
    }

    override fun onBindViewHolder(itemHolderDays: ItemHolderDays, position: Int) {
        itemHolderDays.setData(getItem(position))
    }

    class ItemHolderDays(view: View, private val listener: Listener) : RecyclerView.ViewHolder(view) {
        private val binding = ItemDayBinding.bind(view)

        fun setData(historyDayEntity: HistoryDayEntity) = with(binding){
            val cDate = historyDayEntity.date
            val cCity = historyDayEntity.cityName
            val cTemp = "${historyDayEntity.temperature}°C"
            val cCond = "Direction: ${historyDayEntity.description}"
            val cSearchWindDto = "Wind speed: ${historyDayEntity.windSpeed} m/c"


            tvDateDays.text = cDate
            tvCurrentTemperatureDays.text = cTemp
            tvConditionDays.text = cCond
            tvWindDays.text = cSearchWindDto
            tvCityDays.text = cCity
            tvStatus.text = historyDayEntity.status

            binding.buttonDelete.setOnClickListener {
                listener.onClickViewOnItem(historyDayEntity)
            }
            itemView.setOnClickListener {
                listener.onClickViewOnItemAll(historyDayEntity)
            }
        }
    }

    class ItemComparator() : DiffUtil.ItemCallback<HistoryDayEntity>() {
        override fun areItemsTheSame(oldItem: HistoryDayEntity, newItem: HistoryDayEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HistoryDayEntity, newItem: HistoryDayEntity): Boolean {
            return oldItem == newItem
        }
    }

    interface Listener {
        fun onClickViewOnItem(historyDayEntity: HistoryDayEntity)
        fun onClickViewOnItemAll(historyDayEntity: HistoryDayEntity)
    }


}