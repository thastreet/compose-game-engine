
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size

object CollisionDetector {
    private val states = mutableMapOf<String, Rect>()

    fun updatePosition(state: CharacterState) {
        states[state.uuid] = Rect(Offset(state.x.value, state.y.value), Size(Consts.CHARACTER_SIZE.value, Consts.CHARACTER_SIZE.value))
    }

    fun detectCollision(state: CharacterState, direction: Direction): Boolean =
        states.entries.any {
            state.uuid != it.key && state.getProjectedRect(direction).overlaps(it.value)
        }
}