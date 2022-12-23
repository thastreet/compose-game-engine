import androidx.compose.runtime.MutableState

object Stash {
    private val objects = mutableMapOf<String, MutableState<Boolean>>()

    fun register(state: ObjectState, pickedUp: MutableState<Boolean>) {
        objects[state.id] = pickedUp
    }

    fun pickup(id: String) {
        objects.getValue(id).value = true
    }
}