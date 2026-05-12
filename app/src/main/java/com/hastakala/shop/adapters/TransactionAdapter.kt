package com.hastakala.shop.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hastakala.shop.databinding.ItemTransactionBinding
import com.hastakala.shop.models.SaleEntity
import com.hastakala.shop.utils.FormatUtils

class TransactionAdapter : ListAdapter<SaleEntity, TransactionAdapter.TransactionViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TransactionViewHolder(
        private val binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SaleEntity) {
            binding.productName.text = item.productName
            binding.transactionMeta.text = "${item.color} • Qty ${item.quantity}"
            binding.transactionAmount.text = FormatUtils.currency(item.totalAmount)
            binding.transactionTime.text = FormatUtils.dateTime(item.createdAt)
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<SaleEntity>() {
        override fun areItemsTheSame(oldItem: SaleEntity, newItem: SaleEntity): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: SaleEntity, newItem: SaleEntity): Boolean = oldItem == newItem
    }
}
