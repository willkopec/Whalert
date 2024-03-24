import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.willkopec.whalert.breakingnews.WhalertViewModel
import com.willkopec.whalert.model.CryptoItem
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun DraggableBubbleScreen(
    viewModel: WhalertViewModel = hiltViewModel()
) {
    var bubbles by remember { mutableStateOf(listOf(0.1f, -0.05f)) }
    val currentCryptoBubbleList by viewModel.breakingNews.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        /*bubbles.forEach { percentageChange ->
            DraggableBubble(
                size = 50.dp,
                percentageChange = percentageChange,
                onDrag = { newPosition ->
                    // Handle drag if needed
                }
            )
        }*/
        currentCryptoBubbleList.forEach {
            DraggableBubble(
                size = 75.dp,
                cryptoInfo = it,
                onDrag = { newPosition ->
                    // Handle drag if needed
                }
            )
        }
    }
}

@Composable
fun DraggableBubble(
    size: Dp,
    cryptoInfo: CryptoItem,
    onDrag: (Offset) -> Unit
) {
    val percentageChange = cryptoInfo.price_change_percentage_24h.toFloat()
    val color = if (percentageChange >= 0) Color.Green else Color.Red
    val adjustedSize = size + (percentageChange).dp // Adjust size based on percentage change

    var position by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    position.x.roundToInt(),
                    position.y.roundToInt()
                )
            }
            .background(color, shape = CircleShape)
            .size(adjustedSize)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    position += dragAmount
                    onDrag(position)
                }
            }
    ){
        var textSize: TextUnit = 5.sp
        if(abs(percentageChange) > 15){
            textSize = 3.sp
        } else if(abs(percentageChange) > 25){
            textSize = 4.sp
        } else if(abs(percentageChange) > 35){
            textSize = 5.sp
        }
        Text(
            text = "${cryptoInfo.name} \n ${cryptoInfo.price_change_percentage_24h}",
            textAlign = TextAlign.Center,
            fontSize = 12.sp
        )
    }
}
