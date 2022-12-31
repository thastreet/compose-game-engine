import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.times
import java.util.UUID

data class StashedObject(
    val name: String,
    val pickedUp: Boolean = false
)

data class CollisionInfo(
    val id: String,
    val rect: Rect,
    val canBePickedUp: Boolean
)

@Composable
fun Engine(content: @Composable BoxScope.(Int, CollisionDetector, Stash) -> Unit) {
    MaterialTheme {
        Surface {
            Box(Modifier.fillMaxSize()) {
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

                        override fun updatePosition(indexPoint: IndexPoint) {
                            states.value = states.value.toMutableMap().apply {
                                val id = UUID.randomUUID().toString()

                                set(
                                    id,
                                    CollisionInfo(
                                        id,
                                        Rect(Offset((indexPoint.x * Consts.CASE_SIZE).value, (indexPoint.y * Consts.CASE_SIZE).value), Size(Consts.CASE_SIZE.value, Consts.CASE_SIZE.value)),
                                        false
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

                        override val pickedUp: List<StashedObject>
                            get() = objects.value.values.filter { it.pickedUp }
                    }.apply {
                        repeat(10) {
                            register(it.toString(), StashedObject("Item $it", pickedUp = true))
                        }
                    }
                }

                content(totalFrame.value, collisionDetector, stash)

                LaunchedEffect(Unit) {
                    var prevTime = 0L
                    var deltaCount = 0f

                    while (true) {
                        withFrameNanos { time ->
                            val deltaMs = (time - prevTime) / (1000f * 1000f)
                            deltaCount += deltaMs
                            prevTime = time

                            if (deltaCount >= Consts.FRAME_DURATION_MS) {
                                ++totalFrame.value
                                deltaCount = 0f
                            }
                        }
                    }
                }
            }
        }
    }
}