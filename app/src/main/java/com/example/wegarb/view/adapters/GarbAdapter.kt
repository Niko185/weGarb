package com.example.wegarb.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wegarb.R
import com.example.wegarb.data.GarbModel
import com.example.wegarb.databinding.ItemGarbBinding
import com.squareup.picasso.Picasso


class GarbAdapter : ListAdapter<GarbModel, GarbAdapter.ItemHolder>(ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        return holder.setData(getItem(position))
    }
    class ItemHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = ItemGarbBinding.bind(view)

        fun setData(model: GarbModel) = with(binding) {
            tvGarb.text = model.nameGarb
            imageView.setImageResource(model.imageGarb)

        }

        companion object {
            fun create(parent: ViewGroup): ItemHolder {
                return ItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_garb, parent, false))
            }
        }
    }

    class ItemComparator: DiffUtil.ItemCallback<GarbModel>() {
        override fun areItemsTheSame(oldItem: GarbModel, newItem: GarbModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: GarbModel, newItem: GarbModel): Boolean {
            return oldItem == newItem
        }

    }
}