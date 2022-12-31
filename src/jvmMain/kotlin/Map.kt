import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.times
import org.jetbrains.skiko.toBufferedImage
import kotlin.math.roundToInt

@Composable
fun Map(collisionDetector: CollisionDetector) {
    val tileSizePx = 16
    val map by remember { mutableStateOf(json.decodeFromString(SavedMap.serializer(), useResource("map.json") { stream -> stream.bufferedReader().use { it.readText() } })) }
    val mapImage by remember { mutableStateOf(useResource("map.png") { loadImageBitmap(it) }) }

    val images by remember {
        mutableStateOf(
            map.points.values.associateWith {
                mapImage
                    .asSkiaBitmap()
                    .toBufferedImage()
                    .getSubimage(it.x * tileSizePx, it.y * tileSizePx, tileSizePx, tileSizePx)
                    .toComposeImageBitmap()
            }
        )
    }

    Canvas(Modifier.fillMaxSize()) {
        map.points.entries.map { it.toPair() }.forEach { (destination, source) ->
            drawImage(
                image = images.getValue(source),
                dstOffset = IntOffset(
                    x = (destination.x * Consts.CASE_SIZE).toPx().roundToInt(),
                    y = (destination.y * Consts.CASE_SIZE).toPx().roundToInt()
                ),
                dstSize = IntSize(Consts.CASE_SIZE.toPx().roundToInt(), Consts.CASE_SIZE.toPx().roundToInt()),
                filterQuality = FilterQuality.None
            )
        }
    }

    LaunchedEffect(true) {
        map.collisions.forEach {
            collisionDetector.updatePosition(it)
        }
    }
}