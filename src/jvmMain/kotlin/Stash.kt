interface Stash {
    fun register(id: String, stashedObject: StashedObject)

    fun pickup(id: String)

    fun isPickedUp(id: String): Boolean

    val pickedUp: List<StashedObject>
}