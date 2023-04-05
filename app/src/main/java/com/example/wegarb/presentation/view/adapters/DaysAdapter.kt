package com.example.wegarb.presentation.view.adapters

import android.view.LayoutInflater
import androidx.recyclerview.widget.ListAdapter
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.wegarb.R
import com.example.wegarb.data.database.entity.InfoModel
import com.example.wegarb.databinding.ItemDayBinding

class DaysAdapter : ListAdapter<InfoModel, DaysAdapter.ItemHolderDays>(ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolderDays {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false)
      return ItemHolderDays(view)
    }

    override fun onBindViewHolder(itemHolderDays: ItemHolderDays, position: Int) {
        itemHolderDays.setData(getItem(position))
    }

    class ItemHolderDays(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemDayBinding.bind(view)

        fun setData(infoModel: InfoModel) = with(binding){
            val cDate = infoModel.date
            val cTemp = "${infoModel.currentTemp}°C"
            val cCond = "Condition: ${infoModel.currentCondition}"
            val cWind = "Wind: ${infoModel.currentWind} m/c"
            val cCity = "${infoModel.currentCity}:"

            tvDateDays.text = cDate
            tvCurrentTemperatureDays.text = cTemp
            tvConditionDays.text = cCond
            tvWindDays.text = cWind
            tvCityDays.text = cCity
        }
    }

    class ItemComparator() : DiffUtil.ItemCallback<InfoModel>() {
        override fun areItemsTheSame(oldItem: InfoModel, newItem: InfoModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: InfoModel, newItem: InfoModel): Boolean {
            return oldItem == newItem
        }
    }
}