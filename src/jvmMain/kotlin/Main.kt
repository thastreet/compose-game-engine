
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
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

data class StashedObject(
    val pickedUp: Boolean,
    val name: String
)

data class CollisionInfo(
    val id: String,
    val rect: Rect,
    val canBePickedUp: Boolean
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
        MaterialTheme {
            Surface {
                val totalFrame = remember { mutableStateOf(0) }
                val states = remember { mutableStateOf(mapOf<String, CollisionInfo>()) }
                val objects = remember { mutableStateOf(mapOf<String, StashedObject>()) }

                val collisionDetector = remember {
                    object : CollisionDetector {
                        override fun updatePosition(state: State) {
                            states.value = states.value.toMutableMap().apply {
                                set(
                                    state.id,
                                    CollisionInfo(
                                        state.id,
                                        Rect(Offset(state.x.value, state.y.value), state.size),
                                        state is ObjectState
                                    )
                                )
                            }
                        }

                        override fun detectCollision(state: CharacterState): Boolean =
                            facing(state) != null

                        override fun facing(state: CharacterState): CollisionInfo? =
                            states.value.entries.firstOrNull { state.id != it.key && state.getProjectedRect(state.direction).overlaps(it.value.rect) }?.value

                        override fun remove(id: String) {
                            states.value = states.value.toMutableMap().apply { remove(id) }
                        }
                    }
                }

                val stash = remember {
                    object : Stash {
                        override fun register(id: String, stashedObject: StashedObject) {
                            objects.value = objects.value.toMutableMap().apply { set(id, stashedObject) }
                        }

                        override fun pickup(id: String) {
                            objects.value = objects.value.toMutableMap().apply { set(id, getValue(id).copy(pickedUp = true)) }
                        }

                        override fun isPickedUp(id: String): Boolean =
                            objects.value[id]?.pickedUp == true
                    }
                }

                Box(Modifier.fillMaxSize()) {
                    NPC(Consts.MOVEMENT_DISTANCE * 5, Consts.MOVEMENT_DISTANCE * 5, totalFrame.value, collisionDetector)
                    Object("Pokeball", Consts.MOVEMENT_DISTANCE * 3, Consts.MOVEMENT_DISTANCE * 3, collisionDetector, stash)
                    Hero(totalFrame.value, pressedKeys, collisionDetector, stash)
                    Dialog(10, 10, Modifier.align(Alignment.TopEnd)) {
                        val text = objects.value.values.filter { it.pickedUp }.joinToString("\n") { it.name.uppercase() }
                        Text(text, modifier = it, style = textStyle)
                    }
                }

                Engine(totalFrame)
            }
        }
    }
}
