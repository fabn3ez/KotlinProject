package com.group5.track.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.group5.track.R
import com.group5.track.data.local.entity.PropertyEntity
import com.group5.track.databinding.ItemPropertyCardBinding

class PropertyListAdapter(
    private val onItemClick: (PropertyEntity) -> Unit
) : ListAdapter<PropertyEntity, PropertyListAdapter.PropertyViewHolder>(PropertyDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val binding = ItemPropertyCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PropertyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PropertyViewHolder(
        private val binding: ItemPropertyCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(property: PropertyEntity) {
            binding.tvTitle.text = property.title
            binding.tvAddress.text = property.address
            binding.tvPrice.text = "KES ${String.format("%,.0f", property.monthlyPrice)}/mo"
            binding.tvBedrooms.text = "${property.bedrooms} Beds"

            if (property.isAvailable) {
                binding.tvStatusBadge.text = "Available"
                binding.tvStatusBadge.setBackgroundColor(
                    ContextCompat.getColor(itemView.context, R.color.status_available_bg)
                )
                binding.tvStatusBadge.setTextColor(
                    ContextCompat.getColor(itemView.context, R.color.status_available_text)
                )
            } else {
                binding.tvStatusBadge.text = "Booked"
                binding.tvStatusBadge.setBackgroundColor(
                    ContextCompat.getColor(itemView.context, R.color.status_booked_bg)
                )
                binding.tvStatusBadge.setTextColor(
                    ContextCompat.getColor(itemView.context, R.color.status_booked_text)
                )
            }

            binding.root.setOnClickListener {
                onItemClick(property)
            }
        }
    }

    class PropertyDiffCallback : DiffUtil.ItemCallback<PropertyEntity>() {
        override fun areItemsTheSame(oldItem: PropertyEntity, newItem: PropertyEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PropertyEntity, newItem: PropertyEntity): Boolean {
            return oldItem == newItem
        }
    }
}