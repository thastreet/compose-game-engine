import kotlinx.serialization.Serializable

@Serializable
data class IndexPoint(
    val x: Int = 0,
    val y: Int = 0
)