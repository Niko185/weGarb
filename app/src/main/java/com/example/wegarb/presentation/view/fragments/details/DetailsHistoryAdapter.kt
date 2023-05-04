package com.example.wegarb.presentation.view.fragments.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wegarb.R
import com.example.wegarb.domain.models.old.WardrobeElement
import com.example.wegarb.databinding.ItemGarbDetailsBinding

class DetailsHistoryAdapter() : ListAdapter<WardrobeElement, DetailsHistoryAdapter.ItemHolderDetails>(
    ItemComparator()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolderDetails {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_garb_details, parent, false)
        return ItemHolderDetails(view)
    }

    override fun onBindViewHolder(holder: ItemHolderDetails, position: Int) {
        holder.setData(getItem(position))
    }

    class ItemHolderDetails(view: View): RecyclerView.ViewHolder(view) {
        private val binding = ItemGarbDetailsBinding.bind(view)

        fun setData(wardrobeElement: WardrobeElement) = with(binding) {
            imGarb.setImageResource(wardrobeElement.image)
            textNameGarb.text= wardrobeElement.name
        }

    }

    class ItemComparator(): DiffUtil.ItemCallback<WardrobeElement>() {
        override fun areItemsTheSame(oldItem: WardrobeElement, newItem: WardrobeElement): Boolean {
            return oldItem == newItem
        }


        override fun areContentsTheSame(oldItem: WardrobeElement, newItem: WardrobeElement): Boolean {
            return oldItem == newItem
        }
    }
}