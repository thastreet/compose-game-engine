
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import java.util.UUID

data class ObjectState(
    val x: Dp = 0.dp,
    val y: Dp = 0.dp,
    val uuid: String = UUID.randomUUID().toString()
)

@Composable
fun Object(
    x: Dp,
    y: Dp
) {
    val bitmap = useResource("objects.png") { loadImageBitmap(it) }

    val painter = BitmapPainter(
        bitmap,
        IntOffset(0, 0),
        IntSize(16, 16),
        FilterQuality.None
    )

    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .offset(x, y)
            .size(Consts.CHARACTER_SIZE)
    )
}