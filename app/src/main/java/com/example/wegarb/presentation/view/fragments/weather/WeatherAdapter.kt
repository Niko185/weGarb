package com.example.wegarb.presentation.view.fragments.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wegarb.R
import com.example.wegarb.domain.models.old.WardrobeElement
import com.example.wegarb.databinding.ItemGarbBinding


class WeatherAdapter(private val listener: Listener) : ListAdapter<WardrobeElement, WeatherAdapter.ItemHolder>(
    ItemComparator()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder.create(parent, listener)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        return holder.setData(getItem(position))
    }
    class ItemHolder(view: View, private val listener: Listener): RecyclerView.ViewHolder(view) {
        private val binding = ItemGarbBinding.bind(view)


        fun setData(model: WardrobeElement) = with(binding) {

            tvGarb.text = model.name
            imageView.setImageResource(model.image)

            itemView.setOnClickListener {
                listener.onClickItem(model)
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

    interface Listener {
        fun onClickItem(wardrobeElement: WardrobeElement)
    }
}