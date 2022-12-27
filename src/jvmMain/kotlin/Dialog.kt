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

@Composable
fun Dialog(
    width: Int,
    height: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .width(16.dp * width)
            .height(16.dp * height)
    ) {
        val bitmap = useResource("dialog.png") { loadImageBitmap(it) }

        val topLeft = slice(bitmap, 0, 0)
        val topCenter = slice(bitmap, 1, 0)
        val topRight = slice(bitmap, 2, 0)
        val centerLeft = slice(bitmap, 0, 1)
        val center = slice(bitmap, 1, 1)
        val centerRight = slice(bitmap, 2, 1)
        val bottomLeft = slice(bitmap, 0, 2)
        val bottomCenter = slice(bitmap, 1, 2)
        val bottomRight = slice(bitmap, 2, 2)

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
    }
}

@Composable
private fun Tile(painter: Painter, x: Int, y: Int) {
    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .padding(start = 16.dp * x, top = 16.dp * y)
            .size(16.dp)
    )
}

private fun slice(bitmap: ImageBitmap, x: Int, y: Int): BitmapPainter =
    BitmapPainter(
        bitmap,
        IntOffset(x * 16, y * 16),
        IntSize(16, 16),
        FilterQuality.None
    )