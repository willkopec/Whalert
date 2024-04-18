package com.willkopec.whalert.ui.chartscreen

import android.content.Context
import android.util.Log
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.DefaultAlpha
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.willkopec.whalert.breakingnews.WhalertViewModel
import com.willkopec.whalert.model.polygon.Result
import com.willkopec.whalert.ui.homescreen.RetrySection
import com.willkopec.whalert.*
import com.willkopec.whalert.util.ChartHtmlContentUtil.getBarChart
import com.willkopec.whalert.util.ChartHtmlContentUtil.getStandardChartContent
import com.willkopec.whalert.util.DateUtil
import kotlinx.coroutines.delay

@Composable
fun ChartSymbolScreen(
    timeScaleInDays: Int,
    bottomBarHeight: Int,
    viewModel: WhalertViewModel = hiltViewModel()
) {
    val currentChartData by viewModel.currentChartData.collectAsState()
    val loadError by viewModel.loadError.collectAsState()
    val currentName by viewModel.currentChartName.observeAsState()

    if (loadError == "" && currentChartData.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
            //.padding(bottom = bottomBarHeight.dp) // Add padding to the bottom
        ) {
            SearchBar(
                hint = "Search...",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(16.dp)
            )
            LightweightChart(currentName, currentChartData)
            //Vico Implementation:
            //BarChartExample(timeScaleInDays, currentChartData)
        }


    } else {
        RetrySection(error = loadError, onRetry = {viewModel.getSymbolData("BTC", 101)})
    }
}

@Composable
fun LightweightChart(
    name: String?,
    currentChartData: List<Result>
) {
    val density = LocalDensity.current
    val screenHeight = with(density) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
    val screenHeightWithoutBottomBar = screenHeight - 56

    val currentChartDatas: String by remember(currentChartData) {
        mutableStateOf(getHtmlContent(currentChartData, name, "bar"))
    }

    // Create a unique identifier that changes whenever currentChartDatas changes
    val uniqueId = remember { mutableStateOf(0) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    //settings.useWideViewPort = true
                    settings.loadWithOverviewMode = true
                    loadDataWithBaseURL(null, currentChartDatas, "text/html", "utf-8", null)
                }
            }, update = { webView ->
                // Update WebView content when currentChartDatas changes
                webView.loadDataWithBaseURL(null, currentChartDatas, "text/html", "utf-8", null)
            })
    }


}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hint: String = "",
    onSearch: (String) -> Unit = {},
    viewModel: WhalertViewModel = hiltViewModel()
){
    var text by remember {
        mutableStateOf("")
    }
    var isHintDisplayed by remember {
        mutableStateOf(hint != "")
    }

    Box(modifier = modifier){

        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                onSearch(it)
            },
            maxLines = 1,
            singleLine = true,
            /*textStyle = TextStyle(color = Color.Black),*/
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, CircleShape)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged {
                    isHintDisplayed = !it.isFocused && text.isNotEmpty()
                }
        )
        if(isHintDisplayed) {
            Text(
                text = hint,
                color = Color.LightGray,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }

    }

    LaunchedEffect(key1 = text) {
        if (text.isBlank()) return@LaunchedEffect
        delay(1500)
        viewModel.getSymbolData(text, 500)
        delay(500)
        viewModel.printList()
    }
}

fun getHtmlContent(timePriceData: List<Result>, name: String?, chartType: String): String {
    var indexOne = 0
    var indexTwo = 0

    val lineDataScript = StringBuilder()
    timePriceData.forEach { result ->

        if(indexOne == 0){
            lineDataScript.append("{ time: '${result.date}', value: ${result.c} }")
        } else if(indexOne > 0){
            lineDataScript.append("                    { time: '${result.date}', value: ${result.c} }")
        }

        if(indexOne < timePriceData.size - 1){
            lineDataScript.append(",\n")
        }
        indexOne++
    }

    val barDataScript = StringBuilder()
    timePriceData.forEach { result ->

        if(indexTwo == 0){
            barDataScript.append("{ time: '${result.date}', open: ${result.o}, high: ${result.h}, low: ${result.l}, close: ${result.c} },")
        } else if(indexTwo > 0){
            barDataScript.append("                    { time: '${result.date}', open: ${result.o}, high: ${result.h}, low: ${result.l}, close: ${result.c} }")
        }

        if(indexTwo < timePriceData.size - 1){
            barDataScript.append(",\n")
        }
        indexTwo++
    }

    if(chartType == "bar"){
        return getBarChart(name, barDataScript)
    }

    return getStandardChartContent(name, lineDataScript)
}

@Composable
fun BarChartExample(
    timeScaleInDays: Int,
    currentChartData: List<Result>
) {
    val modelProducer = remember { ChartEntryModelProducer() }
    val datasetForModel = remember { mutableStateListOf(listOf<FloatEntry>()) }
    val datasetLineSpec = remember { arrayListOf<LineChart.LineSpec>() }
    val scrollState = rememberChartScrollState()

    SideEffect {
        if (currentChartData.isNotEmpty()) {
            datasetForModel.clear()
            datasetLineSpec.clear()
            var xPos = 0f
            val dataPoints = arrayListOf<FloatEntry>()
            datasetLineSpec.add(
                LineChart.LineSpec(
                    lineColor = Green.toArgb(),
                    lineBackgroundShader = DynamicShaders.fromBrush(
                        brush = Brush.verticalGradient(
                            listOf(
                                Green.copy(DefaultAlpha.LINE_BACKGROUND_SHADER_START),
                                Green.copy(DefaultAlpha.LINE_BACKGROUND_SHADER_END)
                            )
                        )
                    )
                )
            )

            currentChartData.forEachIndexed { index, result ->
                val yFloatValue = result.c.toFloat()
                dataPoints.add(FloatEntry(x = xPos, y = yFloatValue))
                xPos += 1f
            }

            datasetForModel.add(dataPoints)
            modelProducer.setEntries(datasetForModel)
        }
    }

    // Compose
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            /*Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
            ) {*/
                ProvideChartStyle {

                    Chart(
                        modifier = Modifier.fillMaxSize(),
                        chart = lineChart(
                            lines = datasetLineSpec
                        ),
                        chartModelProducer = modelProducer,
                        startAxis = rememberStartAxis(
                            title = "Top Values",
                            tickLength = 0.dp,
                            valueFormatter = { value, _ ->
                                ((value.toInt()) + 1).toString()
                            },
                            itemPlacer = AxisItemPlacer.Vertical.default(
                                maxItemCount = 8
                            )
                        ),
                        bottomAxis = rememberBottomAxis(
                            title = "Count of values",
                            tickLength = 0.dp,
                            valueFormatter = { value, _ ->
                                val index = (value.toInt()) + 1
                                DateUtil.getDateBeforeDays(timeScaleInDays - index + 1)
                            },
                            itemPlacer = AxisItemPlacer.Horizontal.default(addExtremeLabelPadding = true, spacing = 4, offset = 3)
                        ),

                        chartScrollState = scrollState,
                        isZoomEnabled = true
                    )
                }
            //}
        }
    }
}