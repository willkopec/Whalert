package com.willkopec.mvvmnewsappincompose.homescreen

import android.annotation.SuppressLint
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.willkopec.mvvmnewsappincompose.R
import com.willkopec.mvvmnewsappincompose.BottomNavigationItem
import com.willkopec.mvvmnewsappincompose.breakingnews.NewsViewModel
import com.willkopec.mvvmnewsappincompose.ui.theme.MVVMNewsAppInComposeTheme
import com.willkopec.mvvmnewsappincompose.util.BottomBarScreen
import com.willkopec.mvvmnewsappincompose.util.Constants.Companion.APP_NAME

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: NewsViewModel = hiltViewModel()
) {

    //var darkTheme by remember { viewModel.darkTheme }
    //val loadError by remember { viewModel.loadError }
    val darkTheme by viewModel.darkTheme.collectAsState()
    val loadError by viewModel.loadError.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    MVVMNewsAppInComposeTheme(darkTheme = darkTheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    /*navigationIcon = ,*/
                    title = { Text(text = APP_NAME) },
                    actions = {
                        ThemeSwitcher(
                            darkTheme = darkTheme,
                            size = 50.dp,
                            padding = 5.dp,
                            onClick = { viewModel.switchDarkMode() }
                        )
                    }
                )
            },
            bottomBar = { BottomNavigation(navController) },
        ) { scaffoldPadding ->
            Box(
                modifier =
                Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
                    .consumeWindowInsets(scaffoldPadding)
                    .systemBarsPadding(),
                contentAlignment = Alignment.BottomCenter
            ) {
                HomeNavGraph(navController = navController)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigation(navController: NavHostController) {

    val items =
        listOf(
            BottomNavigationItem(
                title = "Breaking News",
                selectedIcon = ImageVector.vectorResource(R.drawable.ic_breaking_news),
                unSelectedIcon = ImageVector.vectorResource(R.drawable.ic_breaking_news),
                hasNews = false
            ),
            BottomNavigationItem(
                title = "Saved News",
                selectedIcon = ImageVector.vectorResource(R.drawable.ic_favorite),
                unSelectedIcon = ImageVector.vectorResource(R.drawable.ic_favorite),
                hasNews = false,
            ),
            BottomNavigationItem(
                title = "Search News",
                selectedIcon = ImageVector.vectorResource(R.drawable.baseline_search_24),
                unSelectedIcon = ImageVector.vectorResource(R.drawable.baseline_search_24),
                hasNews = false,
            ),
        )

    val screens =
        listOf(
            BottomBarScreen.BreakingNews,
            BottomBarScreen.SavedNews,
            BottomBarScreen.SearchNews,
        )

    var selectedItemIndex by rememberSaveable { mutableStateOf(0) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarDestination = screens.any { it.route == currentDestination?.route }
    if (bottomBarDestination) {
        NavigationBar {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    /*selected = selectedItemIndex == index,*/
                    selected =
                    currentDestination?.hierarchy?.any { it.route == screens[index].route } ==
                            true,
                    onClick = {
                        navController.navigate(screens[index].route) {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    },
                    label = { Text(text = item.title) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (item.badgeCount != null) {
                                    Badge { Text(text = item.badgeCount.toString()) }
                                } else if (item.hasNews) {
                                    Badge()
                                }
                            }
                        ) {
                            Icon(
                                imageVector =
                                if (index == selectedItemIndex) {
                                    item.selectedIcon
                                } else item.unSelectedIcon,
                                contentDescription = item.title
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ThemeSwitcher(
    darkTheme: Boolean = false,
    size: Dp = 150.dp,
    iconSize: Dp = size / 3,
    padding: Dp = 10.dp,
    borderWidth: Dp = 1.dp,
    parentShape: Shape = CircleShape,
    toggleShape: Shape = CircleShape,
    animationSpec: AnimationSpec<Dp> = tween(durationMillis = 300),
    onClick: () -> Unit
) {

    val offset by
    animateDpAsState(
        targetValue = if (darkTheme) 0.dp else size,
        animationSpec = animationSpec,
        label = ""
    )

    Box(
        modifier =
        Modifier
            .width(size * 2)
            .height(size)
            .clip(shape = parentShape)
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Box(
            modifier =
            Modifier
                .size(size)
                .offset(x = offset)
                .padding(all = padding)
                .clip(shape = toggleShape)
                .background(MaterialTheme.colorScheme.primary)
        ) {}
        Row(
            modifier =
            Modifier.border(
                border =
                BorderStroke(
                    width = borderWidth,
                    color = MaterialTheme.colorScheme.primary
                ),
                shape = parentShape
            )
        ) {
            Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
                Icon(
                    modifier = Modifier.size(iconSize),
                    imageVector = Icons.Default.Nightlight,
                    contentDescription = "Theme Icon",
                    tint =
                    if (darkTheme) MaterialTheme.colorScheme.secondaryContainer
                    else MaterialTheme.colorScheme.primary
                )
            }
            Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
                Icon(
                    modifier = Modifier.size(iconSize),
                    imageVector = Icons.Default.LightMode,
                    contentDescription = "Theme Icon",
                    tint =
                    if (darkTheme) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondaryContainer
                )
            }
        }
    }
}

@Composable
fun RetrySection(error: String, onRetry: () -> Unit) {
    Column {
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = error,
            color = Color.Red,
            fontSize = 18.sp,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { onRetry() }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(text = "Retry")
        }
    }
}
