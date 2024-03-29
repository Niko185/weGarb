package com.example.wegarb.presentation.view.fragments.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wegarb.R
import com.example.wegarb.domain.models.cloth.single_wardrobe_element.WardrobeElement
import com.example.wegarb.databinding.ItemGarbBinding


class WeatherAdapter(private val listener: Listener) : ListAdapter<WardrobeElement, WeatherAdapter.ItemHolder>(ItemComparator()) {

    class ItemHolder(view: View, private val listener: Listener): RecyclerView.ViewHolder(view) {
        private val binding = ItemGarbBinding.bind(view)

        fun setData(wardrobeElement: WardrobeElement) = with(binding) {

            tvGarb.text = wardrobeElement.name
            imageView.setImageResource(wardrobeElement.image)

            itemView.setOnClickListener {
                listener.onClickItemInRecyclerView(wardrobeElement)
            }
        }

        companion object {
            fun create(parent: ViewGroup, listener: Listener): ItemHolder {
                return ItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_garb, parent, false), listener)
            }
        }
    }

    class ItemComparator: DiffUtil.ItemCallback<WardrobeElement>() {
        override fun areItemsTheSame(oldItem: WardrobeElement, newItem: WardrobeElement): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WardrobeElement, newItem: WardrobeElement): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder.create(parent, listener)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        return holder.setData(getItem(position))
    }

    interface Listener {
        fun onClickItemInRecyclerView(wardrobeElement: WardrobeElement)
    }
}