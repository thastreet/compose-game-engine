import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

class CollisionDetector {
    data class CollisionInfo(
        val id: String,
        val rect: Rect,
        val canBePickedUp: Boolean
    )

    private val states = mutableMapOf<String, CollisionInfo>()

    fun updatePosition(state: State) {
        states[state.id] = CollisionInfo(
            state.id,
            Rect(Offset(state.x.value, state.y.value), state.size),
            state is ObjectState && state.canBePickedUp
        )
    }

    fun detectCollision(state: CharacterState): Boolean = facing(state) != null

    fun facing(state: CharacterState): CollisionInfo? =
        states.entries.firstOrNull { state.id != it.key && state.getProjectedRect(state.direction).overlaps(it.value.rect) }?.value

    fun remove(id: String) {
        states.remove(id)
    }
}