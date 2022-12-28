import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import java.util.UUID

data class ObjectState(
    override val x: Dp,
    override val y: Dp,
    val name: String
) : State {
    override val id = UUID.randomUUID().toString()
    override val size = Size(Consts.CHARACTER_SIZE.value, Consts.CHARACTER_SIZE.value)
}
