package com.hastakala.shop.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FormatUtils {
    private val currency = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    private val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    fun currency(value: Double): String = currency.format(value)
    fun dateTime(value: Long): String = dateFormat.format(Date(value))
}
