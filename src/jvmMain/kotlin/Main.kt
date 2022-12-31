@file:OptIn(ExperimentalComposeUiApi::class)

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import org.jetbrains.skiko.toBufferedImage
import kotlin.math.roundToInt

enum class Animation {
    IDLE,
    WALKING
}


val fontFamily = FontFamily(
    Font(
        "pokemon.ttf",
        weight = FontWeight.W400,
        style = FontStyle.Normal
    )
)

val textStyle = TextStyle(
    fontSize = 10.sp,
    lineHeight = 16.sp,
    fontFamily = fontFamily
)

fun main() = application {
    val pressedKeys = remember { mutableSetOf<Key>() }
    var lastUpKey: Key? by remember { mutableStateOf(null) }

    Window(
        onCloseRequest = ::exitApplication,
        resizable = false,
        onPreviewKeyEvent = {
            when {
                (it.type == KeyEventType.KeyDown) -> {
                    pressedKeys.add(it.key)
                    true
                }

                (it.type == KeyEventType.KeyUp) -> {
                    pressedKeys.remove(it.key)
                    lastUpKey = it.key
                    true
                }

                else -> false
            }
        },
        state = WindowState(size = DpSize(Consts.MOVEMENT_DISTANCE * 16, Consts.MOVEMENT_DISTANCE * 16))
    ) {
        Engine { totalFrame, collisionDetector, stash ->
            Map()
            NPC(Consts.MOVEMENT_DISTANCE * 5, Consts.MOVEMENT_DISTANCE * 5, totalFrame, collisionDetector)
            Object("Pokeball", Consts.MOVEMENT_DISTANCE * 3, Consts.MOVEMENT_DISTANCE * 3, collisionDetector, stash)

            var dialogItems: List<String>? by remember { mutableStateOf(null) }

            Hero(totalFrame, pressedKeys, collisionDetector, stash, dialogItems == null)

            if (lastUpKey == Key.Escape) {
                if (dialogItems != null) {
                    dialogItems = null
                } else {
                    dialogItems = stash.pickedUp.map { it.name }
                }
                lastUpKey = null
            }

            dialogItems.let { items ->
                if (!items.isNullOrEmpty()) {
                    Dialog(
                        10,
                        10,
                        pressedKeys,
                        totalFrame,
                        items,
                        {
                            if (it == 0) {
                                dialogItems = listOf("Yes", "Yessir", "Oui")
                            } else {
                                dialogItems = null
                            }
                        },
                        Modifier.align(Alignment.TopEnd)
                    )
                }
            }
        }
    }
}

@Composable
fun Map() {
    val map by remember { mutableStateOf(json.decodeFromString(SavedMap.serializer(), useResource("map.json") { stream -> stream.bufferedReader().use { it.readText() } })) }
    val mapImage: ImageBitmap by remember { mutableStateOf(useResource("map.png") { loadImageBitmap(it) }) }

    Canvas(Modifier.fillMaxSize()) {
        map.points.entries.map { it.toPair() }.forEach { (destination, source) ->
            // TODO: extract each sub images once in order to improve performance
            drawImage(
                image = mapImage.asSkiaBitmap().toBufferedImage().getSubimage(source.x * 16, source.y * 16, 16, 16).toComposeImageBitmap(),
                dstOffset = IntOffset(
                    x = (destination.x * Consts.MOVEMENT_DISTANCE).toPx().roundToInt(),
                    y = (destination.y * Consts.MOVEMENT_DISTANCE).toPx().roundToInt()
                ),
                dstSize = IntSize(Consts.MOVEMENT_DISTANCE.toPx().roundToInt(), Consts.MOVEMENT_DISTANCE.toPx().roundToInt()),
                filterQuality = FilterQuality.None
            )
        }
    }
}

