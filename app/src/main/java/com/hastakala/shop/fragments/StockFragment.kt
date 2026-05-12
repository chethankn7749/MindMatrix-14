package com.hastakala.shop.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.hastakala.shop.adapters.StockAdapter
import com.hastakala.shop.databinding.DialogStockEditorBinding
import com.hastakala.shop.databinding.FragmentStockBinding
import com.hastakala.shop.models.InventoryEntity
import com.hastakala.shop.models.ProductEntity
import com.hastakala.shop.utils.HastaKalaApplication
import com.hastakala.shop.viewmodels.AppViewModel
import com.hastakala.shop.viewmodels.AppViewModelFactory

class StockFragment : Fragment() {

    private var _binding: FragmentStockBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: StockAdapter
    private val viewModel: AppViewModel by activityViewModels {
        AppViewModelFactory((requireActivity().application as HastaKalaApplication).salesRepository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStockBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = StockAdapter(
            onEdit = { showEditor(it) },
            onDelete = { showDeleteConfirmation(it) }
        )
        binding.stockRecycler.adapter = adapter
        binding.stockRecycler.itemAnimator = null
        binding.addStockButton.setOnClickListener { showEditor(null) }
        binding.searchInput.doOnTextChanged { text, _, _, _ ->
            val query = text.toString().trim()
            val items = viewModel.inventory.value.orEmpty().filter {
                it.productName.contains(query, true) || it.color.contains(query, true)
            }
            adapter.submitList(items)
        }
        viewModel.inventory.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            binding.emptyState.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun showEditor(item: InventoryEntity?) {
        val dialogBinding = DialogStockEditorBinding.inflate(layoutInflater)
        val products = viewModel.products.value.orEmpty()
        if (item != null) {
            dialogBinding.productNameInput.editText?.setText(item.productName)
            dialogBinding.colorInput.editText?.setText(item.color)
            dialogBinding.quantityInput.editText?.setText(item.quantity.toString())
            dialogBinding.reorderLevelInput.editText?.setText(item.reorderLevel.toString())
            products.firstOrNull { it.id == item.productId }?.let { product ->
                dialogBinding.categoryInput.editText?.setText(product.category)
                dialogBinding.basePriceInput.editText?.setText(product.basePrice.toString())
            }
        }
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(if (item == null) com.hastakala.shop.R.string.add_stock else com.hastakala.shop.R.string.edit_stock)
            .setView(dialogBinding.root)
            .setPositiveButton(com.hastakala.shop.R.string.save, null)
            .setNegativeButton(com.hastakala.shop.R.string.cancel, null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val name = dialogBinding.productNameInput.editText?.text.toString().trim()
                val category = dialogBinding.categoryInput.editText?.text.toString().trim()
                val basePrice = dialogBinding.basePriceInput.editText?.text.toString().toDoubleOrNull() ?: 0.0
                val color = dialogBinding.colorInput.editText?.text.toString().trim()
                val quantity = dialogBinding.quantityInput.editText?.text.toString().toIntOrNull() ?: 0
                val reorder = dialogBinding.reorderLevelInput.editText?.text.toString().toIntOrNull() ?: 3
                val matchedProduct = resolveProduct(products, name)
                if (name.isBlank() || color.isBlank() || quantity <= 0) {
                    Toast.makeText(requireContext(), com.hastakala.shop.R.string.complete_product_details, Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                if (matchedProduct == null && (category.isBlank() || basePrice <= 0.0)) {
                    Toast.makeText(requireContext(), com.hastakala.shop.R.string.match_existing_product, Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                viewModel.saveStockEntry(
                    existingItemId = item?.id,
                    productName = matchedProduct?.name ?: name,
                    category = matchedProduct?.category ?: category,
                    basePrice = matchedProduct?.basePrice ?: basePrice,
                    color = color,
                    quantity = quantity,
                    reorderLevel = reorder
                )
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun resolveProduct(products: List<ProductEntity>, name: String): ProductEntity? {
        return products.firstOrNull { it.name.equals(name.trim(), ignoreCase = true) }
    }

    private fun showDeleteConfirmation(item: InventoryEntity) {
        AlertDialog.Builder(requireContext())
            .setTitle(com.hastakala.shop.R.string.delete_stock_title)
            .setMessage(getString(com.hastakala.shop.R.string.delete_stock_message, item.productName, item.color))
            .setPositiveButton(com.hastakala.shop.R.string.delete) { _, _ ->
                viewModel.deleteStock(item.id)
            }
            .setNegativeButton(com.hastakala.shop.R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
