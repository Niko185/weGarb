package com.example.wegarb.presentation.view.fragments.history

import android.view.LayoutInflater
import androidx.recyclerview.widget.ListAdapter
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.wegarb.R
import com.example.wegarb.data.database.entity.FullDayInformation
import com.example.wegarb.databinding.ItemDayBinding

class HistoryAdapter(private val listener: Listener) : ListAdapter<FullDayInformation, HistoryAdapter.ItemHolderDays>(
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

        fun setData(fullDayInformation: FullDayInformation) = with(binding){
            val cDate = fullDayInformation.date
            val cTemp = fullDayInformation.currentTemp
            val cCond = fullDayInformation.currentCondition
            val cWind = fullDayInformation.currentWind
            val cCity = fullDayInformation.currentCity

            tvDateDays.text = cDate
            tvCurrentTemperatureDays.text = cTemp
            tvConditionDays.text = cCond
            tvWindDays.text = cWind
            tvCityDays.text = cCity
            tvStatus.text = fullDayInformation.status

            binding.buttonDelete.setOnClickListener {
                listener.onClickViewOnItem(fullDayInformation)
            }
            itemView.setOnClickListener {
                listener.onClickViewOnItemAll(fullDayInformation)
            }
        }
    }

    class ItemComparator() : DiffUtil.ItemCallback<FullDayInformation>() {
        override fun areItemsTheSame(oldItem: FullDayInformation, newItem: FullDayInformation): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FullDayInformation, newItem: FullDayInformation): Boolean {
            return oldItem == newItem
        }
    }

    interface Listener {
        fun onClickViewOnItem(fullDayInformation: FullDayInformation)
        fun onClickViewOnItemAll(fullDayInformation: FullDayInformation)
    }


}