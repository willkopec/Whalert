package com.willkopec.whalert.ui.homescreen

import DraggableBubbleScreen
import android.webkit.WebView
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.squareup.moshi.Moshi
import com.willkopec.whalert.Graph
import com.willkopec.whalert.breakingnews.WhalertViewModel
import com.willkopec.whalert.model.newsAPI.Article
import com.willkopec.whalert.ui.chartscreen.ChartSymbolScreen
import com.willkopec.whalert.ui.favoriteslistscreen.FavoritesListScreen
import com.willkopec.whalert.ui.indicatorslistscreen.IndicatorsListScreenn
import com.willkopec.whalert.ui.newsscreen.ArticleScreen
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
            HomeScreen(webView, viewModel = viewModel, navController = navController,
                currentFragmentScreen = { DraggableBubbleScreen() }
            )
        }
        composable(route = BottomBarScreen.ChartsScreen.route) {
            HomeScreen(webView, viewModel = viewModel, navController = navController,
                currentFragmentScreen = { ChartSymbolScreen(timeScaleInDays = 100, bottomBarHeight = bottomBarHeight, darkMode = darkMode) }
            )
        }
        composable(route = BottomBarScreen.DashboardScreen.route) {
            HomeScreen(webView, viewModel = viewModel, navController = navController,
                currentFragmentScreen = { FavoritesListScreen(navController, darkMode) }
            )

        }

        composable(
            route = "${BottomBarScreen.ChartsScreen.route}/{indicator}",
            arguments = listOf(navArgument("indicator") { type = NavType.StringType })
        ) { backStackEntry ->
            val indicator = backStackEntry.arguments?.getString("indicator")

            if (indicator != null) {
                HomeScreen(webView, viewModel = viewModel, navController = navController,
                    currentFragmentScreen = { ChartSymbolScreen(timeScaleInDays = 100, bottomBarHeight = bottomBarHeight, currentIndicator = indicator, darkMode = darkMode) }
                )
            }
        }

        composable(route = DashboardNavigation.IndicatorsPage.route) {
            HomeScreen(webView, viewModel = viewModel, navController = navController, currentFragmentScreen = { IndicatorsListScreenn(navController=navController) })
        }

        composable(route = DashboardNavigation.DcaSimulator.route) {
            HomeScreen(webView, viewModel = viewModel, navController = navController, currentFragmentScreen = {  ChartSymbolScreen(timeScaleInDays = 100, bottomBarHeight = bottomBarHeight, currentIndicator="dca_simulator", darkMode = darkMode) })
        }
        composable(route = DashboardNavigation.NewsPage.route) {
            HomeScreen(webView, viewModel = viewModel, navController = navController, currentFragmentScreen = { BreakingNewsListScreen(navController = navController)})
        }
        composable(route = "${DashboardNavigation.NewsPage.route}/{article}") { backStackEntry ->
            val articleJson = backStackEntry.arguments?.getString("article")
            val moshi = Moshi.Builder().build()
            val jsonAdapter = moshi.adapter(Article::class.java).lenient()
            val currentArticle = jsonAdapter.fromJson(articleJson)

            // WebViewScreen(url = "https://www.google.com")
            if (currentArticle != null) {
                HomeScreen(webView, viewModel = viewModel, navController = navController, currentFragmentScreen = { ArticleScreen(currentArticle) } )
            }

            detailsNavGraph(navController = navController)
        }
        composable(route = DashboardNavigation.FeedbackPage.route) {
            HomeScreen(webView, viewModel = viewModel, navController = navController, currentFragmentScreen = { ChartSymbolScreen(timeScaleInDays = 100, bottomBarHeight = bottomBarHeight, currentIndicator="feedback", darkMode = darkMode) })
        }
        composable(route = DashboardNavigation.AnalyticsPage.route) {
            HomeScreen(webView, viewModel = viewModel, navController = navController, currentFragmentScreen = { ChartSymbolScreen(timeScaleInDays = 100, bottomBarHeight = bottomBarHeight, currentIndicator="monthly_gains_chart", darkMode = darkMode) })
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
