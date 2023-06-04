package com.example.wegarb.presentation.view.fragments.history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import androidx.recyclerview.widget.ListAdapter
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.wegarb.R
import com.example.wegarb.data.history.local.entity.HistoryDayEntity
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

        @SuppressLint("SetTextI18n")
        fun setData(historyDay: HistoryDay) = with(binding){
            tvDateDays.text = historyDay.date
            tvCurrentTemperatureDays.text ="${historyDay.temperature}°C"
            tvCityDays.text = historyDay.cityName
            tvStatus.text = "Status day: ${historyDay.status}"

            binding.buttonDelete.setOnClickListener {
                listener.onClickDeleteOnItem(historyDay)
            }
            itemView.setOnClickListener {
                listener.onClickItem(historyDay)
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
        fun onClickDeleteOnItem(historyDay: HistoryDay)
        fun onClickItem(historyDay: HistoryDay)
    }

}