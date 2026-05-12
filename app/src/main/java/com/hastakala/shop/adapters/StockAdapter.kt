package com.hastakala.shop.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hastakala.shop.databinding.ItemStockBinding
import com.hastakala.shop.models.InventoryEntity
import com.hastakala.shop.utils.ProductVisuals

class StockAdapter(
    private val onEdit: (InventoryEntity) -> Unit,
    private val onDelete: (InventoryEntity) -> Unit
) : ListAdapter<InventoryEntity, StockAdapter.StockViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val binding = ItemStockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StockViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StockViewHolder(
        private val binding: ItemStockBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InventoryEntity) {
            binding.productIcon.setImageResource(ProductVisuals.iconFor(item.productName, null))
            binding.productIconShell.backgroundTintList = ColorStateList.valueOf(
                binding.root.context.getColor(ProductVisuals.iconSurfaceColor(item.productName, null))
            )
            binding.productName.text = item.productName
            binding.productDetail.text = "${item.color} - Qty ${item.quantity}"
            binding.warningBadge.visibility = if (item.quantity <= maxOf(item.reorderLevel, 3)) View.VISIBLE else View.GONE
            binding.warningBadge.text = "Only ${item.quantity} ${item.color} left"
            binding.editButton.setOnClickListener { onEdit(item) }
            binding.deleteButton.setOnClickListener { onDelete(item) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<InventoryEntity>() {
        override fun areItemsTheSame(oldItem: InventoryEntity, newItem: InventoryEntity): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: InventoryEntity, newItem: InventoryEntity): Boolean = oldItem == newItem
    }
}
