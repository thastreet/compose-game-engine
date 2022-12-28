import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

private const val SIZE_PX = 16
private val SIZE = SIZE_PX.dp

@Composable
fun Dialog(
    width: Int,
    height: Int,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit
) {
    Box(
        modifier
            .width(SIZE * width)
            .height(SIZE * height)
    ) {
        val bitmap = useResource("dialog.png") { loadImageBitmap(it) }

        val topLeft = painterAtPosition(bitmap, 0, 0)
        val topCenter = painterAtPosition(bitmap, 1, 0)
        val topRight = painterAtPosition(bitmap, 2, 0)
        val centerLeft = painterAtPosition(bitmap, 0, 1)
        val center = painterAtPosition(bitmap, 1, 1)
        val centerRight = painterAtPosition(bitmap, 2, 1)
        val bottomLeft = painterAtPosition(bitmap, 0, 2)
        val bottomCenter = painterAtPosition(bitmap, 1, 2)
        val bottomRight = painterAtPosition(bitmap, 2, 2)

        Tile(topLeft, 0, 0)
        Tile(topRight, width - 1, 0)
        Tile(bottomLeft, 0, height - 1)
        Tile(bottomRight, width - 1, height - 1)

        (1..width - 2).forEach { x ->
            Tile(topCenter, x, 0)
            Tile(bottomCenter, x, height - 1)
        }

        (1..height - 2).forEach { y ->
            Tile(centerLeft, 0, y)
            Tile(centerRight, width - 1, y)

            (1..width - 2).forEach { x ->
                Tile(center, x, y)
            }
        }

        content(Modifier.padding(SIZE))
    }
}

@Composable
private fun Tile(painter: Painter, x: Int, y: Int) {
    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .padding(start = SIZE * x, top = SIZE * y)
            .size(SIZE)
    )
}

private fun painterAtPosition(bitmap: ImageBitmap, x: Int, y: Int): BitmapPainter =
    BitmapPainter(
        bitmap,
        IntOffset(x * SIZE_PX, y * SIZE_PX),
        IntSize(SIZE_PX, SIZE_PX),
        FilterQuality.None
    )