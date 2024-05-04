package com.willkopec.whalert.ui.favoriteslistscreen

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.willkopec.whalert.BottomNavigationItem
import com.willkopec.whalert.R
import com.willkopec.whalert.breakingnews.WhalertViewModel
import com.willkopec.whalert.model.coinAPI.CoinAPIResultItem
import com.willkopec.whalert.model.coingecko.CryptoItem

@Composable
fun FavoritesListScreen(
    navController: NavController,
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
            dashboardHeader()
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
fun dashboardHeader(){

    var dashboardItems: List<DashboardItem> = listOf(
        DashboardItem(
        title = "Indicators",
        selectedIcon = R.drawable.baseline_line_axis_24,
        unSelectedIcon = R.drawable.baseline_line_axis_24
        ),
        DashboardItem(
            title = "DCA Simulator",
            selectedIcon = R.drawable.baseline_price_check_24,
            unSelectedIcon = R.drawable.baseline_price_check_24
        ),
        DashboardItem(
            title = "Tools",
            selectedIcon = R.drawable.baseline_bubble_chart_24,
            unSelectedIcon = R.drawable.baseline_bubble_chart_24
        ),
        DashboardItem(
            title = "Analytics",
            selectedIcon = R.drawable.baseline_query_stats_24,
            unSelectedIcon = R.drawable.baseline_query_stats_24
        ),
        DashboardItem(
            title = "Sentiment",
            selectedIcon = R.drawable.baseline_textsms_24,
            unSelectedIcon = R.drawable.baseline_textsms_24
        ),
        )

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(120.dp)
        .clip(RoundedCornerShape(5.dp))
        .background(color = Color.LightGray)
    ) {
        LazyColumn(contentPadding = PaddingValues(16.dp)) {

            val itemCount = dashboardItems.size / 5 + if (dashboardItems.size % 5 == 0) 0 else 1

            items(itemCount) {
                IndicatorRows(rowIndex = it, entries = dashboardItems)
            }

        }
    }
}

@Composable
fun IndicatorRows(
    rowIndex: Int,
    entries: List<DashboardItem>,
    navContoller: NavController = rememberNavController()
) {
    Column {
        Row {
            for (i in 0 until 5) {
                Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                    if (rowIndex * 5 + i < entries.size) {

                        Image(
                            painter = painterResource(entries[rowIndex * 5 + i].unSelectedIcon),
                            contentDescription = "Content description for visually impaired",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .size(30.dp)
                        )

                        Text(
                            text = "${entries[rowIndex * 5 + i].title}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )


                    }
                }
                Spacer(modifier = Modifier.width(20.dp))

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


