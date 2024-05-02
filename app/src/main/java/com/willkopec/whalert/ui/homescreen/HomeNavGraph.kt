package com.willkopec.whalert.ui.homescreen

import DraggableBubbleScreen
import android.webkit.WebView
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.willkopec.whalert.Graph
import com.willkopec.whalert.breakingnews.WhalertViewModel
import com.willkopec.whalert.ui.chartscreen.BarChartExample
import com.willkopec.whalert.ui.chartscreen.ChartSymbolScreen
import com.willkopec.whalert.ui.favoriteslistscreen.FavoritesListScreen
import com.willkopec.whalert.util.BottomBarScreen

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
    viewModel: WhalertViewModel
    ) {

    val snackbarHostState = remember { SnackbarHostState() }

    NavHost(
        navController = navController,
        route = Graph.HOME,
        startDestination = BottomBarScreen.BreakingNews.route
    ) {
        composable(route = BottomBarScreen.BreakingNews.route) {
            DraggableBubbleScreen(bottomBarHeight = bottomBarHeight, viewModel = viewModel)
        }
        composable(route = BottomBarScreen.SavedNews.route) {
            ChartSymbolScreen(timeScaleInDays = 100, bottomBarHeight = bottomBarHeight)
        }
        composable(route = BottomBarScreen.SearchNews.route) {
            FavoritesListScreen(navController)
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
