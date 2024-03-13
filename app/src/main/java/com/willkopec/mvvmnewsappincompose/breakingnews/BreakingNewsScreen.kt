package com.willkopec.mvvmnewsappincompose.breakingnews

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import coil.compose.SubcomposeAsyncImage
import com.willkopec.mvvmnewsappincompose.util.SortType
import com.willkopec.mvvmnewsappincompose.util.SwipeDirection
import com.willkopec.mvvmnewsappincompose.util.getAllTypes
import com.willkopec.mvvmnewsappincompose.util.getSortType
import com.willkopec.mvvmnewsappincompose.homescreen.RetrySection
import com.squareup.moshi.Moshi
import java.net.URLEncoder
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

/*
 *
 * BreakingNewsScreen: This file contains all of the needed composables for all
 * of the different sections in the main screen (Includes BreakingNewsScreen,
 * BreakingNewsListScreen, NewsArticleEntry, and the Chips for sorting news)
 *
 */

@Composable
fun BreakingNewsScreen(
    navController: NavController,
    name: String,
    onClick: () -> Unit,
    viewModel: NewsViewModel = hiltViewModel()
) {

    //val currentSortType by remember { viewModel.currentSortType }
    val currentSortType by viewModel.currentSortType.collectAsState()

    Column {
        ChipGroup(
            chips = getAllTypes(),
            selectedType = currentSortType,
            onSelectedChanged = {
                viewModel.setCurrentSortType(getSortType(it))
                viewModel.updateScrollToTop(true)
            }
        )

        Surface(modifier = Modifier.fillMaxSize()) { BreakingNewsListScreen(navController) }
    }
}

@Composable
fun BreakingNewsListScreen(
    navController: NavController,
    viewModel: NewsViewModel = hiltViewModel()
) {

    val scrollState = rememberLazyListState()
    val scrollToTop by viewModel.scrollToTop.observeAsState()
    //val currentNews by remember { viewModel.currentNews }
    //val loadError by remember { viewModel.loadError }
    val loadError by viewModel.loadError.collectAsState()

    LaunchedEffect(
        key1 = scrollToTop,
    ) {
        if (scrollToTop == true) {
            scrollState.scrollToItem(0)
            viewModel.updateScrollToTop(false)
        }
    }

}

@Composable
fun Chip(
    name: String = "Chip",
    isSelected: Boolean = false,
    onSelectionChanged: (String) -> Unit = {},
) {
    Surface(
        modifier = Modifier.padding(1.dp),
        shape = MaterialTheme.shapes.small,
        color =
        if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.secondary
    ) {
        Row(
            modifier =
            Modifier.toggleable(
                value = isSelected,
                onValueChange = { onSelectionChanged(name) }
            )
        ) {
            Text(
                text = name,
                /*color = MaterialTheme.colorScheme.,*/
                fontSize = 14.sp,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Composable
fun ChipGroup(
    chips: List<SortType> = getAllTypes(),
    selectedType: SortType? = null,
    viewModel: NewsViewModel = hiltViewModel(),
    onSelectedChanged: (String) -> Unit = {},
) {

    Column(
        modifier =
        Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.primaryContainer)
    ) {
        LazyRow(modifier = Modifier) {
            items(chips) {
                Chip(
                    name = it.value,
                    isSelected = selectedType == it,
                    onSelectionChanged = { onSelectedChanged(it) },
                )
            }
        }
    }
}

@Composable
fun NewsArticleEntry(
    navController: NavController,
    rowIndex: Int,
    modifier: Modifier = Modifier,
    viewModel: NewsViewModel = hiltViewModel()
) {

    Column {
        Box(
            modifier =
            Modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(1.dp))
                /*.padding(13.dp)*/
                .clickable {

                }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(0.3f)
                /*.align(Alignment.TopStart)*/
            ) {
                Column {

                }
            }


            Row(modifier = Modifier) {
                Column {

                }
            }
        }
    }
}