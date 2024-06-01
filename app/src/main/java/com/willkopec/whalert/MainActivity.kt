package com.willkopec.whalert

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.willkopec.whalert.breakingnews.WhalertViewModel
import com.willkopec.whalert.datastore.PreferenceDatastore
import com.willkopec.whalert.ui.homescreen.HomeScreen
import com.willkopec.whalert.util.Constants.Companion.APP_NAME
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var webView: WebView

    private val viewModel: WhalertViewModel by viewModels()

    @Inject
    lateinit var appSettings: PreferenceDatastore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)


        setContent {
            val navController = rememberNavController() // Remember the navController
            //val viewModel: WhalertViewModel = viewModel(factory = viewModelFactory)
            SplashScreen(navController = navController, viewModel = viewModel, webView = webView) {
                // Callback function to navigate to RootNavigationGraph
                //RootNavigationGraph()
                //navigateToRoot(navController)
            }
        }
    }

    private fun navigateToRoot(navController: NavHostController) {
        navController.navigate(Graph.HOME)
    }
}

@Composable
fun SplashScreen(
    navController: NavHostController,
    webView: WebView,
    viewModel: WhalertViewModel,
    onInitializationComplete: () -> Unit
) {
    val isInitialized by viewModel.isInitialized.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        if (isInitialized) {
            // Data is initialized, show the main UI
            RootNavigationGraph(navController = navController, webView = webView, viewModel = viewModel)
            onInitializationComplete()
        } else {
            // Data is not yet initialized, show a loading indicator or placeholder UI
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp)

            ) {
                Image(
                    painter = painterResource(R.drawable.designer),
                    contentDescription = APP_NAME,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .size(150.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun RootNavigationGraph(
    navController: NavHostController,
    webView: WebView,
    viewModel: WhalertViewModel
    ) {
    NavHost(
        navController = navController,
        route = Graph.ROOT,
        startDestination = Graph.HOME
    ) {
        composable(route = Graph.HOME) {
            HomeScreen(webView, viewModel = viewModel)
        }
    }
}

object Graph {
    const val ROOT = "root_graph"
    const val HOME = "home_graph"
    const val DETAILS = "details_graph"
}

