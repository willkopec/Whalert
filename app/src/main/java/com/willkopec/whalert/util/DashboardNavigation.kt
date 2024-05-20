package com.willkopec.whalert.util

import com.willkopec.whalert.R

sealed class DashboardNavigation(
    val route: String,
    val title: String,
    val icon: Int
) {

    object IndicatorsPage : DashboardNavigation(
        title = "Indicators",
        route = "indicators",
        icon = R.drawable.baseline_line_axis_24
    )
    object DcaSimulator : DashboardNavigation(
        title = "DCA Sim",
        route = "dcasim",
        icon = R.drawable.baseline_price_check_24
    )
    object AnalyticsPage : DashboardNavigation(
        title = "Analytics",
        route = "analytics",
        icon = R.drawable.baseline_query_stats_24
    )
    object ToolsPage : DashboardNavigation(
        title = "Tools",
        route = "tools",
        icon = R.drawable.baseline_build_24
    )
    object FeedbackPage : DashboardNavigation(
        title = "Feedback",
        route = "feedback",
        icon = R.drawable.baseline_textsms_24
    )
}