package com.willkopec.whalert.ui.homescreen

import DraggableBubbleScreen
import android.webkit.WebView
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.willkopec.whalert.Graph
import com.willkopec.whalert.breakingnews.WhalertViewModel
import com.willkopec.whalert.ui.chartscreen.ChartSymbolScreen
import com.willkopec.whalert.ui.favoriteslistscreen.FavoritesListScreen
import com.willkopec.whalert.ui.indicatorslistscreen.IndicatorsListScreenn
import com.willkopec.whalert.ui.newsscreen.BreakingNewsListScreen
import com.willkopec.whalert.util.BottomBarScreen
import com.willkopec.whalert.util.DashboardNavigation

/*
 * ------------------------------------
 * HomeNavGraph: Contains the NavHost
 * and different route names/composables
 * ------------------------------------
 */

@Composable
fun HomeNavGraph(
    navController: NavHostController,
    bottomBarHeight: Int,
    webView: WebView,
    viewModel: WhalertViewModel,
    darkMode: Boolean
    ) {

    val snackbarHostState = remember { SnackbarHostState() }

    NavHost(
        navController = navController,
        route = Graph.HOME,
        startDestination = BottomBarScreen.BubbleCharts.route
    ) {
        composable(route = BottomBarScreen.BubbleCharts.route) {
            DraggableBubbleScreen(bottomBarHeight = bottomBarHeight, viewModel = viewModel)
        }
        composable(route = BottomBarScreen.ChartsScreen.route) {
            ChartSymbolScreen(timeScaleInDays = 100, bottomBarHeight = bottomBarHeight, darkMode = darkMode)
        }
        composable(
            route = "${BottomBarScreen.ChartsScreen.route}/{indicator}",
            arguments = listOf(navArgument("indicator") { type = NavType.StringType })
        ) { backStackEntry ->
            val indicator = backStackEntry.arguments?.getString("indicator")

            if (indicator != null) {
                ChartSymbolScreen(timeScaleInDays = 100, bottomBarHeight = bottomBarHeight, currentIndicator = indicator, darkMode = darkMode)
            }
        }
        composable(route = BottomBarScreen.DashboardScreen.route) {
            FavoritesListScreen(navController)
        }

        composable(route = DashboardNavigation.IndicatorsPage.route) {
            IndicatorsListScreenn(navController=navController)
        }

        composable(route = DashboardNavigation.DcaSimulator.route) {
            ChartSymbolScreen(timeScaleInDays = 100, bottomBarHeight = bottomBarHeight, currentIndicator="dca_simulator", darkMode = darkMode)
        }
        composable(route = DashboardNavigation.ToolsPage.route) {
            BreakingNewsListScreen(navController = navController)
        }
        composable(route = DashboardNavigation.FeedbackPage.route) {
            ChartSymbolScreen(timeScaleInDays = 100, bottomBarHeight = bottomBarHeight, currentIndicator="feedback", darkMode = darkMode)
        }
        composable(route = DashboardNavigation.AnalyticsPage.route) {
            ChartSymbolScreen(timeScaleInDays = 100, bottomBarHeight = bottomBarHeight, currentIndicator="monthly_gains_chart", darkMode = darkMode)
        }

        detailsNavGraph(navController = navController)
    }
}

fun NavGraphBuilder.detailsNavGraph(navController: NavHostController) {
    navigation(route = Graph.DETAILS, startDestination = DetailsScreen.Information.route) {
        composable(route = DetailsScreen.Information.route) {
            ScreenContent(name = DetailsScreen.Information.route) {
                navController.navigate(DetailsScreen.Overview.route)
            }
        }
        composable(route = DetailsScreen.Overview.route) {
            ScreenContent(name = DetailsScreen.Overview.route) {
                navController.popBackStack(
                    route = DetailsScreen.Information.route,
                    inclusive = false
                )
            }
        }
    }
}

sealed class DetailsScreen(val route: String) {
    object Information : DetailsScreen(route = "INFORMATION")
    object Overview : DetailsScreen(route = "OVERVIEW")
}
