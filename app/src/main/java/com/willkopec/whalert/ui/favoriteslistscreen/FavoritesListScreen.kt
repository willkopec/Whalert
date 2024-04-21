package com.willkopec.whalert.ui.favoriteslistscreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.squareup.moshi.Moshi
import com.willkopec.whalert.breakingnews.WhalertViewModel
import com.willkopec.whalert.model.coinAPI.CoinAPIResultItem
import com.willkopec.whalert.ui.chartscreen.BarChartExample

@Composable
fun FavoritesListScreen(
    navController: NavController,
    viewModel: WhalertViewModel = hiltViewModel()
) {

    val scrollState = rememberLazyListState()
    val savedStocksList by viewModel.savedList.collectAsState()
    val loadError by viewModel.loadError.collectAsState()

    if (loadError == "") {
        LazyColumn(
            state = scrollState,
            modifier = Modifier.fillMaxSize()
        ) {
            val itemCount = savedStocksList.size

            items(itemCount) {
                FavoritesListItem(
                    navController,
                    rowIndex = it,
                    entry = savedStocksList,
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
fun FavoritesListItem(
    navController: NavController,
    rowIndex: Int,
    entry: Set<String>,
    modifier: Modifier = Modifier,
    viewModel: WhalertViewModel = hiltViewModel()
) {

        Box(
            modifier =
            Modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(1.dp))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(0.3f)
                /*.align(Alignment.TopStart)*/
            ) {
                Text(text = entry.elementAt(rowIndex))
                //Text(text = entry.elementAt(rowIndex))
                //BarChartExample(entry.elementAt(rowIndex),10)

            }


            /*val modifierForText = Modifier
                    .fillMaxWidth(0.7f)
                    .align(Alignment.Center)

            Row(modifier = modifierForText) {
                Column {
                    if (entry[rowIndex].title != "[Removed]") {
                        entry[rowIndex].title?.let { it1 ->
                            Text(
                                text = it1.replace('+', ' '),
                                fontSize = 12.sp,
                                fontWeight = Bold,
                                lineHeight = 15.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 2
                            )
                        }

                        if (entry[rowIndex].description != null) {
                            entry[rowIndex].description?.let { it1 ->
                                Text(
                                    text = it1.replace('+', ' '),
                                    fontSize = 10.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 12.sp,
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 4
                                )
                            }
                        } else {
                            entry[rowIndex].title?.let { it1 ->
                                Text(
                                    text = it1.replace('+', ' '),
                                    fontSize = 9.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 11.sp,
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 4
                                )
                            }
                        }
                    }
                }
            }*/
        }
}




