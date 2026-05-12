package com.hastakala.shop.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hastakala.shop.databinding.ItemQuickBillProductBinding
import com.hastakala.shop.models.ProductEntity
import com.hastakala.shop.utils.FormatUtils
import com.hastakala.shop.utils.ProductVisuals

class ProductQuickBillAdapter(
    private val onSelect: (ProductEntity) -> Unit
) : ListAdapter<ProductEntity, ProductQuickBillAdapter.ProductViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemQuickBillProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductViewHolder(
        private val binding: ItemQuickBillProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProductEntity) {
            binding.productIcon.setImageResource(ProductVisuals.iconFor(item.name, item.category))
            binding.productIcon.backgroundTintList = ColorStateList.valueOf(
                binding.root.context.getColor(ProductVisuals.iconSurfaceColor(item.name, item.category))
            )
            binding.productName.text = item.name
            binding.productCategory.text = item.category
            binding.productPrice.text = FormatUtils.currency(item.basePrice)
            binding.root.setOnClickListener { onSelect(item) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<ProductEntity>() {
        override fun areItemsTheSame(oldItem: ProductEntity, newItem: ProductEntity): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ProductEntity, newItem: ProductEntity): Boolean = oldItem == newItem
    }
}
