package com.willkopec.whalert.ui.indicatorslistscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
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
import com.willkopec.whalert.ui.homescreen.BottomNavigationItem
import com.willkopec.whalert.R
import com.willkopec.whalert.breakingnews.WhalertViewModel
import com.willkopec.whalert.model.coinAPI.CoinAPIResultItem
import com.willkopec.whalert.model.coingecko.CryptoItem
import com.willkopec.whalert.ui.favoriteslistscreen.PercentageChangeChip
import com.willkopec.whalert.ui.favoriteslistscreen.RiskLevelChip
import com.willkopec.whalert.util.BottomBarScreen

data class IndicatorListItemInfo(
    val name: String,
    val description: String,
    val indicatorID: String,
    val chartImage: String
)

@Composable
fun IndicatorsListScreenn(
    navController: NavHostController,
    viewModel: WhalertViewModel = hiltViewModel()
) {

    val indicatorList =
        listOf(
            IndicatorListItemInfo(
                name = "Pi Cycle Top Indicator",
                description = "This indicator uses two moving averages to predict the top of the current crypto cycle. When the indicator's yellow moving average crosses the blue moving average, this should predict the current cycle top within 3 days of the moving averages crossing.",
                indicatorID = "picycle",
                chartImage = ""
            ),
            IndicatorListItemInfo(
                name = "BTC Profitable Days",
                description = "This indicator takes the current price of Bitcoin and calculates the amount of profitable buy days if you were to sell today at the current price.",
                indicatorID = "profitable_days",
                chartImage = ""
            ),
            IndicatorListItemInfo(
                name = "2 Year MA Multiplier",
                description = "This indicator uses the 2 year moving average and 5x the 2 year moving average to give good a good range of where the bitcoin price should be and any values outside of these moving averages should indicate under or over valued prices.",
                indicatorID = "2y_ma_multiplier",
                chartImage = ""
            ),
            IndicatorListItemInfo(
                name = "Puell Multiple",
                description = "This metric looks at the supply side of Bitcoin's economy - bitcoin miners and their revenue. It explores market cycles from a mining revenue perspective.The chart above highlights periods where the value of Bitcoin's issued on a daily basis has historically been extremely low (Puell Multiple entering green box), which produced outsized returns for Bitcoin investors who bought Bitcoin here. It also shows periods where the daily issuance value was extremely high (Puell Multiple entering red box), providing advantageous profit-taking for Bitcoin investors who sold here.",
                indicatorID = "puell_multiple",
                chartImage = ""
            )
        )

    val scrollState = rememberLazyListState()
    val loadError by viewModel.loadError.collectAsState()

    if (loadError == "") {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Legend
            Legend()

            LazyColumn(
                state = scrollState,
                modifier = Modifier.weight(1f)
            ) {
                val itemCount = indicatorList.size

                items(itemCount) {

                    IndicatorListItem(
                        navController,
                        indicatorList.elementAt(it),
                        modifier = Modifier,
                        viewModel = viewModel
                    )

                }
            }
        }
    }
}

@Composable
fun IndicatorListItem(
    navController: NavController,
    indicatorData: IndicatorListItemInfo,
    modifier: Modifier = Modifier,
    viewModel: WhalertViewModel = hiltViewModel()
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(1.dp))
            .clickable {
                navController.navigate("${BottomBarScreen.ChartsScreen.route}/${indicatorData.indicatorID}") {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                }
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween, // Adjusted horizontal arrangement
            verticalAlignment = Alignment.CenterVertically // Center items vertically
        ) {

            Column(modifier = Modifier
                .weight(0.35f)
                .padding(horizontal = 8.dp)
            ){
                Text(
                    text = indicatorData.name,
                    fontSize = 14.sp
                )
            }


            Column(
                modifier = Modifier
                    .weight(0.65f)
                    .padding(horizontal = 8.dp) // Add horizontal padding
            ) {
                Text(
                    text = indicatorData.description,
                    fontSize = 14.sp
                )
            }

            /*Column(
                modifier = Modifier
                    .weight(0.4f)
                    .padding(horizontal = 16.dp) // Add horizontal padding
            ) {

            }*/

        }
    }
}

@Composable
fun Legend() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(0.35f)
            ) {
                Text(
                    text = "Indicator Name",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Column(
                modifier = Modifier.weight(0.65f)
            ) {
                Text(
                    text = "Description",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}