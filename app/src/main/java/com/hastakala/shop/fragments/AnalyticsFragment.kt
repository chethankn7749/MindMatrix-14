package com.hastakala.shop.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.hastakala.shop.R
import com.hastakala.shop.databinding.FragmentAnalyticsBinding
import com.hastakala.shop.utils.HastaKalaApplication
import com.hastakala.shop.viewmodels.AppViewModel
import com.hastakala.shop.viewmodels.AppViewModelFactory

class AnalyticsFragment : Fragment() {

    private var _binding: FragmentAnalyticsBinding? = null
    private val binding get() = _binding!!
    private var hasAnimatedCharts = false
    private val viewModel: AppViewModel by activityViewModels {
        AppViewModelFactory((requireActivity().application as HastaKalaApplication).salesRepository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stylePie(binding.productPieChart)
        stylePie(binding.colorPieChart)
        styleBar(binding.revenueBarChart)
        styleBar(binding.performanceBarChart)

        viewModel.analytics.observe(viewLifecycleOwner) { analytics ->
            binding.emptyState.visibility = if (analytics.productSales.isEmpty()) View.VISIBLE else View.GONE

            binding.productPieChart.data = PieData(
                PieDataSet(
                    analytics.productSales.map { PieEntry(it.value, it.key) },
                    "Products"
                ).apply {
                    colors = listOf(
                        requireContext().getColor(R.color.phonepe_mid),
                        requireContext().getColor(R.color.amber_secondary),
                        requireContext().getColor(R.color.phonepe_deep),
                        requireContext().getColor(R.color.success_green)
                    )
                    valueTextColor = Color.WHITE
                    valueTextSize = 12f
                    sliceSpace = 3f
                }
            )

            binding.colorPieChart.data = PieData(
                PieDataSet(
                    analytics.colorSales.map { PieEntry(it.value, it.key) },
                    "Colors"
                ).apply {
                    colors = listOf(
                        requireContext().getColor(R.color.phonepe_mid),
                        requireContext().getColor(R.color.amber_secondary),
                        requireContext().getColor(R.color.alert_red),
                        requireContext().getColor(R.color.success_green)
                    )
                    valueTextColor = Color.WHITE
                    valueTextSize = 12f
                    sliceSpace = 3f
                }
            )

            binding.revenueBarChart.data = BarData(
                BarDataSet(
                    analytics.weeklyRevenue.mapIndexed { index, value -> BarEntry(index.toFloat(), value) },
                    "Weekly Revenue"
                ).apply {
                    colors = listOf(
                        requireContext().getColor(R.color.phonepe_mid),
                        requireContext().getColor(R.color.phonepe_deep),
                        requireContext().getColor(R.color.amber_secondary),
                        requireContext().getColor(R.color.success_green)
                    )
                    valueTextColor = requireContext().getColor(R.color.text_primary)
                    valueTextSize = 11f
                }
            )

            binding.performanceBarChart.data = BarData(
                BarDataSet(
                    analytics.productPerformance.entries.mapIndexed { index, entry ->
                        BarEntry(index.toFloat(), entry.value)
                    },
                    "Product Performance"
                ).apply {
                    colors = listOf(
                        requireContext().getColor(R.color.phonepe_mid),
                        requireContext().getColor(R.color.amber_secondary),
                        requireContext().getColor(R.color.phonepe_deep),
                        requireContext().getColor(R.color.success_green)
                    )
                    valueTextColor = requireContext().getColor(R.color.text_primary)
                    valueTextSize = 11f
                }
            )

            listOf(
                binding.productPieChart,
                binding.colorPieChart,
                binding.revenueBarChart,
                binding.performanceBarChart
            ).forEach { it.invalidate() }

            if (!hasAnimatedCharts) {
                binding.productPieChart.animateY(380)
                binding.colorPieChart.animateY(380)
                binding.revenueBarChart.animateY(380)
                binding.performanceBarChart.animateY(380)
                hasAnimatedCharts = true
            }
        }
    }

    private fun stylePie(chart: PieChart) {
        chart.description.isEnabled = false
        chart.isDrawHoleEnabled = true
        chart.setUsePercentValues(false)
        chart.centerText = "Live\nSales"
        chart.setCenterTextColor(requireContext().getColor(R.color.phonepe_deep))
        chart.setCenterTextSize(16f)
        chart.setDrawEntryLabels(false)
        chart.holeRadius = 64f
        chart.transparentCircleRadius = 70f
        chart.setHoleColor(requireContext().getColor(R.color.card_white))
        chart.setTransparentCircleColor(requireContext().getColor(R.color.surface_soft))
        chart.legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        chart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        chart.legend.orientation = Legend.LegendOrientation.HORIZONTAL
        chart.legend.setDrawInside(false)
        chart.legend.textColor = requireContext().getColor(R.color.text_secondary)
        chart.legend.textSize = 11f
        chart.setExtraOffsets(8f, 8f, 8f, 12f)
    }

    private fun styleBar(chart: BarChart) {
        chart.description.isEnabled = false
        chart.setFitBars(true)
        chart.axisRight.isEnabled = false
        chart.legend.isEnabled = false
        chart.setDrawGridBackground(false)
        chart.setScaleEnabled(false)
        chart.axisLeft.textColor = requireContext().getColor(R.color.text_secondary)
        chart.xAxis.textColor = requireContext().getColor(R.color.text_secondary)
        chart.axisLeft.gridColor = requireContext().getColor(R.color.phonepe_surface_alt)
        chart.xAxis.gridColor = requireContext().getColor(R.color.phonepe_surface_alt)
        chart.axisLeft.axisLineColor = requireContext().getColor(R.color.phonepe_surface_alt)
        chart.xAxis.axisLineColor = requireContext().getColor(R.color.phonepe_surface_alt)
        chart.setExtraOffsets(0f, 10f, 10f, 8f)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hasAnimatedCharts = false
        _binding = null
    }
}
