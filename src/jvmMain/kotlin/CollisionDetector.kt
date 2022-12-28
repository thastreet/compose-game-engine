interface CollisionDetector {
    fun updatePosition(state: State)

    fun detectCollision(state: CharacterState): Boolean

    fun facing(state: CharacterState): CollisionInfo?

    fun remove(id: String)
}