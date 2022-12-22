
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

enum class Animation {
    IDLE,
    WALKING
}

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
        }
    ) {
        MaterialTheme {
            Surface {
                val totalFrame = remember { mutableStateOf(0) }

                Box(
                    Modifier
                        .fillMaxSize()
                ) {
                    NPC(
                        Consts.MOVEMENT_DISTANCE * 10,
                        Consts.MOVEMENT_DISTANCE * 10,
                        totalFrame.value
                    )
                    NPC(
                        Consts.MOVEMENT_DISTANCE * 5,
                        Consts.MOVEMENT_DISTANCE * 5,
                        totalFrame.value
                    )
                    Hero(totalFrame.value, pressedKeys)
                }

                Engine(totalFrame)
            }
        }
    }
}
