import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp

sealed interface State {
    val x: Dp
    val y: Dp
    val size: Size
    val id: String
}