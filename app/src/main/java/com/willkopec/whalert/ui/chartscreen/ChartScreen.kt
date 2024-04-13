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
        mutableStateOf(getHtmlContent(currentChartData, name, screenHeightWithoutBottomBar.toInt()))
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

fun getHtmlContent(timePriceData: List<Result>, name: String?, screenHeight: Int): String {
    var index = 0

    val dataScript = StringBuilder()
    timePriceData.forEach { result ->

        if(index == 0){
            dataScript.append("{ time: '${result.date}', value: ${result.c} }")
        } else if(index > 0){
            dataScript.append("                    { time: '${result.date}', value: ${result.c} }")
        }

        if(index < timePriceData.size - 1){
            dataScript.append(",\n")
        }
        index++
    }


    return """
        <html>
        <head>
            <script src="https://unpkg.com/lightweight-charts/dist/lightweight-charts.standalone.production.js"></script>
            <style>
                html,
                body {
                	font-family: 'Trebuchet MS', Roboto, Ubuntu, sans-serif;
                	background: #f9fafb;
                	-webkit-font-smoothing: antialiased;
                	-moz-osx-font-smoothing: grayscale;
                }

                .three-line-legend {
                	width: 96px;
                	height: 70px;
                	position: absolute;
                	padding: 8px;
                	font-size: 12px;
                	color: '#20262E';
                	background-color: rgba(255, 255, 255, 0.23);
                	text-align: left;
                	z-index: 1000;
                	pointer-events: none;
                }
            </style>
        </head>
        <body>
            <script>
                document.body.style.position = 'relative';

var container = document.createElement('div');
document.body.appendChild(container);

var width = window.screen.width;
var height = window.screen.height - 300;

var chart = LightweightCharts.createChart(container, {
	width: width,
	height: height,
	rightPriceScale: {
		scaleMargins: {
			top: 0.2,
			bottom: 0.2,
		},
		borderVisible: false,
	},
	timeScale: {
		borderVisible: false,
	},
	layout: {
		backgroundColor: '#ffffff',
		textColor: '#333',
	},
	grid: {
		horzLines: {
			color: '#eee',
		},
		vertLines: {
			color: '#ffffff',
		},
	},
});

var areaSeries = chart.addAreaSeries({
  topColor: 'rgba(255, 82, 82, 0.56)',
  bottomColor: 'rgba(255, 82, 82, 0.04)',
  lineColor: 'rgba(255, 82, 82, 1)',
  lineWidth: 2,
	symbol: 'AAPL',
});

areaSeries.setData([
	${dataScript}
]);

const toolTipWidth = 80;
const toolTipHeight = 80;
const toolTipMargin = 15;

// Create and style the tooltip html element
const toolTip = document.createElement('div');
toolTip.style = `width: 96px; height: 80px; position: absolute; display: none; padding: 8px; box-sizing: border-box; font-size: 12px; text-align: left; z-index: 1000; top: 12px; left: 12px; pointer-events: none; border: 1px solid; border-radius: 2px;font-family: -apple-system, BlinkMacSystemFont, 'Trebuchet MS', Roboto, Ubuntu, sans-serif; -webkit-font-smoothing: antialiased; -moz-osx-font-smoothing: grayscale;`;
toolTip.style.background = 'white';
toolTip.style.color = 'black';
toolTip.style.borderColor = 'rgba(255, 82, 82, 1)';
container.appendChild(toolTip);

// update tooltip
chart.subscribeCrosshairMove(param => {
	if (
		param.point === undefined ||
		!param.time ||
		param.point.x < 0 ||
		param.point.x > container.clientWidth ||
		param.point.y < 0 ||
		param.point.y > container.clientHeight
	) {
		toolTip.style.display = 'none';
	} else {
		// time will be in the same format that we supplied to setData.
		// thus it will be YYYY-MM-DD
		const dateStr = param.time;
		toolTip.style.display = 'block';
		const data = param.seriesData.get(areaSeries);
		const price = data.value !== undefined ? data.value : data.close;
		toolTip.innerHTML = `<div style="color: ${"\${'rgba(255, 82, 82, 1)'}"}">${name}</div><div style="font-size: 14px; margin: 4px 0px; color: ${"${"black"}"}">
			${"\${Math.round(100 * price) / 100}"}
			</div><div style="color: ${"${"black"}"}">
			${"\${dateStr}"}
			</div>`;

		const y = param.point.y;
		let left = param.point.x + toolTipMargin;
		if (left > container.clientWidth - toolTipWidth) {
			left = param.point.x - toolTipMargin - toolTipWidth;
		}

		let top = y + toolTipMargin;
		if (top > container.clientHeight - toolTipHeight) {
			top = y - toolTipHeight - toolTipMargin;
		}
		toolTip.style.left = left + 'px';
		toolTip.style.top = top + 'px';
	}
});
            </script>
        </body>
        </html>
    """.trimIndent()
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