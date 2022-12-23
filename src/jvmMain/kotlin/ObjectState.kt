import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import java.util.UUID

data class ObjectState(
    override val x: Dp,
    override val y: Dp,
    val canBePickedUp: Boolean,
    override val id: String = UUID.randomUUID().toString()
) : State {
    override val size = Size(Consts.CHARACTER_SIZE.value, Consts.CHARACTER_SIZE.value)
}
