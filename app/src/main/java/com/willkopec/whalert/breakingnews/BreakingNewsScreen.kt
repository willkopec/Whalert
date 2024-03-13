package com.willkopec.whalert.breakingnews

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.willkopec.whalert.util.SortType
import com.willkopec.whalert.util.getAllTypes
import com.willkopec.whalert.util.getSortType

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