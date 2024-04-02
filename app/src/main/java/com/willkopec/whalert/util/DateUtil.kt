package com.willkopec.whalert.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtil {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun getCurrentDate(): String {
        return dateFormat.format(Date())
    }

    fun getDateBeforeDays(days: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        return dateFormat.format(calendar.time)
    }

    fun getDateBeforeMonths(months: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -months)
        return dateFormat.format(calendar.time)
    }

    fun getDateBeforeYears(years: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -years)
        return dateFormat.format(calendar.time)
    }
}