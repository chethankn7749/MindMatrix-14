package com.hastakala.shop.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.hastakala.shop.activities.IncomeLogActivity
import com.hastakala.shop.R
import com.hastakala.shop.databinding.FragmentHomeBinding
import com.hastakala.shop.utils.FormatUtils
import com.hastakala.shop.utils.HastaKalaApplication
import com.hastakala.shop.viewmodels.AppViewModel
import com.hastakala.shop.viewmodels.AppViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AppViewModel by activityViewModels {
        AppViewModelFactory((requireActivity().application as HastaKalaApplication).salesRepository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.incomeLogButton.setOnClickListener {
            startActivity(Intent(requireContext(), IncomeLogActivity::class.java))
            requireActivity().overridePendingTransition(R.anim.nav_enter, R.anim.nav_exit)
        }
        bindTitles()
        viewModel.dashboard.observe(viewLifecycleOwner) { summary ->
            binding.weeklyIncomeValue.text = FormatUtils.currency(summary.weeklyIncome)
            binding.weeklyIncomeMirrorValue.text = FormatUtils.currency(summary.weeklyIncome)
            binding.monthlyIncomeValue.text = FormatUtils.currency(summary.monthlyIncome)
            binding.bestSellerValue.text = summary.bestSellingProduct
            binding.lowStockValue.text = summary.lowStockCount.toString()
            binding.salesSummaryValue.text = summary.salesSummary
            binding.totalSoldValue.text = summary.totalProductsSold.toString()
        }
    }

    private fun bindTitles() {
        binding.weeklyIncomeTitle.text = getString(R.string.weekly_income)
        binding.monthlyIncomeTitle.text = getString(R.string.monthly_income)
        binding.bestSellerTitle.text = getString(R.string.best_selling_product)
        binding.lowStockTitle.text = getString(R.string.low_stock_alerts)
        binding.salesSummaryTitle.text = getString(R.string.sales_summary)
        binding.totalSoldTitle.text = getString(R.string.total_products_sold)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
