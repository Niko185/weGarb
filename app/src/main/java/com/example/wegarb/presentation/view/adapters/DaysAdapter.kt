package com.example.wegarb.presentation.view.adapters

import android.app.Activity
import android.app.Application
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.ListAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.wegarb.R
import com.example.wegarb.data.database.entity.InfoModel
import com.example.wegarb.databinding.ItemDayBinding
import com.example.wegarb.presentation.view.fragments.DetailsDaysFragment
import com.example.wegarb.utils.FragmentManager

class DaysAdapter(private val listener: Listener) : ListAdapter<InfoModel, DaysAdapter.ItemHolderDays>(ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolderDays {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false)
      return ItemHolderDays(view, listener)
    }

    override fun onBindViewHolder(itemHolderDays: ItemHolderDays, position: Int) {
        itemHolderDays.setData(getItem(position))
    }

    class ItemHolderDays(view: View, private val listener: Listener) : RecyclerView.ViewHolder(view) {
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
            tvStatus.text = infoModel.status
            binding.buttonDelete.setOnClickListener {
                listener.onClickViewOnItem(infoModel)
            }
            itemView.setOnClickListener {
                listener.onClickViewOnItemAll()
            }
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

    interface Listener {
        fun onClickViewOnItem(infoModel: InfoModel)
        fun onClickViewOnItemAll()
    }


}