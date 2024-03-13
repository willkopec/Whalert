package com.willkopec.mvvmnewsappincompose.homescreen

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
import com.willkopec.mvvmnewsappincompose.Graph
import com.willkopec.mvvmnewsappincompose.breakingnews.BreakingNewsScreen
import com.willkopec.mvvmnewsappincompose.util.BottomBarScreen
import com.squareup.moshi.Moshi

/*
 * ------------------------------------
 * HomeNavGraph: Contains the NavHost
 * and different route names/composables
 * ------------------------------------
 */

@Composable
fun HomeNavGraph(navController: NavHostController) {

    val snackbarHostState = remember { SnackbarHostState() }

    NavHost(
        navController = navController,
        route = Graph.HOME,
        startDestination = BottomBarScreen.BreakingNews.route
    ) {
        composable(route = BottomBarScreen.BreakingNews.route) {
            BreakingNewsScreen(
                navController,
                name = BottomBarScreen.BreakingNews.route,
                onClick = { /*TODO*/}
            )
        }
        composable(route = BottomBarScreen.SavedNews.route) {
            ScreenContent(
                name = BottomBarScreen.SavedNews.route,
                onClick = {}
            )
        }
        composable(route = BottomBarScreen.SearchNews.route) {
            ScreenContent(
                name = BottomBarScreen.SavedNews.route,
                onClick = {}
            )
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