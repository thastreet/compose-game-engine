import androidx.compose.material.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

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
    fontSize = 12.sp,
    lineHeight = 20.sp,
    fontFamily = fontFamily
)

fun main() = application {
    val pressedKeys = remember { mutableSetOf<Key>() }

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
                    true
                }

                else -> false
            }
        },
        state = WindowState(size = DpSize(Consts.MOVEMENT_DISTANCE * 16, Consts.MOVEMENT_DISTANCE * 16))
    ) {
        Engine { totalFrame, collisionDetector, stash ->
            NPC(Consts.MOVEMENT_DISTANCE * 5, Consts.MOVEMENT_DISTANCE * 5, totalFrame, collisionDetector)
            Object("Pokeball", Consts.MOVEMENT_DISTANCE * 3, Consts.MOVEMENT_DISTANCE * 3, collisionDetector, stash)
            Hero(totalFrame, pressedKeys, collisionDetector, stash)
            Dialog(10, 10, Modifier.align(Alignment.TopEnd)) { modifier ->
                Text(
                    text = stash.pickedUp.joinToString("\n") { stashedObject -> stashedObject.name.uppercase() },
                    modifier = modifier,
                    style = textStyle
                )
            }
        }
    }
}
