import kotlinx.serialization.Serializable

@Serializable
data class SavedMap(
    val points: Map<IndexPoint, IndexPoint>,
    val collisions: Set<IndexPoint>
)
