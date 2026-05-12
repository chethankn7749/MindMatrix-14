package com.hastakala.shop.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.children
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import com.hastakala.shop.databinding.FragmentQuickBillBinding
import com.hastakala.shop.models.ProductEntity
import com.hastakala.shop.utils.FormatUtils
import com.hastakala.shop.utils.HastaKalaApplication
import com.hastakala.shop.utils.ProductVisuals
import com.hastakala.shop.viewmodels.AppViewModel
import com.hastakala.shop.viewmodels.AppViewModelFactory

class QuickBillFragment : Fragment() {

    private var _binding: FragmentQuickBillBinding? = null
    private val binding get() = _binding!!
    private var selectedProduct: ProductEntity? = null
    private var selectedColor: String? = null
    private var quantity = 1

    private val viewModel: AppViewModel by activityViewModels {
        AppViewModelFactory((requireActivity().application as HastaKalaApplication).salesRepository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentQuickBillBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.productSelectorCard.setOnClickListener { showProductPicker() }

        binding.quantityInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                quantity = s?.toString()?.toIntOrNull()?.coerceAtLeast(1) ?: 1
                binding.quantityInputLayout.error = if (s.isNullOrBlank()) {
                    getString(com.hastakala.shop.R.string.required_field)
                } else {
                    null
                }
            }
        })

        viewModel.products.observe(viewLifecycleOwner) { products ->
            val selectedId = selectedProduct?.id
            val updatedSelection = products.firstOrNull { it.id == selectedId } ?: products.firstOrNull()
            if (updatedSelection != null) {
                updateSelectedProduct(updatedSelection)
            } else {
                selectedProduct = null
                selectedColor = null
                binding.selectedProductIcon.setImageDrawable(null)
                binding.selectedProductPickerText.text = getString(com.hastakala.shop.R.string.tap_to_choose_product)
                binding.selectedProductName.text = ""
                binding.selectedProductPrice.text = ""
                binding.colorChipGroup.removeAllViews()
            }
        }

        viewModel.inventory.observe(viewLifecycleOwner) {
            syncColors()
        }

        binding.saveSaleButton.setOnClickListener {
            val product = selectedProduct
            val color = selectedColor
            if (product == null || color == null) {
                Toast.makeText(requireContext(), com.hastakala.shop.R.string.select_product_and_color, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (quantity < 1) {
                binding.quantityInputLayout.error = getString(com.hastakala.shop.R.string.enter_valid_quantity)
                return@setOnClickListener
            }
            viewModel.recordSale(product, color, quantity)
        }
    }

    private fun showProductPicker() {
        val products = viewModel.products.value.orEmpty()
        if (products.isEmpty()) {
            Toast.makeText(requireContext(), com.hastakala.shop.R.string.no_products_available, Toast.LENGTH_SHORT).show()
            return
        }
        val labels = products.map { "${it.name} - ${FormatUtils.currency(it.basePrice)}" }.toTypedArray()
        AlertDialog.Builder(requireContext())
            .setTitle(com.hastakala.shop.R.string.select_product)
            .setItems(labels) { _, which ->
                updateSelectedProduct(products[which])
            }
            .show()
    }

    private fun updateSelectedProduct(product: ProductEntity) {
        selectedProduct = product
        binding.selectedProductPickerText.text = product.name
        binding.selectedProductIcon.setImageResource(ProductVisuals.iconFor(product.name, product.category))
        binding.selectedProductIconShell.backgroundTintList = android.content.res.ColorStateList.valueOf(
            requireContext().getColor(ProductVisuals.iconSurfaceColor(product.name, product.category))
        )
        binding.selectedProductName.text = product.name
        binding.selectedProductPrice.text = FormatUtils.currency(product.basePrice)
        syncColors()
    }

    private fun syncColors() {
        val product = selectedProduct ?: return
        val inventory = viewModel.inventory.value.orEmpty().filter { it.productId == product.id }
        binding.colorChipGroup.removeAllViews()
        selectedColor = null
        inventory.forEachIndexed { index, item ->
            val chip = Chip(requireContext()).apply {
                text = "${item.color} (${item.quantity})"
                isCheckable = true
                isChecked = index == 0
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) selectedColor = item.color
                }
            }
            binding.colorChipGroup.addView(chip)
            if (index == 0) selectedColor = item.color
        }
        binding.colorChipGroup.children.firstOrNull()?.let { first ->
            first.performClick()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
