import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.willkopec.whalert.breakingnews.WhalertViewModel
import com.willkopec.whalert.model.coingecko.CryptoItem
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.roundToInt


@Composable
fun DraggableBubbleScreen(
    viewModel: WhalertViewModel = hiltViewModel(),
    bottomBarHeight: Int
) {
    val density = LocalDensity.current
    val screenWidth = with(density) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
    val currentCryptoBubbleList by viewModel.bubbleList.collectAsState()
    var item: Int = 0

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        var xOffset = 0f // Initialize xOffset as float
        var yOffset = 0f

        currentCryptoBubbleList?.forEach {
            item++

            DraggableBubble(
                size = 40.dp,
                cryptoInfo = it,
                position = Offset(xOffset, yOffset), // Pass the xOffset
                onDrag = { newPosition ->
                    // Handle drag if needed
                },
                bottomBarHeight = bottomBarHeight
            )

            xOffset += with(LocalDensity.current) { 70.dp.toPx() + ((0..10).random()).dp.toPx()} // Increment xOffset by bubble size (75dp) + gap (50dp)

            // Reset xOffset if it exceeds the screen width
            if (xOffset + with(LocalDensity.current) { 35.dp.toPx() } > screenWidth) {
                xOffset = 0f
                yOffset += with(LocalDensity.current) { 55.dp.toPx() + ((0..10).random()).dp.toPx()}
            }
        }
    }
}

@Composable
fun DraggableBubble(
    size: Dp,
    cryptoInfo: CryptoItem,
    position: Offset, // Receive position
    onDrag: (Offset) -> Unit,
    bottomBarHeight: Int
) {
    val percentageChange = cryptoInfo.price_change_percentage_24h.toFloat()
    val color = if (percentageChange >= 0) Color.Green else Color.Red
    val adjustedSize = size + (abs(percentageChange) * 2).dp // Adjust size based on percentage change

    var bubblePosition by remember { mutableStateOf(position) } // Use bubblePosition instead of position

    val density = LocalDensity.current
    val screenWidth = with(density) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
    val screenHeight = with(density) { LocalConfiguration.current.screenHeightDp.dp.toPx() }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    bubblePosition.x.roundToInt(),
                    bubblePosition.y.roundToInt()
                )
            }
            .background(color, shape = CircleShape)
            .size(adjustedSize)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()

                    // Calculate new position
                    val newX = (bubblePosition.x + dragAmount.x).coerceIn(0f, screenWidth - adjustedSize.toPx())
                    val newY = (bubblePosition.y + dragAmount.y)
                        .coerceIn(0f, screenHeight - adjustedSize.toPx() - bottomBarHeight.dp.toPx())

                    bubblePosition = Offset(newX, newY)
                    onDrag(bubblePosition)
                }
            }
    ) {
        val df = DecimalFormat("#.##")
        df.maximumFractionDigits = 2

        var percentText : String = df.format(cryptoInfo.price_change_percentage_24h)
        if(cryptoInfo.price_change_percentage_24h >= 0){
            percentText = "+${df.format(cryptoInfo.price_change_percentage_24h)}"
        }

        var textSize: TextUnit = 10.sp
        Text(
            modifier = Modifier.fillMaxWidth().align(Alignment.Center),
            text = "${cryptoInfo.symbol.uppercase()} \n ${percentText}%",
            textAlign = TextAlign.Center,
            fontSize = textSize,
            fontWeight = FontWeight.Bold,
            lineHeight = 10.sp,
            color = Color.Black
        )
    }
}



data class Bubble(var center: Offset, val radius: Float, val color: Color, var velocity: Offset = Offset.Zero, var dragging: Boolean = false)

/*@Composable
fun DraggableBubble(defaultSize: Float,
                    cryptoInfo: List<CryptoItem>) {

    var bubbles by remember { mutableStateOf(listOf(Bubble(Offset(50f, 50f), 50f, Color.Red), Bubble(Offset(250f, 250f), 50f, Color.Blue))) }
    var thisCount: Int = 0

    cryptoInfo.map { thisCrypto ->
        val percentageChange = thisCrypto.price_change_percentage_24h.toFloat()
        val color = if (percentageChange >= 0) Color.Green else Color.Red

        if(thisCount <= 4){
            bubbles += Bubble(Offset(50f, 50f), defaultSize, color)
        }

        thisCount++

    }
    //listOf(Bubble(Offset(50f, 50f), 50f, Color.Red), Bubble(Offset(250f, 250f), 50f, Color.Blue))


    Canvas(modifier = Modifier.fillMaxSize()
        .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
                bubbles = bubbles.map { bubble ->
                    if (change.position.x in (bubble.center.x - bubble.radius)..(bubble.center.x + bubble.radius) &&
                        change.position.y in (bubble.center.y - bubble.radius)..(bubble.center.y + bubble.radius)
                    ) {
                        bubble.copy(center = bubble.center + dragAmount)
                    } else {
                        bubble
                    }
                }
            }
        }) {
        //drawBackground()
        bubbles.forEachIndexed { index, bubble ->
            val updatedBubble = calculatePosition(bubble, index, bubbles)
            bubbles = bubbles.toMutableList().also { it[index] = updatedBubble }
            drawCircle(updatedBubble.color, updatedBubble.radius, updatedBubble.center)
        }
    }
}

fun calculatePosition(bubble: Bubble, index: Int, bubbles: List<Bubble>): Bubble {
    if (!bubble.dragging) {
        return bubble
    }

    var newVelocity = bubble.velocity
    var newPosition = bubble.center + bubble.velocity

    for ((i, otherBubble) in bubbles.withIndex()) {
        if (i != index) {
            val dx = newPosition.x - otherBubble.center.x
            val dy = newPosition.y - otherBubble.center.y
            val distance = sqrt((dx * dx + dy * dy).toDouble())

            if (distance < bubble.radius + otherBubble.radius) {
                // Bounce off
                val normalX = dx / distance
                val normalY = dy / distance

                val dotProduct = bubble.velocity.x * normalX + bubble.velocity.y * normalY
                newVelocity -= Offset((dotProduct * normalX).toFloat(), (dotProduct * normalY).toFloat())
                newVelocity *= 0.8f

                newPosition += Offset((normalX * (bubble.radius + otherBubble.radius - distance)).toFloat(),
                    (normalY * (bubble.radius + otherBubble.radius - distance)).toFloat()
                )
            }
        }
    }

    return Bubble(newPosition, bubble.radius, bubble.color, newVelocity, bubble.dragging)
}

@Composable
fun drawBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(Color.White)
    }
}*/