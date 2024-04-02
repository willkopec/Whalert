package com.willkopec.whalert.ui.chartscreen

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.compose.style.ChartStyle
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.DefaultAlpha
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShader
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.willkopec.whalert.breakingnews.WhalertViewModel
import com.willkopec.whalert.model.polygon.Result
import com.willkopec.whalert.ui.homescreen.RetrySection
import com.willkopec.whalert.util.DateUtil

@Composable
fun ChartSymbolScreen(
    timeScaleInDays: Int,
    viewModel: WhalertViewModel = hiltViewModel()
) {
    val currentChartData by viewModel.currentChartData.collectAsState()
    val loadError by viewModel.loadError.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.getSymbolData("BTC", 101) // Fetch data
    }

    if (loadError == "") {
        BarChartExample(timeScaleInDays, currentChartData)
    } else {
        RetrySection(error = loadError) {  }
    }
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