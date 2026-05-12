package com.hastakala.shop.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.hastakala.shop.R
import com.hastakala.shop.adapters.TransactionAdapter
import com.hastakala.shop.databinding.ActivityIncomeLogBinding
import com.hastakala.shop.models.IncomeFilter
import com.hastakala.shop.utils.FormatUtils
import com.hastakala.shop.utils.HastaKalaApplication
import com.hastakala.shop.utils.LocalizedActivity
import com.hastakala.shop.viewmodels.AppViewModel
import com.hastakala.shop.viewmodels.AppViewModelFactory

class IncomeLogActivity : LocalizedActivity() {

    private lateinit var binding: ActivityIncomeLogBinding
    private lateinit var adapter: TransactionAdapter
    private val viewModel: AppViewModel by viewModels {
        AppViewModelFactory((application as HastaKalaApplication).salesRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncomeLogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        applyToolbarInsets()

        adapter = TransactionAdapter()
        binding.transactionRecycler.adapter = adapter
        binding.transactionRecycler.itemAnimator = null

        val filterLabels = listOf(
            getString(R.string.today),
            getString(R.string.this_week),
            getString(R.string.this_month)
        )
        binding.filterDropdown.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, filterLabels))
        binding.filterDropdown.setText(filterLabels[1], false)
        binding.filterDropdown.setOnItemClickListener { _, _, position, _ ->
            viewModel.setIncomeFilter(
                when (position) {
                    0 -> IncomeFilter.TODAY
                    1 -> IncomeFilter.THIS_WEEK
                    else -> IncomeFilter.THIS_MONTH
                }
            )
        }

        viewModel.incomeSummary.observe(this) { summary ->
            binding.totalRevenueValue.text = FormatUtils.currency(summary.totalRevenue)
            binding.salesCountValue.text = summary.totalSales.toString()
            binding.earningsBreakdown.text = summary.productWiseEarnings.entries.joinToString("\n") {
                "${it.key}: ${FormatUtils.currency(it.value)}"
            }.ifBlank { getString(R.string.no_income_data) }
            adapter.submitList(summary.transactions)
            binding.emptyState.isVisible = summary.transactions.isEmpty()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        overridePendingTransition(R.anim.nav_pop_enter, R.anim.nav_pop_exit)
        return true
    }

    private fun applyToolbarInsets() {
        val baseHeight = resources.getDimensionPixelSize(R.dimen.top_app_bar_height)
        val start = binding.toolbar.paddingStart
        val end = binding.toolbar.paddingEnd
        val bottom = binding.toolbar.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { view, insets ->
            val topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updateLayoutParams {
                height = baseHeight + topInset
            }
            view.setPadding(start, topInset, end, bottom)
            insets
        }
    }
}
