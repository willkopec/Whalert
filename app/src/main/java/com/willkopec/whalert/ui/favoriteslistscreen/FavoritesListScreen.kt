package com.willkopec.whalert.ui.favoriteslistscreen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.willkopec.whalert.BottomNavigationItem
import com.willkopec.whalert.R
import com.willkopec.whalert.breakingnews.WhalertViewModel
import com.willkopec.whalert.model.coinAPI.CoinAPIResultItem
import com.willkopec.whalert.model.coingecko.CryptoItem
import com.willkopec.whalert.util.BottomBarScreen
import com.willkopec.whalert.util.DashboardNavigation

@Composable
fun FavoritesListScreen(
    navController: NavHostController,
    viewModel: WhalertViewModel = hiltViewModel()
) {

    val scrollState = rememberLazyListState()
    val savedStocksList by viewModel.savedList.collectAsState()
    val indicatorDataList by viewModel.savedListData.collectAsState()
    val loadError by viewModel.loadError.collectAsState()

    if (loadError == "") {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            dashboardHeader(navController=navController)
            // Legend
            Legend()

            // LazyColumn
            LazyColumn(
                state = scrollState,
                modifier = Modifier.weight(1f)
            ) {
                val itemCount = savedStocksList.size

                items(itemCount) {
                    val cryptoInfo: CryptoItem? = viewModel.getSymbolDataPreview(savedStocksList.elementAt(it))
                    val indicatorData: List<CoinAPIResultItem>? = indicatorDataList[savedStocksList.elementAt(it)]

                    FavoritesListItem(
                        navController,
                        savedStocksList.elementAt(it),
                        cryptoInfo,
                        indicatorData = indicatorData,
                        modifier = Modifier,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun FavoritesListItem(
    navController: NavController,
    symbol: String,
    cryptoInfo: CryptoItem?,
    indicatorData: List<CoinAPIResultItem>?,
    modifier: Modifier = Modifier,
    viewModel: WhalertViewModel = hiltViewModel()
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(1.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween, // Adjusted horizontal arrangement
            verticalAlignment = Alignment.CenterVertically // Center items vertically
        ) {
            Column(
                modifier = Modifier
                    .weight(0.65f)
                    .padding(horizontal = 16.dp) // Add horizontal padding
            ) {
                Text(
                    text = symbol,
                    fontSize = 14.sp
                )
                if (cryptoInfo != null) {
                    Text(
                        text = cryptoInfo.name,
                        fontSize = 11.sp
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .padding(horizontal = 16.dp) // Add horizontal padding
            ) {
                if (indicatorData != null) {
                    indicatorData[0].current_risk?.let { RiskLevelChip(it) }
                }
            }

            Column(
                modifier = Modifier
                    .weight(0.7f)
                    .padding(horizontal = 16.dp) // Add horizontal padding
            ) {
                if (cryptoInfo != null) {
                    Text(
                        text = cryptoInfo.current_price.toString(),
                        fontSize = 14.sp
                    )
                }
                if (cryptoInfo != null) {
                    PercentageChangeChip(cryptoInfo.price_change_percentage_24h)
                }
            }

            Row(
                modifier=modifier.padding(end = 16.dp)
            ){
                Image(
                    painter = painterResource(R.drawable.baseline_remove_red_eye_24),
                    contentDescription = "Content description for visually impaired",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .size(30.dp)
                        /*.padding(end = 16.dp) // Add horizontal padding*/
                        .clickable {
                            viewModel.updateSymbolAndNavigate(symbol, navController)
                        }
                )
            }

        }
    }
}

@Composable
fun Legend(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Name",
            fontSize = 14.sp,
            modifier = Modifier
                .weight(0.65f)
                .padding(horizontal = 16.dp)
        )
        Text(
            text = "Risk Level",
            fontSize = 14.sp,
            modifier = Modifier
                .weight(0.4f)
                .padding(start = 16.dp, end = 8.dp) // Adjusted padding
        )
        Text(
            text = "Price",
            fontSize = 14.sp,
            modifier = Modifier
                .weight(0.7f)
                .padding(start = 48.dp, end = 8.dp) // Adjusted padding
        )
        Text(
            text = "View on Chart",
            fontSize = 10.sp,
            modifier = Modifier
                .padding(end = 16.dp)
        )
    }
}

@Composable
fun PercentageChangeChip(percent: Double){
    val color = if (percent >= 0) Color.Green else colorResource(R.color.red)
    Box(modifier = Modifier
        .size(width = 55.dp, height = 20.dp)
        .clip(RoundedCornerShape(5.dp))
        .background(color = color)
    ){
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "${String.format("%.2f", percent)}%",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun RiskLevelChip(riskLevel: Double){
    val color = if (riskLevel <= 0.35) Color.Green else if (riskLevel < 0.55) colorResource(R.color.chip_light_green) else if (riskLevel <= 0.75) colorResource(R.color.chip_yellow) else colorResource(R.color.red)
    var currentRisk: Double = riskLevel
    Box(modifier = Modifier
        .size(width = 55.dp, height = 20.dp)
        .clip(RoundedCornerShape(5.dp))
        .background(color = color)
    ){
        if(riskLevel >= 1.0){
            currentRisk = 1.0
        }
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "${String.format("%.2f", currentRisk)}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun dashboardHeader(navController: NavHostController){

    var dashboardItems: List<DashboardNavigation> =
    listOf(
        DashboardNavigation.IndicatorsPage,
        DashboardNavigation.DcaSimulator,
        DashboardNavigation.ToolsPage,
        DashboardNavigation.AnalyticsPage,
        DashboardNavigation.SentimentPage
    )

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(80.dp)
        .clip(RoundedCornerShape(5.dp))
        .background(color = Color.LightGray)
    ) {
        LazyColumn(contentPadding = PaddingValues(16.dp)) {

            val itemCount = dashboardItems.size / 5 + if (dashboardItems.size % 5 == 0) 0 else 1

            items(itemCount) {
                IndicatorRows(rowIndex = it, entries = dashboardItems, navController=navController)
            }

        }
    }
}

@Composable
fun IndicatorRows(
    rowIndex: Int,
    entries: List<DashboardNavigation>,
    navController: NavHostController
) {
    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            entries.subList(rowIndex * 5, minOf((rowIndex + 1) * 5, entries.size)).forEach { entry ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                        .clickable {
                            navController.navigate(entry.route) {
                                popUpTo(navController.graph.findStartDestination().id)
                                launchSingleTop = true
                            }
                            Log.d("NAVIGATION", "${entry.route}")
                        }
                ) {
                    Image(
                        painter = painterResource(entry.icon),
                        contentDescription = "Content description for visually impaired",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .size(30.dp)
                    )

                    Text(
                        text = entry.title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp) // Adjust top padding as needed
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

data class DashboardItem(
    val title: String,
    val selectedIcon: Int,
    val unSelectedIcon: Int
)



