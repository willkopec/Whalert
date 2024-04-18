package com.willkopec.whalert.util

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
            <script src="https://unpkg.com/lightweight-charts/dist/lightweight-charts.standalone.production.js"></script>
        </head>
        <body>
            <script>
                var chart = LightweightCharts.createChart(document.body, {
                  width: window.screen.width - 10,
                  height: window.screen.height - 310,
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
                ];
                series.setData(data);

                var datesForMarkers = [data[data.length - 39], data[data.length - 19]];
                var indexOfMinPrice = 0;
                for (var i = 1; i < datesForMarkers.length; i++) {
                	if (datesForMarkers[i].high < datesForMarkers[indexOfMinPrice].high) {
                		indexOfMinPrice = i;
                	}
                }

                var markers = [{ time: data[data.length - 48].time, position: 'aboveBar', color: '#f68410', shape: 'circle', text: 'D' }];
                for (var i = 0; i < datesForMarkers.length; i++) {
                	if (i !== indexOfMinPrice) {
                		markers.push({ time: datesForMarkers[i].time, position: 'aboveBar', color: '#e91e63', shape: 'arrowDown', text: 'Sell @ ' + Math.floor(datesForMarkers[i].high + 2) });
                	} else {
                		markers.push({ time: datesForMarkers[i].time, position: 'belowBar', color: '#2196F3', shape: 'arrowUp', text: 'Buy @ ' + Math.floor(datesForMarkers[i].low - 2) });
                	}
                }
                series.setMarkers(markers);
            </script>
        </body>
        </html>
    """.trimIndent()
    }
}