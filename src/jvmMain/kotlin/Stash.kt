interface Stash {
    fun register(id: String, stashedObject: StashedObject)

    fun pickup(id: String)

    fun isPickedUp(id: String): Boolean

    /*
    private val objects = mutableMapOf<String, MutableState<Boolean>>()

    fun register(state: ObjectState, pickedUp: MutableState<Boolean>) {
        objects[state.id] = pickedUp
    }

    fun pickup(id: String) {
        objects.getValue(id).value = true
    }*/
}