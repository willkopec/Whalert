package com.willkopec.whalert.util

import com.willkopec.whalert.util.DateUtil.getDateBeforeDays
import com.willkopec.whalert.util.DateUtil.getDateBeforeDaysWithTime
import com.willkopec.whalert.util.SymbolUtils.convertToCoinAPIFormat
import java.lang.StringBuilder

object ChartHtmlContentUtil {
    fun getStandardChartContent(name: String?, dataScript: StringBuilder): String {
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

var width = window.screen.width - 10;
var height = window.screen.height - 310;

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

    fun getBarChart(name: String?, dataScript: StringBuilder): String {

        return """
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Candlestick Chart</title>
            <script src="https://unpkg.com/lightweight-charts/dist/lightweight-charts.standalone.production.js"></script>
            <style>
                #chartContainer {
                    position: relative;
                    width: 100%;
                    height: 550px;
                }

            #additionalContent {
                position: absolute;
                top: 5px;
                left: 5px;
                z-index: 20;
                background-color: #f0f0f0;
            }
</style>
        </head>
        <body>
            <div id="chartContainer">
                <div id="additionalContent">
                    <!-- Price Information: -->
                    <div id="priceInfo"></div>
                </div>
            </div>
        
            <script>
                var chartContainer = document.getElementById('chartContainer');
                var additionalContent = document.getElementById('additionalContent');
                var priceInfo = document.getElementById('priceInfo');
        
                var chart = LightweightCharts.createChart(chartContainer, {
                    width: chartContainer.offsetWidth,
                    height: chartContainer.offsetHeight,
                    timeScale: {
                        timeVisible: true,
                        borderColor: '#D1D4DC',
                    },
                    rightPriceScale: {
                        borderColor: '#D1D4DC',
                    },
                    layout: {
                        background: {
                            type: 'solid',
                            color: '#ffffff',
                        },
                        textColor: '#000',
                    },
                    grid: {
                        horzLines: {
                            color: '#F0F3FA',
                        },
                        vertLines: {
                            color: '#F0F3FA',
                        },
                    },
                });
        
                var series = chart.addCandlestickSeries({
                    upColor: 'rgb(38,166,154)',
                    downColor: 'rgb(255,82,82)',
                    wickUpColor: 'rgb(38,166,154)',
                    wickDownColor: 'rgb(255,82,82)',
                    borderVisible: false,
                });
        
                var data = [
                    ${dataScript}
                    // Add your data here
                ];
        
                series.setData(data);
        
                chart.subscribeCrosshairMove((param) => {
                    if (param.time) {
                        const data = param.seriesData.get(series);
                        priceInfo.innerHTML = 
                            "<div>OPEN: " + data.open + "</div>" +
                            "<div>HIGH: " + data.high + "</div>" +
                            "<div>LOW: " + data.low + "</div>" +
                            "<div>CLOSE: " + data.close + "</div>";
                    }
                });
            </script>
        </body>
        </html>
    """.trimIndent()
    }

    fun getBarChartHtmlContent(symbol: String?, days: Int): String{
        return """
        <html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Real-time Chart Example</title>
    <!-- Include Lightweight Charts library -->
    <script src="https://unpkg.com/lightweight-charts/dist/lightweight-charts.standalone.production.js"></script>
    <style>
			#chartContainer {
                    position: relative;
                    width: 100%;
                    height: 550px;
                }

            #additionalContent {
                position: absolute;
                top: 50px;
                left: 5px;
                z-index: 20;
                background-color: #f0f0f0;
            }
        /* Add your custom styles here */
        .buttons-container {
            display: flex;
            flex-direction: row;
            gap: 8px;
        }
        .buttons-container button {
            all: initial;
            font-family: -apple-system, BlinkMacSystemFont, 'Trebuchet MS', Roboto, Ubuntu, sans-serif;
            font-size: 14px;
            font-style: normal;
            font-weight: 510;
            line-height: 24px; /* 150% */
            letter-spacing: -0.32px;
            padding: 8px 24px;
            color: rgba(19, 23, 34, 1);
            background-color: rgba(240, 243, 250, 1);
            border-radius: 8px;
            cursor: pointer;
        }
        .buttons-container button:hover {
            background-color: rgba(224, 227, 235, 1);
        }
        .buttons-container button:active {
            background-color: rgba(209, 212, 220, 1);
        }
    </style>
</head>
<body>
    <!-- Container for the chart and buttons -->
    <div id="chartContainer">
    <!-- Buttons container -->
    <div class="buttons-container">
        <!-- Button to scroll to real-time -->
        <button id="realtimeButton">Go to realtime</button>
        <!-- Button to add to favorites -->
        <button id="addToFavoritesButton">Add to Favorites</button>
    </div>
    <div id="additionalContent">
        <!-- Price Information: -->
        <div id="priceInfo"></div>
    </div>
</div>

    <script>
        // Initialize the chart
        const chartOptions = {
            layout: {
                textColor: 'black',
                background: { type: 'solid', color: 'white' },
            },
            height: 500, // Increased height for better visualization
        };
        const container = document.getElementById('chartContainer');
        const chart = LightweightCharts.createChart(container, chartOptions);

        // Add candlestick series to the chart
        const series = chart.addCandlestickSeries({
            upColor: '#26a69a',
            downColor: '#ef5350',
            borderVisible: false,
            wickUpColor: '#26a69a',
            wickDownColor: '#ef5350',
        });

        // Function to fetch data from the API endpoint
        async function fetchDataAndUpdateChart() {
            try {
                const response = await fetch('https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_${symbol?.let {
            convertToCoinAPIFormat(
                it
            )
        }}_USD/apikey-59659DAF-46F7-4981-BCDB-6A10B727341E/history?period_id=1DAY&time_start=${getDateBeforeDaysWithTime(days - 1)}&limit=${days}');
                if (!response.ok) {
                    throw new Error('Failed to fetch data: ' + response.statusText);
                }
                const apiData = await response.json();
                updateChartWithNewData(apiData);
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        }

        // Function to update the chart with new data
        function updateChartWithNewData(newData) {
            const candleData = newData.map(item => ({
                time: item.time_period_start,
                open: item.price_open,
                high: item.price_high,
                low: item.price_low,
                close: item.price_close,
            }));
            series.setData(candleData);
        }

        // Function to scroll to real-time data
        function scrollToRealTime() {
            chart.timeScale().scrollToRealTime();
        }
		
		function addToOrDeleteFromFavorites() {
            // Invoke the isInFavorites method of the JavaScript interface
            if (Android.isInFavorites("$symbol")) {
                // Symbol is already in favorites, delete it
                Android.deleteFromFavorites("$symbol");
            } else {
                // Symbol is not in favorites, add it
                Android.addToFavorites("$symbol");
            }
        }

        // Fetch data from the API and update the chart every 5 seconds
        fetchDataAndUpdateChart();
        setInterval(fetchDataAndUpdateChart, 5000);

        // Add event listener to the real-time button
        const realtimeButton = document.getElementById('realtimeButton');
        realtimeButton.addEventListener('click', scrollToRealTime);
		
		const addToFavoritesButton = document.getElementById('addToFavoritesButton');
		addToFavoritesButton.addEventListener('click', addToOrDeleteFromFavorites);
		
		chart.subscribeCrosshairMove((param) => {
                    if (param.time) {
                        const data = param.seriesData.get(series);
                        priceInfo.innerHTML = 
                            "<div>OPEN: " + data.open + "</div>" +
                            "<div>HIGH: " + data.high + "</div>" +
                            "<div>LOW: " + data.low + "</div>" +
                            "<div>CLOSE: " + data.close + "</div>";
                    }
                });
		
    </script>
</body>
</html>
    """.trimIndent()
    }

    fun getDarkModeBarChartHtmlContent(symbol: String?, days: Int): String{
        return """
        <html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, height=device-height, initial-scale=1.0">
    <title>Real-time Chart Example</title>
    <!-- Include Lightweight Charts library -->
    <script src="https://unpkg.com/lightweight-charts/dist/lightweight-charts.standalone.production.js"></script>
    <style>
    body {
        background-color: black;
        color: white; /* Optionally set text color to white for better visibility */
    }
    #chartContainer {
        position: relative;
        width: 100%;
        height: calc(100vh - 40px); /* Adjusted height to accommodate range-switcher */
    }

    #additionalContent {
        position: absolute;
        top: 50px;
        left: 5px;
        z-index: 20;
        background-color: #262522;
    }

    /* Add your custom styles here */
    .buttons-container {
        display: flex;
        flex-direction: row;
        gap: 8px;
    }

    .buttons-container button {
        all: initial;
        font-family: -apple-system, BlinkMacSystemFont, 'Trebuchet MS', Roboto, Ubuntu, sans-serif;
        font-size: 14px;
        font-style: normal;
        font-weight: 510;
        line-height: 24px; /* 150% */
        letter-spacing: -0.32px;
        padding: 8px 24px;
        color: rgba(19, 23, 34, 1);
        background-color: rgba(240, 243, 250, 1);
        border-radius: 8px;
        cursor: pointer;
    }

    .buttons-container button:hover {
        background-color: rgba(224, 227, 235, 1);
    }

    .buttons-container button:active {
        background-color: rgba(209, 212, 220, 1);
    }

    #range-switcher {
        position: fixed;
        bottom: 0;
        left: 0;
        width: 100%;
        background-color: #000000;
        padding: 10px;
    }

    #range-switcher button {
        font-family: -apple-system, BlinkMacSystemFont, 'Trebuchet MS', Roboto, Ubuntu, sans-serif;
        font-size: 14px;
        font-style: normal;
        font-weight: 510;
        line-height: 10px; /* 150% */
        letter-spacing: -0.32px;
        padding: 8px 10px;
        color: rgba(19, 23, 34, 1);
        background-color: rgba(240, 243, 250, 1);
        border-radius: 8px;
        cursor: pointer;
    }
    #range-switcher button:hover {
        background-color: rgba(224, 227, 235, 1);
    }

    #range-switcher button:active {
        background-color: rgba(209, 212, 220, 1);
    }

    .range-switcher-buttons {
        display: flex;
        flex-direction: row;
        gap: 5px;
    }
    </style>
</head>
<body>
    <!-- Container for the chart and buttons -->
    <div id="chartContainer">
        <!-- Buttons container -->
        <div class="buttons-container">
            <!-- Button to scroll to real-time -->
            <button id="realtimeButton">Go to realtime</button>
            <!-- Button to add to favorites -->
            <button id="addToFavoritesButton">Add to Favorites</button>
        </div>
        <div id="additionalContent">
            <!-- Price Information: -->
            <div id="priceInfo"></div>
        </div>
    </div>

    <!-- Range switcher -->
    <div id="range-switcher"></div>

    <script>
    // Initialize the chart
    const chartOptions = {
        layout: {
            textColor: 'white',
            background: { type: 'solid', color: 'black' },
        },
        grid: {
            vertLines: {
                color: 'rgba(197, 203, 206, 0.5)',
            },
            horzLines: {
                color: 'rgba(197, 203, 206, 0.5)',
            },
        },
        height: 525, // Increased height for better visualization
    };
    const container = document.getElementById('chartContainer');
    const chart = LightweightCharts.createChart(container, chartOptions);
    let currentInterval = '1DAY'; // Default interval

    // Add candlestick series to the chart
    const series = chart.addCandlestickSeries({
        upColor: '#26a69a',
        downColor: '#ef5350',
        borderVisible: false,
        wickUpColor: '#26a69a',
        wickDownColor: '#ef5350',
    });

    // Function to fetch data from the API endpoint
    async function fetchDataAndUpdateChart() {
        try {
            const response = await fetch(`https://rest.coinapi.io/v1/ohlcv/${symbol?.let {
            convertToCoinAPIFormat(
                it
            )
        }}/apikey-59659DAF-46F7-4981-BCDB-6A10B727341E/history?period_id=${'$'}{currentInterval}&time_start=${getDateBeforeDaysWithTime(days - 1)}&limit=${days}`);
            if (!response.ok) {
                throw new Error('Failed to fetch data: ' + response.statusText);
            }
            const apiData = await response.json();
            updateChartWithNewData(apiData);
        } catch (error) {
            console.error('Error fetching data:', error);
        }
    }

    // Function to update the chart with new data
    function updateChartWithNewData(newData) {
        const candleData = newData.map(item => ({
            time: item.time_period_start,
            open: item.price_open,
            high: item.price_high,
            low: item.price_low,
            close: item.price_close,
        }));
        series.setData(candleData);
    }

    // Function to scroll to real-time data
    function scrollToRealTime() {
        chart.timeScale().scrollToRealTime();
    }

    function addToOrDeleteFromFavorites() {
        // Invoke the isInFavorites method of the JavaScript interface
        if (Android.isInFavorites("$symbol")) {
            // Symbol is already in favorites, delete it
            Android.deleteFromFavorites("$symbol");
        } else {
            // Symbol is not in favorites, add it
            Android.addToFavorites("$symbol");
        }
    }

    // Fetch data from the API and update the chart every 5 seconds
    fetchDataAndUpdateChart();
    setInterval(fetchDataAndUpdateChart, 5000);

    // Add event listener to the real-time button
    const realtimeButton = document.getElementById('realtimeButton');
    realtimeButton.addEventListener('click', scrollToRealTime);

    const addToFavoritesButton = document.getElementById('addToFavoritesButton');
    addToFavoritesButton.addEventListener('click', addToOrDeleteFromFavorites);

    chart.subscribeCrosshairMove((param) => {
        if (param.time) {
            const data = param.seriesData.get(series);
            priceInfo.innerHTML =
                "<div>OPEN: " + data.open + "</div>" +
                "<div>HIGH: " + data.high + "</div>" +
                "<div>LOW: " + data.low + "</div>" +
                "<div>CLOSE: " + data.close + "</div>";
        }
    });

    // Function to set the chart interval
    function setChartInterval(interval) {
        // Set the current interval
        if(interval == '1D'){
            currentInterval = '1DAY';
        } else if (interval == '1W'){
            currentInterval = '7DAY';
        } else if (interval == '1M'){
            currentInterval = '1MTH';
        }
        // Fetch data with the new interval
        fetchDataAndUpdateChart();
        console.log('Chart interval set to: ' + interval);
    }

    // Array of intervals
    const intervals = ['1D', '1W', '1M', '1Y'];

    // Get the range-switcher container
    const rangeSwitcher = document.getElementById('range-switcher');

    // Create buttons for each interval and append them to the range-switcher container
    intervals.forEach(interval => {
        const button = document.createElement('button');
        button.innerText = interval;
        button.addEventListener('click', () => setChartInterval(interval));
        rangeSwitcher.appendChild(button);
    });

</script>
</body>
</html>
    """.trimIndent()
    }

    fun getPiCycleTopIndicator(days: Int): String{
        return """
        <html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, height=device-height, initial-scale=1.0">
    <title>Real-time Chart Example</title>
    <!-- Include Lightweight Charts library -->
    <script src="https://unpkg.com/lightweight-charts/dist/lightweight-charts.standalone.production.js"></script>
    <style>
    body {
        background-color: black;
        color: white; /* Optionally set text color to white for better visibility */
    }
    #chartContainer {
        position: relative;
        width: 100%;
        height: calc(100vh - 40px); /* Adjusted height to accommodate range-switcher */
    }

    #additionalContent {
        position: absolute;
        top: 50px;
        left: 5px;
        z-index: 20;
        background-color: #262522;
    }

    /* Add your custom styles here */
    .buttons-container {
        display: flex;
        flex-direction: row;
        gap: 8px;
    }

    .buttons-container button {
        all: initial;
        font-family: -apple-system, BlinkMacSystemFont, 'Trebuchet MS', Roboto, Ubuntu, sans-serif;
        font-size: 14px;
        font-style: normal;
        font-weight: 510;
        line-height: 24px; /* 150% */
        letter-spacing: -0.32px;
        padding: 8px 24px;
        color: rgba(19, 23, 34, 1);
        background-color: rgba(240, 243, 250, 1);
        border-radius: 8px;
        cursor: pointer;
    }

    .buttons-container button:hover {
        background-color: rgba(224, 227, 235, 1);
    }

    .buttons-container button:active {
        background-color: rgba(209, 212, 220, 1);
    }

    #range-switcher {
        position: fixed;
        bottom: 0;
        left: 0;
        width: 100%;
        background-color: #000000;
        padding: 10px;
    }

    #range-switcher button {
        font-family: -apple-system, BlinkMacSystemFont, 'Trebuchet MS', Roboto, Ubuntu, sans-serif;
        font-size: 14px;
        font-style: normal;
        font-weight: 510;
        line-height: 10px; /* 150% */
        letter-spacing: -0.32px;
        padding: 8px 10px;
        color: rgba(19, 23, 34, 1);
        background-color: rgba(240, 243, 250, 1);
        border-radius: 8px;
        cursor: pointer;
    }
    #range-switcher button:hover {
        background-color: rgba(224, 227, 235, 1);
    }

    #range-switcher button:active {
        background-color: rgba(209, 212, 220, 1);
    }

    .range-switcher-buttons {
        display: flex;
        flex-direction: row;
        gap: 5px;
    }
    </style>
</head>
<body>
    <!-- Container for the chart and buttons -->
    <div id="chartContainer">
        <!-- Buttons container -->
        <div class="buttons-container">
            <!-- Button to scroll to real-time -->
            <button id="realtimeButton">Go to realtime</button>
        </div>
        <div id="additionalContent">
            <!-- Price Information: -->
            <div id="priceInfo"></div>
        </div>
    </div>

    <!-- Range switcher -->
    <div id="range-switcher"></div>

    <script>
    // Initialize the chart
    const chartOptions = {
        layout: {
            textColor: 'white',
            background: { type: 'solid', color: 'black' },
        },
        grid: {
            vertLines: {
                color: 'rgba(197, 203, 206, 0.5)',
            },
            horzLines: {
                color: 'rgba(197, 203, 206, 0.5)',
            },
        },
        height: 525, // Increased height for better visualization
    };
    const container = document.getElementById('chartContainer');
    const chart = LightweightCharts.createChart(container, chartOptions);
    let currentInterval = '1DAY'; // Default interval

    // Add candlestick series to the chart
    const series = chart.addCandlestickSeries({
        upColor: '#26a69a',
        downColor: '#ef5350',
        borderVisible: false,
        wickUpColor: '#26a69a',
        wickDownColor: '#ef5350',
    });

    // Function to fetch data from the API endpoint
    async function fetchDataAndUpdateChart() {
        try {
            const response = await fetch(`https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_BTC_USD/apikey-59659DAF-46F7-4981-BCDB-6A10B727341E/history?period_id=1DAY&time_start=2012-01-01T00:00:00&limit=5000`);
            if (!response.ok) {
                throw new Error('Failed to fetch data: ' + response.statusText);
            }
            const apiData = await response.json();
            updateChartWithNewData(apiData);
        } catch (error) {
            console.error('Error fetching data:', error);
        }
    }

	// Add SMA trend lines to the chart
const smaPeriod = 111;
const smaSeries = chart.addLineSeries({
    color: 'rgba(255, 255, 0, 1)',
    lineWidth: 2,
});
const smaPeriod2 = 350;
const smaSeries2 = chart.addLineSeries({
    color: 'rgba(0, 255, 255, 1)',
    lineWidth: 2,
});

// Function to update the chart with new data
function updateChartWithNewData(newData) {
    const candleData = newData.map(item => ({
        time: item.time_period_start,
        open: item.price_open,
        high: item.price_high,
        low: item.price_low,
        close: item.price_close,
    }));

    // Add candlestick series to the chart
    series.setData(candleData);

    // Calculate SMA
    const smaData = calculateSMA(candleData, smaPeriod);
    smaSeries.setData(smaData);

    const smaData2 = calculateSMATimesTwo(candleData, smaPeriod2);
    smaSeries2.setData(smaData2);
}

    // Function to calculate Simple Moving Average (SMA)
    function calculateSMA(data, period) {
        const sma = [];
        for (let i = period - 1; i < data.length; i++) {
            let sum = 0;
            for (let j = i - period + 1; j <= i; j++) {
                sum += data[j].close;
            }
            sma.push({
                time: data[i].time,
                value: sum / period,
            });
        }
        return sma;
    }
	
	function calculateSMATimesTwo(data, period) {
        const sma = [];
        for (let i = period - 1; i < data.length; i++) {
            let sum = 0;
            for (let j = i - period + 1; j <= i; j++) {
                sum += data[j].close;
            }
            sma.push({
                time: data[i].time,
                value: (sum / period) * 2,
            });
        }
        return sma;
    }

    // Function to scroll to real-time data
    function scrollToRealTime() {
        chart.timeScale().scrollToRealTime();
    }

    // Fetch data from the API and update the chart every 5 seconds
    fetchDataAndUpdateChart();
    setInterval(fetchDataAndUpdateChart, 5000);

    // Add event listener to the real-time button
    const realtimeButton = document.getElementById('realtimeButton');
    realtimeButton.addEventListener('click', scrollToRealTime);

    // Subscribe to crosshair move event to display price information
    chart.subscribeCrosshairMove((param) => {
        if (param.time) {
            const data = param.seriesData.get(series);
            priceInfo.innerHTML =
                "<div>OPEN: " + data.open + "</div>" +
                "<div>HIGH: " + data.high + "</div>" +
                "<div>LOW: " + data.low + "</div>" +
                "<div>CLOSE: " + data.close + "</div>";
        }
    });

    </script>
</body>
</html>
    """.trimIndent()
    }
}
