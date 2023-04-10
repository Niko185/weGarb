package com.example.wegarb.presentation.view.adapters

import android.app.Application
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wegarb.R
import com.example.wegarb.data.models.GarbModel
import com.example.wegarb.databinding.ItemGarbBinding


class GarbAdapter(private val listener: Listener) : ListAdapter<GarbModel, GarbAdapter.ItemHolder>(ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder.create(parent, listener)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        return holder.setData(getItem(position))
    }
    class ItemHolder(view: View, private val listener: Listener): RecyclerView.ViewHolder(view) {
        private val binding = ItemGarbBinding.bind(view)


        fun setData(model: GarbModel) = with(binding) {

            tvGarb.text = model.nameGarb
            imageView.setImageResource(model.imageGarb)

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

    class ItemComparator: DiffUtil.ItemCallback<GarbModel>() {
        override fun areItemsTheSame(oldItem: GarbModel, newItem: GarbModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: GarbModel, newItem: GarbModel): Boolean {
            return oldItem == newItem
        }

    }

    interface Listener {
        fun onClickItem(garbModel: GarbModel)
    }
}