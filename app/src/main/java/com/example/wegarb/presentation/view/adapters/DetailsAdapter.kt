package com.example.wegarb.presentation.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wegarb.R
import com.example.wegarb.data.models.GarbModel
import com.example.wegarb.databinding.ItemGarbDetailsBinding

class DetailsAdapter() : ListAdapter< GarbModel ,DetailsAdapter.ItemHolderDetails>(ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolderDetails {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_garb_details, parent, false)
        return ItemHolderDetails(view)
    }

    override fun onBindViewHolder(holder: ItemHolderDetails, position: Int) {
        holder.setData(getItem(position))
    }

    class ItemHolderDetails(view: View): RecyclerView.ViewHolder(view) {
        private val binding = ItemGarbDetailsBinding.bind(view)

        fun setData(garbModel: GarbModel) = with(binding) {
            imGarb.setImageResource(garbModel.imageGarb)
            textNameGarb.text= garbModel.nameGarb
        }

    }

    class ItemComparator(): DiffUtil.ItemCallback<GarbModel>() {
        override fun areItemsTheSame(oldItem: GarbModel, newItem: GarbModel): Boolean {
            return oldItem == newItem
        }


        override fun areContentsTheSame(oldItem: GarbModel, newItem: GarbModel): Boolean {
            return oldItem == newItem
        }
    }
}