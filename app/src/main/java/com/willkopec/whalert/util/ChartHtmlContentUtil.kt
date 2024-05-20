package com.willkopec.whalert.util

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
    <meta name="viewport" content="width=device-width, height=device-height, initial-scale=1.0">
    <title>Real-time Chart Example</title>
    <!-- Include Lightweight Charts library -->
    <script src="https://unpkg.com/lightweight-charts/dist/lightweight-charts.standalone.production.js"></script>
    <style>
    body {
        background-color: white;
        color: white; /* Optionally set text color to white for better visibility */
    }
    #chartContainer {
        position: relative;
        width: 100%;
        height: calc(100vh - 60px); /* Adjusted height to accommodate range-switcher */
        margin-bottom: 60px; /* Equal to the height of the range-switcher */
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
        background-color: #FFFFFF;
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
    function setChartContainerHeight() {
        const chartContainer = document.getElementById('chartContainer');
        const buttonsContainerHeight = document.querySelector('.buttons-container').offsetHeight;
        const additionalContentHeight = document.getElementById('additionalContent').offsetHeight;
        const rangeSwitcherHeight = document.getElementById('range-switcher').offsetHeight + 35; // Include range switcher height
        const windowHeight = window.innerHeight;

        // Calculate the remaining height after considering other elements
        const remainingHeight = windowHeight - buttonsContainerHeight - additionalContentHeight - rangeSwitcherHeight;

        // Set the height of the chart container
        chartContainer.style.height = remainingHeight + 'px';
    }

// Initialize the chart with the default height
setChartContainerHeight();

// Add event listener to recalculate height when the window is resized
window.addEventListener('resize', setChartContainerHeight);

// Initialize the chart
const chartOptions = {
    layout: {
        textColor: 'black',
        background: { type: 'solid', color: 'white' },
    },
    grid: {
        vertLines: {
            color: 'rgba(197, 203, 206, 0.5)',
        },
        horzLines: {
            color: 'rgba(197, 203, 206, 0.5)',
        },
    },
};

// Add the candlestick series to the chart
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
        height: calc(100vh - 60px); /* Adjusted height to accommodate range-switcher */
        margin-bottom: 60px; /* Equal to the height of the range-switcher */
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
    function setChartContainerHeight() {
        const chartContainer = document.getElementById('chartContainer');
        const buttonsContainerHeight = document.querySelector('.buttons-container').offsetHeight;
        const additionalContentHeight = document.getElementById('additionalContent').offsetHeight;
        const rangeSwitcherHeight = document.getElementById('range-switcher').offsetHeight + 35; // Include range switcher height
        const windowHeight = window.innerHeight;

        // Calculate the remaining height after considering other elements
        const remainingHeight = windowHeight - buttonsContainerHeight - additionalContentHeight - rangeSwitcherHeight;

        // Set the height of the chart container
        chartContainer.style.height = remainingHeight + 'px';
    }

// Initialize the chart with the default height
setChartContainerHeight();

// Add event listener to recalculate height when the window is resized
window.addEventListener('resize', setChartContainerHeight);

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
};

// Add the candlestick series to the chart
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

    fun getPiCycleTopIndicatorDarkMode(days: Int): String {
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
    function setChartContainerHeight() {
            const chartContainer = document.getElementById('chartContainer');
            const buttonsContainerHeight = document.querySelector('.buttons-container').offsetHeight;
            const additionalContentHeight = document.getElementById('additionalContent').offsetHeight;
            const rangeSwitcherHeight = document.getElementById('range-switcher').offsetHeight; // Include range switcher height
            const windowHeight = window.innerHeight;

            // Calculate the remaining height after considering other elements
            const remainingHeight = windowHeight - buttonsContainerHeight - additionalContentHeight - rangeSwitcherHeight;

            // Set the height of the chart container
            chartContainer.style.height = remainingHeight + 'px';
        }

    // Initialize the chart with the default height
    setChartContainerHeight();

    // Add event listener to recalculate height when the window is resized
    window.addEventListener('resize', setChartContainerHeight);

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
    };

    // Add the candlestick series to the chart
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

    fun getPiCycleTopIndicatorLightMode(days: Int): String {
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
        background-color: white;
        color: black; /* Optionally set text color to white for better visibility */
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
        background-color: #FFFFFF;
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
        background-color: #FFFFFF;
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
    function setChartContainerHeight() {
            const chartContainer = document.getElementById('chartContainer');
            const buttonsContainerHeight = document.querySelector('.buttons-container').offsetHeight;
            const additionalContentHeight = document.getElementById('additionalContent').offsetHeight;
            const rangeSwitcherHeight = document.getElementById('range-switcher').offsetHeight; // Include range switcher height
            const windowHeight = window.innerHeight;

            // Calculate the remaining height after considering other elements
            const remainingHeight = windowHeight - buttonsContainerHeight - additionalContentHeight - rangeSwitcherHeight;

            // Set the height of the chart container
            chartContainer.style.height = remainingHeight + 'px';
        }

    // Initialize the chart with the default height
    setChartContainerHeight();

    // Add event listener to recalculate height when the window is resized
    window.addEventListener('resize', setChartContainerHeight);

    // Initialize the chart
    const chartOptions = {
        layout: {
            textColor: 'black',
            background: { type: 'solid', color: 'white' },
        },
        grid: {
            vertLines: {
                color: 'rgba(197, 203, 206, 0.5)',
            },
            horzLines: {
                color: 'rgba(197, 203, 206, 0.5)',
            },
        },
    };

    // Add the candlestick series to the chart
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
    color: 'rgba(235, 223, 55, 1)',
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

    fun getBtcProfitableDaysIndicatorLightMode(symbol: String = ""): String{
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
        background-color: white;
        color: white; /* Optionally set text color to white for better visibility */
    }
    #chartContainer {
        position: relative;
        width: 100%;
        height: calc(100vh - 60px); /* Adjusted height to accommodate range-switcher */
        margin-bottom: 60px; /* Equal to the height of the range-switcher */
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
        background-color: #FFFFFF;
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

    #winLossRatio {
    display: block; /* Ensure the element is displayed */
    color: black; /* Set text color */
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
            <!-- Display win/loss ratio -->
            <div id="winLossRatio"></div>
        </div>
        <div id="additionalContent">
            <!-- Price Information: -->
            <div id="priceInfo"></div>
        </div>
    </div>

    <!-- Range switcher -->
    <div id="range-switcher"></div>

    <script>
    let loseCount = 0;
    let winCount = 0;
    let ratioCalculated = false;

    function setChartContainerHeight() {
        const chartContainer = document.getElementById('chartContainer');
        const buttonsContainerHeight = document.querySelector('.buttons-container').offsetHeight;
        const additionalContentHeight = document.getElementById('additionalContent').offsetHeight;
        const rangeSwitcherHeight = document.getElementById('range-switcher').offsetHeight; // Include range switcher height
        const windowHeight = window.innerHeight;

        // Calculate the remaining height after considering other elements
        const remainingHeight = windowHeight - buttonsContainerHeight - additionalContentHeight - rangeSwitcherHeight;

        // Set the height of the chart container
        chartContainer.style.height = remainingHeight + 'px';
    }

    // Initialize the chart with the default height
    setChartContainerHeight();

    // Add event listener to recalculate height when the window is resized
    window.addEventListener('resize', setChartContainerHeight);

    // Initialize the chart
    const chartOptions = {
        layout: {
            textColor: 'black',
            background: { type: 'solid', color: 'white' },
        },
        grid: {
            vertLines: {
                color: 'rgba(197, 203, 206, 0.5)',
            },
            horzLines: {
                color: 'rgba(197, 203, 206, 0.5)',
            },
        },
    };

    // Add the line series to the chart
    const container = document.getElementById('chartContainer');
    const chart = LightweightCharts.createChart(container, chartOptions);
    let currentInterval = '1DAY'; // Default interval

    // Add line series to the chart
    const series = chart.addLineSeries({
        color: '#26a69a',
        lineWidth: 2,
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

    // Function to update the chart with new data
    function updateChartWithNewData(newData) {
        const previousClosePrice = newData[newData.length - 2].price_close;
        
        const lineData = newData.map(item => {
            const isWin = item.price_close > previousClosePrice;
            if (isWin) {
                winCount++;
            } else {
                loseCount++;
            }
            return {
                time: item.time_period_start,
                value: item.price_close,
                color: isWin ? 'red' : 'green' // Set color based on value compared to threshold
            };
        });

        // Display win/loss ratio
        // Display win/loss ratio
const winLossRatioElement = document.getElementById('winLossRatio');
if (!ratioCalculated && winCount + loseCount > 0) {
    const ratio = loseCount / (winCount + loseCount);
    winLossRatioElement.innerHTML = `% Days Profitable: ${'$'}{ratio.toFixed(2) * 100} %<br>`;
    winLossRatioElement.innerHTML += `Profitable Days: ${'$'}{loseCount}<br>`;
    winLossRatioElement.innerHTML += `Unprofitable Days: ${'$'}{winCount}<br>`;
    ratioCalculated = true; // Set flag to true
} else if (!ratioCalculated && winCount + loseCount == 0) {
    winLossRatioElement.innerText = '';
}
        series.setData(lineData);
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

</script>
</body>
</html>
    """.trimIndent()
    }

    fun getBtcProfitableDaysIndicatorDarkMode(symbol: String = ""): String {
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
        color: black; /* Optionally set text color to white for better visibility */
    }
    #chartContainer {
        position: relative;
        width: 100%;
        height: calc(100vh - 60px); /* Adjusted height to accommodate range-switcher */
        margin-bottom: 60px; /* Equal to the height of the range-switcher */
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

    #winLossRatio {
    display: block; /* Ensure the element is displayed */
    color: white; /* Set text color */
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
            <!-- Display win/loss ratio -->
            <div id="winLossRatio"></div>
        </div>
        <div id="additionalContent">
            <!-- Price Information: -->
            <div id="priceInfo"></div>
        </div>
    </div>

    <!-- Range switcher -->
    <div id="range-switcher"></div>

    <script>
    let loseCount = 0;
    let winCount = 0;
    let ratioCalculated = false;

    function setChartContainerHeight() {
        const chartContainer = document.getElementById('chartContainer');
        const buttonsContainerHeight = document.querySelector('.buttons-container').offsetHeight;
        const additionalContentHeight = document.getElementById('additionalContent').offsetHeight;
        const rangeSwitcherHeight = document.getElementById('range-switcher').offsetHeight + 35; // Include range switcher height
        const windowHeight = window.innerHeight;

        // Calculate the remaining height after considering other elements
        const remainingHeight = windowHeight - buttonsContainerHeight - additionalContentHeight - rangeSwitcherHeight;

        // Set the height of the chart container
        chartContainer.style.height = remainingHeight + 'px';
    }

    // Initialize the chart with the default height
    setChartContainerHeight();

    // Add event listener to recalculate height when the window is resized
    window.addEventListener('resize', setChartContainerHeight);

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
    };

    // Add the line series to the chart
    const container = document.getElementById('chartContainer');
    const chart = LightweightCharts.createChart(container, chartOptions);
    let currentInterval = '1DAY'; // Default interval

    // Add line series to the chart
    const series = chart.addLineSeries({
        color: '#26a69a',
        lineWidth: 2,
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

    // Function to update the chart with new data
    function updateChartWithNewData(newData) {
        const previousClosePrice = newData[newData.length - 2].price_close;
        
        const lineData = newData.map(item => {
            const isWin = item.price_close > previousClosePrice;
            if (isWin) {
                winCount++;
            } else {
                loseCount++;
            }
            return {
                time: item.time_period_start,
                value: item.price_close,
                color: isWin ? 'red' : 'green' // Set color based on value compared to threshold
            };
        });

        // Display win/loss ratio
        // Display win/loss ratio
const winLossRatioElement = document.getElementById('winLossRatio');
if (!ratioCalculated && winCount + loseCount > 0) {
    const ratio = loseCount / (winCount + loseCount);
    winLossRatioElement.innerHTML = `% Days Profitable: ${'$'}{ratio.toFixed(2) * 100} %<br>`;
    winLossRatioElement.innerHTML += `Profitable Days: ${'$'}{loseCount}<br>`;
    winLossRatioElement.innerHTML += `Unprofitable Days: ${'$'}{winCount}<br>`;
    ratioCalculated = true; // Set flag to true
} else if (!ratioCalculated && winCount + loseCount == 0) {
    winLossRatioElement.innerText = '';
}
        series.setData(lineData);
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

</script>
</body>
</html>
    """.trimIndent()
    }

    fun getTwoYearMAMultiplierIndicatorLightMode(symbol: String = ""): String {
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
        background-color: white;
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
        background-color: #FFFFFF;
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
    function setChartContainerHeight() {
            const chartContainer = document.getElementById('chartContainer');
            const buttonsContainerHeight = document.querySelector('.buttons-container').offsetHeight;
            const additionalContentHeight = document.getElementById('additionalContent').offsetHeight;
            const rangeSwitcherHeight = document.getElementById('range-switcher').offsetHeight; // Include range switcher height
            const windowHeight = window.innerHeight;

            // Calculate the remaining height after considering other elements
            const remainingHeight = windowHeight - buttonsContainerHeight - additionalContentHeight - rangeSwitcherHeight;

            // Set the height of the chart container
            chartContainer.style.height = remainingHeight + 'px';
        }

    // Initialize the chart with the default height
    setChartContainerHeight();

    // Add event listener to recalculate height when the window is resized
    window.addEventListener('resize', setChartContainerHeight);

    // Initialize the chart
    const chartOptions = {
        layout: {
            textColor: 'black',
            background: { type: 'solid', color: 'white' },
        },
        grid: {
            vertLines: {
                color: 'rgba(197, 203, 206, 0.5)',
            },
            horzLines: {
                color: 'rgba(197, 203, 206, 0.5)',
            },
        },
    };

    // Add the candlestick series to the chart
    const container = document.getElementById('chartContainer');
    const chart = LightweightCharts.createChart(container, chartOptions);
    let currentInterval = '1DAY'; // Default interval

    // Add line series to the chart
const series = chart.addLineSeries();

    let previousFillSeries; // Variable to store previous fill series

    // Function to fetch data from the API endpoint
    async function fetchDataAndUpdateChart() {
        try {
            const response = await fetch(`https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_BTC_USD/apikey-59659DAF-46F7-4981-BCDB-6A10B727341E/history?period_id=1DAY&time_start=2011-01-01T00:00:00&limit=5000`);
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
    const smaPeriod = 730;
    const smaSeries = chart.addLineSeries({
        color: 'rgba(60, 179, 113, 1)',
        lineWidth: 2,
    });
    const smaPeriod2 = 730;
    const smaSeries2 = chart.addLineSeries({
        color: 'rgba(255, 0, 0, 1)',
        lineWidth: 2,
    });

    // Function to shade the area below SMA
    function shadeAreaBelowSMA(currentPrice, smaData, series) {
        const priceBelowSMAData = smaData.filter(point => point.value > currentPrice);
        const firstPoint = { time: priceBelowSMAData[0].time, value: currentPrice };
        const dataToFill = [firstPoint, ...priceBelowSMAData];
        const fillSeries = chart.addAreaSeries({
            topColor: 'rgba(0, 255, 0, 0.5)', // Bright green color for shading
            bottomColor: 'rgba(0, 255, 0, 0.5)', // Bright green color for shading
            lineColor: 'rgba(0, 255, 0, 0.5)', // Bright green color for shading
            lineWidth: 0, // No line width for the fill series
        });
        fillSeries.setData(dataToFill);
        return fillSeries;
    }

    function updateChartWithNewData(newData) {
    const lineData = newData.map(item => ({
        time: item.time_period_start,
        value: item.price_close,
    }));
    series.setData(lineData);

    // Calculate SMA
    const smaData = calculateSMA(lineData, smaPeriod);
    smaSeries.setData(smaData);

    const smaData2 = calculateSMATimesFive(lineData, smaPeriod2);
    smaSeries2.setData(smaData2);

    // Remove previous fill series if exists
    if (previousFillSeries) {
        chart.removeSeries(previousFillSeries);
    }

    // Shade the area below SMA when lineData goes under SMA
    const dataToFill = [];
    for (let i = 0; i < lineData.length; i++) {
        if (lineData[i].value < smaData[i].value) {
            dataToFill.push({
                time: lineData[i].time,
                value: lineData[i].value,
            });
        }
    }
    if (dataToFill.length > 0) {
        dataToFill.unshift({
            time: dataToFill[0].time,
            value: smaData[0].value,
        });
        previousFillSeries = chart.addAreaSeries({
            topColor: 'rgba(0, 255, 0, 0.5)', // Bright green color for shading
            bottomColor: 'rgba(0, 255, 0, 0.5)', // Bright green color for shading
            lineColor: 'rgba(0, 255, 0, 0.5)', // Bright green color for shading
            lineWidth: 0, // No line width for the fill series
        });
        previousFillSeries.setData(dataToFill);
    }
}

    // Function to calculate Simple Moving Average (SMA)
    function calculateSMA(data, period) {
        const sma = [];
        for (let i = period - 1; i < data.length; i++) {
            let sum = 0;
            for (let j = i - period + 1; j <= i; j++) {
                sum += data[j].value;
            }
            sma.push({
                time: data[i].time,
                value: sum / period,
            });
        }
        return sma;
    }

    // Function to calculate Simple Moving Average (SMA) multiplied by 5
    function calculateSMATimesFive(data, period) {
        const sma = [];
        for (let i = period - 1; i < data.length; i++) {
            let sum = 0;
            for (let j = i - period + 1; j <= i; j++) {
                sum += data[j].value;
            }
            sma.push({
                time: data[i].time,
                value: (sum / period) * 5,
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

    </script>
</body>
</html>
    """.trimIndent()
    }

    fun getTwoYearMAMultiplierIndicatorDarkMode(symbol: String = ""): String {
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
        color: black; /* Optionally set text color to white for better visibility */
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
    function setChartContainerHeight() {
            const chartContainer = document.getElementById('chartContainer');
            const buttonsContainerHeight = document.querySelector('.buttons-container').offsetHeight;
            const additionalContentHeight = document.getElementById('additionalContent').offsetHeight;
            const rangeSwitcherHeight = document.getElementById('range-switcher').offsetHeight; // Include range switcher height
            const windowHeight = window.innerHeight;

            // Calculate the remaining height after considering other elements
            const remainingHeight = windowHeight - buttonsContainerHeight - additionalContentHeight - rangeSwitcherHeight;

            // Set the height of the chart container
            chartContainer.style.height = remainingHeight + 'px';
        }

    // Initialize the chart with the default height
    setChartContainerHeight();

    // Add event listener to recalculate height when the window is resized
    window.addEventListener('resize', setChartContainerHeight);

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
    };

    // Add the candlestick series to the chart
    const container = document.getElementById('chartContainer');
    const chart = LightweightCharts.createChart(container, chartOptions);
    let currentInterval = '1DAY'; // Default interval

    // Add line series to the chart
const series = chart.addLineSeries();

    let previousFillSeries; // Variable to store previous fill series

    // Function to fetch data from the API endpoint
    async function fetchDataAndUpdateChart() {
        try {
            const response = await fetch(`https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_BTC_USD/apikey-59659DAF-46F7-4981-BCDB-6A10B727341E/history?period_id=1DAY&time_start=2011-01-01T00:00:00&limit=5000`);
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
    const smaPeriod = 730;
    const smaSeries = chart.addLineSeries({
        color: 'rgba(60, 179, 113, 1)',
        lineWidth: 2,
    });
    const smaPeriod2 = 730;
    const smaSeries2 = chart.addLineSeries({
        color: 'rgba(255, 0, 0, 1)',
        lineWidth: 2,
    });

    // Function to shade the area below SMA
    function shadeAreaBelowSMA(currentPrice, smaData, series) {
        const priceBelowSMAData = smaData.filter(point => point.value > currentPrice);
        const firstPoint = { time: priceBelowSMAData[0].time, value: currentPrice };
        const dataToFill = [firstPoint, ...priceBelowSMAData];
        const fillSeries = chart.addAreaSeries({
            topColor: 'rgba(0, 255, 0, 0.5)', // Bright green color for shading
            bottomColor: 'rgba(0, 255, 0, 0.5)', // Bright green color for shading
            lineColor: 'rgba(0, 255, 0, 0.5)', // Bright green color for shading
            lineWidth: 0, // No line width for the fill series
        });
        fillSeries.setData(dataToFill);
        return fillSeries;
    }

    function updateChartWithNewData(newData) {
    const lineData = newData.map(item => ({
        time: item.time_period_start,
        value: item.price_close,
    }));
    series.setData(lineData);

    // Calculate SMA
    const smaData = calculateSMA(lineData, smaPeriod);
    smaSeries.setData(smaData);

    const smaData2 = calculateSMATimesFive(lineData, smaPeriod2);
    smaSeries2.setData(smaData2);

    // Remove previous fill series if exists
    if (previousFillSeries) {
        chart.removeSeries(previousFillSeries);
    }

    // Shade the area below SMA when lineData goes under SMA
    const dataToFill = [];
    for (let i = 0; i < lineData.length; i++) {
        if (lineData[i].value < smaData[i].value) {
            dataToFill.push({
                time: lineData[i].time,
                value: lineData[i].value,
            });
        }
    }
    if (dataToFill.length > 0) {
        dataToFill.unshift({
            time: dataToFill[0].time,
            value: smaData[0].value,
        });
        previousFillSeries = chart.addAreaSeries({
            topColor: 'rgba(0, 255, 0, 0.5)', // Bright green color for shading
            bottomColor: 'rgba(0, 255, 0, 0.5)', // Bright green color for shading
            lineColor: 'rgba(0, 255, 0, 0.5)', // Bright green color for shading
            lineWidth: 0, // No line width for the fill series
        });
        previousFillSeries.setData(dataToFill);
    }
}

    // Function to calculate Simple Moving Average (SMA)
    function calculateSMA(data, period) {
        const sma = [];
        for (let i = period - 1; i < data.length; i++) {
            let sum = 0;
            for (let j = i - period + 1; j <= i; j++) {
                sum += data[j].value;
            }
            sma.push({
                time: data[i].time,
                value: sum / period,
            });
        }
        return sma;
    }

    // Function to calculate Simple Moving Average (SMA) multiplied by 5
    function calculateSMATimesFive(data, period) {
        const sma = [];
        for (let i = period - 1; i < data.length; i++) {
            let sum = 0;
            for (let j = i - period + 1; j <= i; j++) {
                sum += data[j].value;
            }
            sma.push({
                time: data[i].time,
                value: (sum / period) * 5,
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

    </script>
</body>
</html>
    """.trimIndent()
    }

    fun getDcaSimulatorLightMode(symbol: String = ""): String {
        return """
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, height=device-height, initial-scale=1.0">
<title>Date Picker Example</title>
<!-- Include CSS for datepicker -->
<link rel="stylesheet" href="https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<style>
    body {
        background-color: white;
        color: black; /* Optionally set text color to white for better visibility */
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
        background-color: #FFFFFF;
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
    .container {
        text-align: center;
    }
    .datepicker {
        display: inline-block;
        margin-right: 20px;
    }
    h2 {
        text-align: center;
    }
</style>
</head>
<body>

<div class="container">
    <!-- Date Picker 1 -->
    <div class="datepicker">
        <label for="datepicker1">Select DCA Start Date:</label>
        <input type="text" id="datepicker1" placeholder="YYYY-MM-DD">
    </div>

    <!-- Date Picker 2 -->
    <div class="datepicker">
        <label for="datepicker2">Select DCA End Date:</label>
        <input type="text" id="datepicker2" placeholder="YYYY-MM-DD">
    </div>

    <!-- Input box for accepting a number -->
    <div>
        <label for="numberInput">Enter DCA Amount:</label>
        <input type="number" id="numberInput" name="numberInput" placeholder="Enter DCA Amount">
    </div>

    <!-- Dropdown for selecting frequency -->
    <div>
        <label for="frequencySelect">Select Frequency:</label>
        <select id="frequencySelect">
            <option value="daily">Daily</option>
            <option value="weekly">Weekly</option>
            <option value="monthly">Monthly</option>
        </select>
    </div>

    <!-- Button to execute processValues function -->
    <button onclick="processValues()">Simulate DCA</button>

    <!-- Container for displaying total DCA amount -->
    <div id="totalDcaContainer"></div>
</div>

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

<!-- Include Lightweight Charts library -->
<script src="https://unpkg.com/lightweight-charts/dist/lightweight-charts.standalone.production.js"></script>
<!-- Include jQuery -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<!-- Include jQuery UI -->
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<script>
    let startDate;
    let endDate;
    let dcaAmount = getDefaultDcaAmount();
    let frequency = getDefaultFrequency();

    function setChartContainerHeight() {
    const chartContainer = document.getElementById('chartContainer');
    const totalDcaContainer = document.getElementById('totalDcaContainer');
    const windowHeight = window.innerHeight;
    const elementsAboveChart = document.querySelectorAll('.buttons-container, #additionalContent, #range-switcher, .container');
    let totalHeightAboveChart = 0;

    // Calculate the total height of all elements above the chart container
    elementsAboveChart.forEach(element => {
        totalHeightAboveChart += element.offsetHeight;
    });

    // Calculate the total height of the chart container including the totalDcaContainer
    const totalChartContainerHeight = totalHeightAboveChart + totalDcaContainer.offsetHeight;

    // Set the height of the chart container
    chartContainer.style.height = (windowHeight - totalChartContainerHeight) + 'px';
}



    // Initialize the chart with the default height
    setChartContainerHeight();

    // Add event listener to recalculate height when the window is resized
    window.addEventListener('resize', setChartContainerHeight);

    // Initialize the chart
    const chartOptions = {
        layout: {
            textColor: 'black',
            background: { type: 'solid', color: 'white' },
        },
        grid: {
            vertLines: {
                color: 'rgba(197, 203, 206, 0.5)',
            },
            horzLines: {
                color: 'rgba(197, 203, 206, 0.5)',
            },
        },
    };

    // Add the candlestick series to the chart
    const container = document.getElementById('chartContainer');
    const chart = LightweightCharts.createChart(container, chartOptions);
    let currentInterval = '1DAY'; // Default interval

    // Add line series to the chart
    const series = chart.addLineSeries();

    const smaSeries = chart.addLineSeries({
        color: 'rgba(60, 179, 113, 1)',
        lineWidth: 2,
    });

    function updateChartWithNewData(newData, dcaAmountValue, frequencyValue) {
        let accumulatedValue = 0;
		let currentDays = 0;
		
        const updatedData = newData.map(item => {
			if(frequencyValue == "daily" || (frequencyValue == "weekly" && currentDays % 7 == 0) || (frequencyValue == "monthly" && currentDays % 30 == 0)){
				accumulatedValue += dcaAmountValue / item.price_close;
			}
            currentDays+=1;
            return {
                time: item.time_period_start,
                value: accumulatedValue * item.price_close
            };
        });
        series.setData(updatedData);

		currentDays = 0;
        let accumulatedDCAValue = 0;
		
        const dcaAmount = newData.map(item => {
			if(frequencyValue == "daily" || (frequencyValue == "weekly" && currentDays % 7 == 0) || (frequencyValue == "monthly" && currentDays % 30 == 0)){
				accumulatedDCAValue += dcaAmountValue; // Simply accumulate the DCA amount for each data point
            }
			currentDays+=1;
			return {
                time: item.time_period_start,
                value: accumulatedDCAValue
            };
        });

        // Convert the dcaAmount array to the correct format
        const dcaAmountPoints = dcaAmount.map(point => ({ time: point.time, value: point.value }));
        console.log("dcaAmountPoints:", dcaAmountPoints); // Log dcaAmountPoints to console
        smaSeries.setData(dcaAmountPoints); // Set the data for the smaSeries
    }

    function getDefaultStartDate() {
        // return default end date here, e.g., tomorrow's date
        const startDate = new Date();
        startDate.setDate(startDate.getDate() - 1460); // Increment current date by 1 day
        return startDate.toISOString().slice(0, 10); // Returns tomorrow's date in "YYYY-MM-DD" format
    }

    function getDefaultEndDate() {
        // return default end date here, e.g., tomorrow's date
        const endDate = new Date();
        endDate.setDate(endDate.getDate() + 1); // Increment current date by 1 day
        return endDate.toISOString().slice(0, 10); // Returns tomorrow's date in "YYYY-MM-DD" format
    }

    function getDefaultDcaAmount() {
        // return default DCA amount here
        return 100; // Example default DCA amount
    }

    function getDefaultFrequency() {
        // return default frequency here
        return "daily"; // Example default frequency
    }

    $(document).ready(function() {
        startDate = getDefaultStartDate();
        endDate = getDefaultEndDate();

        // Date Picker 1
        $( "#datepicker1" ).datepicker({
            dateFormat: "yy-mm-dd",
            onSelect: function(selectedDate) {
                startDate = selectedDate; // Save selected start date
            }
        });

        // Date Picker 2
        $( "#datepicker2" ).datepicker({
            dateFormat: "yy-mm-dd",
            onSelect: function(selectedDate) {
                endDate = selectedDate; // Save selected end date
            }
        });

        // Set default values in input boxes
        $("#datepicker1").val(startDate);
        $("#datepicker2").val(endDate);
        $("#numberInput").val(dcaAmount);

        processValues();
    });

    function calculateDaysDifference(startDate, endDate) {
        // Convert start and end dates to Date objects
        const start = new Date(startDate);
        const end = new Date(endDate);

        // Calculate the difference in milliseconds
        const differenceInMs = end - start;

        // Convert milliseconds to days
        const daysDifference = differenceInMs / (1000 * 60 * 60 * 24);

        // Return the number of days (rounded to the nearest integer)
        return Math.round(daysDifference);
    }

    // Function to fetch data from the API endpoint and process it
    async function processValues() {
    try {
        // Fetch data from API based on user input
        const startDateValue = $("#datepicker1").val().replace(/\//g, '-');
        const endDateValue = $("#datepicker2").val().replace(/\//g, '-');
        const dcaAmountValue = parseInt($("#numberInput").val()); // Convert to number
        const frequencyValue = $("#frequencySelect").val();
        const daysBetweenStartAndEnd = calculateDaysDifference(startDateValue, endDateValue);

        const totalDcaAmount = daysBetweenStartAndEnd * dcaAmountValue;

        const response = await fetch(`https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_BTC_USD/apikey-59659DAF-46F7-4981-BCDB-6A10B727341E/history?period_id=1DAY&time_start=${'$'}{startDateValue}T00:00:00&limit=${'$'}{daysBetweenStartAndEnd}`);
        if (!response.ok) {
            throw new Error(`Failed to fetch data: ${'$'}{response.status} ${'$'}{response.statusText}`);
        }
        const apiData = await response.json();

        // Process the fetched data
        const totalAdded = apiData.reduce((total, item) => {
            // Perform calculation and add it to total
            return total + (dcaAmountValue / item.price_close);
        }, 0);

        console.log("Total BTC Accumulated:", totalAdded);
        console.log("Days between: ", calculateDaysDifference(startDateValue, endDateValue));

        updateChartWithNewData(apiData, dcaAmountValue, frequencyValue);

        // Calculate profit
        const profit = (totalAdded * apiData[apiData.length - 1].price_close - totalDcaAmount).toFixed(2);

        // Determine color based on profit
        const profitColor = profit >= 0 ? 'green' : 'red';

        // Display total DCA amount on the page
        //const totalDcaContainer = document.getElementById('totalDcaContainer');
        //totalDcaContainer.innerHTML = `
        //    <p>Total DCA Amount: ${'$'}${'$'}{totalDcaAmount.toLocaleString('en-US', {maximumFractionDigits: 2})} Current amount: ${'$'}${'$'}{(totalAdded * apiData[apiData.length - 1].price_close).toLocaleString('en-US', {maximumFractionDigits: 2})} Profit: <span style="color: ${'$'}{profitColor}">${'$'}${'$'}{profit.toLocaleString('en-US', {maximumFractionDigits: 2})}</span></p>
        //`;

        // After updating the content, recalculate the chart container height
        setChartContainerHeight();

    } catch (error) {
        console.error('Error processing data:', error);
    }
}
</script>

</body>
</html>
    """.trimIndent()
    }

    fun getPuellMultipleIndicatorDarkMode(symbol: String = ""): String {
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
        color: black; /* Optionally set text color to white for better visibility */
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
    function setChartContainerHeight() {
        const chartContainer = document.getElementById('chartContainer');
        const buttonsContainerHeight = document.querySelector('.buttons-container').offsetHeight;
        const additionalContentHeight = document.getElementById('additionalContent').offsetHeight;
        const rangeSwitcherHeight = document.getElementById('range-switcher').offsetHeight; // Include range switcher height
        const windowHeight = window.innerHeight;

        // Calculate the remaining height after considering other elements
        const remainingHeight = windowHeight - buttonsContainerHeight - additionalContentHeight - rangeSwitcherHeight;

        // Set the height of the chart container
        chartContainer.style.height = remainingHeight + 'px';
    }

    // Initialize the chart with the default height
    setChartContainerHeight();

    // Add event listener to recalculate height when the window is resized
    window.addEventListener('resize', setChartContainerHeight);

    // Initialize the chart
    const chartOptions = {
	rightPriceScale: {
		visible: true,
    borderColor: 'rgba(197, 203, 206, 1)',
	},
	leftPriceScale: {
		visible: true,
    borderColor: 'rgba(197, 203, 206, 1)',
	},
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
    };

    // Add the candlestick series to the chart
    const container = document.getElementById('chartContainer');
    const chart = LightweightCharts.createChart(container, chartOptions);
    let currentInterval = '1DAY'; // Default interval

    // Add line series to the chart
    const series = chart.addLineSeries();
    const smaSeries = chart.addLineSeries({
        color: 'rgba(60, 179, 113, 1)',
        lineWidth: 2,
    });
    const puellSeries = chart.addLineSeries({
		priceScaleId: 'left',
        color: 'rgba(255, 165, 0, 1)', // Orange color for Puell Multiple
        lineWidth: 2,
    });
	

    let previousFillSeries; // Variable to store previous fill series

    const smaPeriod = 365; // Define the SMA period

    // Function to fetch data from the API endpoint
    async function fetchDataAndUpdateChart() {
        try {
            const response = await fetch(`https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_BTC_USD/apikey-59659DAF-46F7-4981-BCDB-6A10B727341E/history?period_id=1DAY&time_start=2011-01-01T00:00:00&limit=5000`);
            if (!response.ok) {
                throw new Error('Failed to fetch data: ' + response.statusText);
            }
            const apiData = await response.json();
            updateChartWithNewData(apiData);
        } catch (error) {
            console.error('Error fetching data:', error);
        }
    }

    function updateChartWithNewData(newData) {
        const lineData = newData.map(item => ({
            time: item.time_period_start.substring(0, 10),
            value: item.price_close,
        }));
        series.setData(lineData);

        // Calculate SMA
        const smaData = calculateSMA(lineData, smaPeriod);

        // Calculate Puell Multiple
        const puellMultipleData = calculatePuellMultiple(lineData, smaData);
        puellSeries.setData(puellMultipleData);
    }

    // Function to calculate Simple Moving Average (SMA)
    function calculateSMA(data, period) {
        const sma = [];
        for (let i = period - 1; i < data.length; i++) {
            let sum = 0;
            for (let j = i - period + 1; j <= i; j++) {
                sum += data[j].value;
            }
            sma.push({
                time: data[i].time,
                value: sum / period,
            });
        }
        return sma;
    }

    function calculatePuellMultiple(priceData, smaData) {
    const puellMultiple = [];
    
    // Define halving events and corresponding daily issuances
    const halvingEvents = [
		{ date: '2012-11-28', dailyIssuanceBTC: 3600 }, // First halving
        { date: '2016-07-09', dailyIssuanceBTC: 1800 }, // Second halving
        { date: '2020-05-11', dailyIssuanceBTC: 900 },  // Third halving
		{ date: '2024-04-19', dailyIssuanceBTC: 450 },  // Fourth/Current halving
    ];

    for (let i = 0; i < priceData.length; i++) {
        const time = priceData[i].time;
        const dailyIssuanceEvent = findHalvingEvent(time, halvingEvents);
        
        if (dailyIssuanceEvent) {
            const dailyIssuanceBTC = dailyIssuanceEvent.dailyIssuanceBTC;
            const dailyIssuanceValue = dailyIssuanceBTC * priceData[i].value;

            const smaItem = smaData.find(smaItem => smaItem.time === time);
            const smaIssuanceValue = smaItem ? smaItem.value * dailyIssuanceBTC : 0;
            
            if (smaIssuanceValue !== 0) {
                puellMultiple.push({
                    time: time,
                    value: dailyIssuanceValue / smaIssuanceValue,
                });
            }
        }
    }
    return puellMultiple;
}

// Function to find the halving event corresponding to the given date
function findHalvingEvent(date, halvingEvents) {
    for (let i = 0; i < halvingEvents.length; i++) {
        if (date >= halvingEvents[i].date) {
            return halvingEvents[i];
        }
    }
    return null;
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

    </script>
</body>
</html>
    """.trimIndent()
    }

    fun getPuellMultipleIndicatorLightMode(symbol: String = ""): String {
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
        background-color: white;
        color: black; /* Optionally set text color to white for better visibility */
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
        background-color: #FFFFFF;
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
    function setChartContainerHeight() {
        const chartContainer = document.getElementById('chartContainer');
        const buttonsContainerHeight = document.querySelector('.buttons-container').offsetHeight;
        const additionalContentHeight = document.getElementById('additionalContent').offsetHeight;
        const rangeSwitcherHeight = document.getElementById('range-switcher').offsetHeight; // Include range switcher height
        const windowHeight = window.innerHeight;

        // Calculate the remaining height after considering other elements
        const remainingHeight = windowHeight - buttonsContainerHeight - additionalContentHeight - rangeSwitcherHeight;

        // Set the height of the chart container
        chartContainer.style.height = remainingHeight + 'px';
    }

    // Initialize the chart with the default height
    setChartContainerHeight();

    // Add event listener to recalculate height when the window is resized
    window.addEventListener('resize', setChartContainerHeight);

    // Initialize the chart
    const chartOptions = {
	rightPriceScale: {
		visible: true,
    borderColor: 'rgba(197, 203, 206, 1)',
	},
	leftPriceScale: {
		visible: true,
    borderColor: 'rgba(197, 203, 206, 1)',
	},
        layout: {
            textColor: 'black',
            background: { type: 'solid', color: 'white' },
        },
        grid: {
            vertLines: {
                color: 'rgba(197, 203, 206, 0.5)',
            },
            horzLines: {
                color: 'rgba(197, 203, 206, 0.5)',
            },
        },
    };

    // Add the candlestick series to the chart
    const container = document.getElementById('chartContainer');
    const chart = LightweightCharts.createChart(container, chartOptions);
    let currentInterval = '1DAY'; // Default interval

    // Add line series to the chart
    const series = chart.addLineSeries();
    const smaSeries = chart.addLineSeries({
        color: 'rgba(60, 179, 113, 1)',
        lineWidth: 2,
    });
    const puellSeries = chart.addLineSeries({
		priceScaleId: 'left',
        color: 'rgba(255, 165, 0, 1)', // Orange color for Puell Multiple
        lineWidth: 2,
    });
	

    let previousFillSeries; // Variable to store previous fill series

    const smaPeriod = 365; // Define the SMA period

    // Function to fetch data from the API endpoint
    async function fetchDataAndUpdateChart() {
        try {
            const response = await fetch(`https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_BTC_USD/apikey-59659DAF-46F7-4981-BCDB-6A10B727341E/history?period_id=1DAY&time_start=2011-01-01T00:00:00&limit=5000`);
            if (!response.ok) {
                throw new Error('Failed to fetch data: ' + response.statusText);
            }
            const apiData = await response.json();
            updateChartWithNewData(apiData);
        } catch (error) {
            console.error('Error fetching data:', error);
        }
    }

    function updateChartWithNewData(newData) {
        const lineData = newData.map(item => ({
            time: item.time_period_start.substring(0, 10),
            value: item.price_close,
        }));
        series.setData(lineData);

        // Calculate SMA
        const smaData = calculateSMA(lineData, smaPeriod);

        // Calculate Puell Multiple
        const puellMultipleData = calculatePuellMultiple(lineData, smaData);
        puellSeries.setData(puellMultipleData);
    }

    // Function to calculate Simple Moving Average (SMA)
    function calculateSMA(data, period) {
        const sma = [];
        for (let i = period - 1; i < data.length; i++) {
            let sum = 0;
            for (let j = i - period + 1; j <= i; j++) {
                sum += data[j].value;
            }
            sma.push({
                time: data[i].time,
                value: sum / period,
            });
        }
        return sma;
    }

    function calculatePuellMultiple(priceData, smaData) {
    const puellMultiple = [];
    
    // Define halving events and corresponding daily issuances
    const halvingEvents = [
		{ date: '2012-11-28', dailyIssuanceBTC: 3600 }, // First halving
        { date: '2016-07-09', dailyIssuanceBTC: 1800 }, // Second halving
        { date: '2020-05-11', dailyIssuanceBTC: 900 },  // Third halving
		{ date: '2024-04-19', dailyIssuanceBTC: 450 },  // Fourth/Current halving
    ];

    for (let i = 0; i < priceData.length; i++) {
        const time = priceData[i].time;
        const dailyIssuanceEvent = findHalvingEvent(time, halvingEvents);
        
        if (dailyIssuanceEvent) {
            const dailyIssuanceBTC = dailyIssuanceEvent.dailyIssuanceBTC;
            const dailyIssuanceValue = dailyIssuanceBTC * priceData[i].value;

            const smaItem = smaData.find(smaItem => smaItem.time === time);
            const smaIssuanceValue = smaItem ? smaItem.value * dailyIssuanceBTC : 0;
            
            if (smaIssuanceValue !== 0) {
                puellMultiple.push({
                    time: time,
                    value: dailyIssuanceValue / smaIssuanceValue,
                });
            }
        }
    }
    return puellMultiple;
}

// Function to find the halving event corresponding to the given date
function findHalvingEvent(date, halvingEvents) {
    for (let i = 0; i < halvingEvents.length; i++) {
        if (date >= halvingEvents[i].date) {
            return halvingEvents[i];
        }
    }
    return null;
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

    </script>
</body>
</html>
    """.trimIndent()
    }

    fun getMonthlyGains() : String{
        return """
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Bitcoin Monthly Performance</title>
  <style>
    table {
      border-collapse: collapse;
      width: 100%;
      table-layout: fixed;
      overflow-x: auto;
    }
    th, td {
      border: 1px solid #dddddd;
      text-align: center;
      padding: 8px;
      white-space: nowrap;
    }
    th {
      background-color: transparent;
    }
    .positive {
      background-color: lightgreen;
    }
    .negative {
      background-color: lightcoral;
    }
	
	/* Centering the h3 tag */
	h3 {
	  text-align: center;
	}
	
	/* Responsive styles */
    @media screen and (max-width: 768px) {
      table {
        font-size: 9px; /* Reduce font size for smaller screens */
      }
    }
  </style>
</head>
<body>
<h3>Bitcoin Monthly Performance</h3>
<div style="overflow-x: auto;">
  <table id="btcTable">
    <thead>
      <tr>
        <th>Year</th>
        <th>Jan  </th>
        <th>Feb  </th>
        <th>Mar  </th>
        <th>Apr  </th>
        <th>May  </th>
        <th>Jun  </th>
        <th>Jul  </th>
        <th>Aug  </th>
        <th>Sep  </th>
        <th>Oct  </th>
        <th>Nov  </th>
        <th>Dec  </th>
      </tr>
    </thead>
    <tbody>
    </tbody>
  </table>
</div>

<!-- New table for win/loss -->

<div style="overflow-x: auto;">
  <table id="btcPerformanceTable">
    <thead>
      <tr>
        <th>Month</th>
        <th>Wins</th>
        <th>Losses</th>
        <th>Percentage Win</th>
      </tr>
    </thead>
    <tbody>
    </tbody>
  </table>
</div>

<script>
  // Function to fetch data from the API and generate the table
  async function generateTable() {
    var tableBody = document.querySelector('#btcTable tbody');
    var years = [2012, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2023, 2024]; // Add more years as needed
    var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

    // Fetch data from the API
    const response = await fetch('https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_BTC_USD/apikey-59659DAF-46F7-4981-BCDB-6A10B727341E/history?period_id=1MTH&time_start=2012-01-01T00:00:00&limit=500');
    const data = await response.json();

    // Prepare an object to hold percentage gains for each month/year
    var percentageData = {};
    years.forEach(year => {
      percentageData[year] = {};
      months.forEach(month => {
        percentageData[year][month] = null; // Initialize to null
      });
    });

    // Calculate percentage gains and update the object
    data.forEach(entry => {
      const year = parseInt(entry.time_period_start.substr(0, 4));
      const month = parseInt(entry.time_period_start.substr(5, 2)) - 1; // Months are zero-based in JavaScript
      const percentageGain = ((entry.price_close - entry.price_open) / entry.price_close) * 100;
      if (!isNaN(percentageGain)) { // Check if percentageGain is not NaN
        percentageData[year][months[month]] = percentageGain.toFixed(2) + '%';

        // Add class to cell based on percentage gain
        if (percentageGain > 0) {
          percentageData[year][months[month] + '_class'] = 'positive';
        } else if (percentageGain < 0) {
          percentageData[year][months[month] + '_class'] = 'negative';
        }
      }
    });

    // Generate table rows
    years.forEach(year => {
      var row = document.createElement('tr');
      var yearCell = document.createElement('td');
      yearCell.textContent = year;
      row.appendChild(yearCell);

      months.forEach(month => {
        var cell = document.createElement('td');
        cell.textContent = percentageData[year][month] || '-'; // Display '-' for missing data
        if (percentageData[year][month + '_class']) {
          cell.classList.add(percentageData[year][month + '_class']); // Add class to cell
        }
        row.appendChild(cell);
      });

      tableBody.appendChild(row);
    });
  }

  // Call the function to generate the percentage gains table
  generateTable();

  // Function to fetch data from the API and generate the performance table
  async function generatePerformanceTable() {
    var tableBody = document.querySelector('#btcPerformanceTable tbody');
    var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

    // Fetch data from the API (same as previous function)
    const response = await fetch('https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_BTC_USD/apikey-59659DAF-46F7-4981-BCDB-6A10B727341E/history?period_id=1MTH&time_start=2012-01-01T00:00:00&limit=500');
    const data = await response.json();

    // Prepare objects to hold wins, losses, and total gains for each month (same as previous function)
    var wins = {};
    var losses = {};
    var totalGains = {};
    months.forEach(month => {
      wins[month] = 0;
      losses[month] = 0;
      totalGains[month] = 0;
    });

    // Calculate wins, losses, and total gains (same as previous function)
    data.forEach(entry => {
      const month = parseInt(entry.time_period_start.substr(5, 2)) - 1;
      const percentageGain = ((entry.price_close - entry.price_open) / entry.price_close) * 100;

      if (!isNaN(percentageGain)) { // Check if percentageGain is not NaN
        if (percentageGain > 0) {
          wins[months[month]]++;
        } else if (percentageGain < 0) {
          losses[months[month]]++;
        }
        totalGains[months[month]] += percentageGain;
      }
    });

    // Generate table rows (same as previous function)
    months.forEach(month => {
      var row = document.createElement('tr');
      var monthCell = document.createElement('td');
      monthCell.textContent = month;
      row.appendChild(monthCell);

      var winsCell = document.createElement('td');
      winsCell.textContent = wins[month];
      row.appendChild(winsCell);

      var lossesCell = document.createElement('td');
      lossesCell.textContent = losses[month];
      row.appendChild(lossesCell);

      var percentageWinCell = document.createElement('td');
      var percentageWin = ((wins[month] / (wins[month] + losses[month])) * 100).toFixed(2);
      percentageWinCell.textContent = isNaN(percentageWin) ? '-' : percentageWin + '%';
      row.appendChild(percentageWinCell);

      tableBody.appendChild(row);
    });
  }

  // Call the function to generate the performance table
  generatePerformanceTable();
</script>

</body>
</html>
    """.trimIndent()
    }

    fun getFeedbackPage() : String{
        return """
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Contact Me</title>
    <link rel="stylesheet" href="style.css">
    <style>
        body {
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            background-color: #f4f4f4;
        }

        .container {
            position: relative;
            background-color: #fff;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            width: 90%;
            max-width: 400px;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
        }

        h2 {
            text-align: center;
            margin-bottom: 20px; /* Add some space below the title */
        }

        input,
        textarea {
            width: calc(100% - 20px); /* Adjust width minus padding and border */
            margin-bottom: 15px;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 5px;
            box-sizing: border-box;
        }

        button {
            width: calc(100% - 20px); /* Adjust width minus padding and border */
            padding: 10px;
            border: none;
            border-radius: 5px;
            background-color: #007bff;
            color: #fff;
            cursor: pointer;
            transition: background-color 0.3s ease;
        }

        button:hover {
            background-color: #0056b3;
        }

        span {
            display: block;
            margin-top: 10px;
            color: green;
        }

        .error {
            color: red;
        }
    </style>
</head>

<body>

    <div class="container">
        <h2>Feedback/Reports</h2>
        <form name="submit-to-google-sheet">
            <input type="text" name="Name" placeholder="Your Name*" required>
            <input type="email" name="Email" placeholder="Your Email (Optional)">
            <textarea name="Message" rows="6" placeholder="Your Message*" required></textarea>
            <button id="submitbtn" type="submit" class="btn btn2">Submit</button>
        </form>
        <span id="feedbackmsg"></span>
    </div>

    <script>
        const scriptURL = 'https://script.google.com/macros/s/AKfycbw9ZN2zw-rlXpRS_gkhXWkfklUlGZ1mcrqAYc7WZv8fv3soPvfEfn1x_4uewrFQdapf/exec'
        const form = document.forms['submit-to-google-sheet']
        const feedbackmsg = document.getElementById("feedbackmsg")
        const submitbtn = document.getElementById("submitbtn");

        form.addEventListener('submit', e => {
            submitbtn.innerHTML = "Submitting..."
            e.preventDefault()
            fetch(scriptURL, { method: 'POST', body: new FormData(form) })
                .then(response => {
                    if (response.ok) {
                        feedbackmsg.innerHTML = "Report sent successfully. Thank you for your feedback!";
                        feedbackmsg.classList.remove("error");
                    } else {
                        throw new Error('Network response was not ok.');
                    }
                    submitbtn.innerHTML = "Submit";
                    setTimeout(function () {
                        feedbackmsg.innerHTML = "";
                    }, 5000);
                    form.reset();

                })
                .catch(error => {
                    feedbackmsg.innerHTML = "Error! Something went wrong :/";
                    feedbackmsg.classList.add("error");
                });
        });
    </script>

</body>

</html>

    """.trimIndent()
    }
}

