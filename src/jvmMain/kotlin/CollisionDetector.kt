import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.DpOffset

object CollisionDetector {
    private val states = mutableMapOf<String, DpOffset>()

    fun updatePosition(state: CharacterState) {
        states[state.uuid] = DpOffset(state.x, state.y)
    }

    fun detectCollision(state: CharacterState, direction: Direction): Boolean =
        states.entries.any {
            state.uuid != it.key && state.getProjectedRect(direction).overlaps(
                Rect(
                    Offset(it.value.x.value, it.value.y.value),
                    Size(Consts.CHARACTER_SIZE.value, Consts.CHARACTER_SIZE.value)
                )
            )
        }
}