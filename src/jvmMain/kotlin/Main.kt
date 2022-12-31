@file:OptIn(ExperimentalComposeUiApi::class)

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

enum class Animation {
    IDLE,
    WALKING
}


val fontFamily = FontFamily(
    Font(
        "pokemon.ttf",
        weight = FontWeight.W400,
        style = FontStyle.Normal
    )
)

val textStyle = TextStyle(
    fontSize = 10.sp,
    lineHeight = 16.sp,
    fontFamily = fontFamily
)

fun main() = application {
    val pressedKeys = remember { mutableSetOf<Key>() }
    var lastUpKey: Key? by remember { mutableStateOf(null) }

    Window(
        onCloseRequest = ::exitApplication,
        resizable = false,
        onPreviewKeyEvent = {
            when {
                (it.type == KeyEventType.KeyDown) -> {
                    pressedKeys.add(it.key)
                    true
                }

                (it.type == KeyEventType.KeyUp) -> {
                    pressedKeys.remove(it.key)
                    lastUpKey = it.key
                    true
                }

                else -> false
            }
        },
        state = WindowState(size = DpSize(Consts.CASE_SIZE * 16, Consts.CASE_SIZE * 16))
    ) {
        Engine { totalFrame, collisionDetector, stash ->
            Map(collisionDetector)

            NPC(Consts.CASE_SIZE * 5, Consts.CASE_SIZE * 5, totalFrame, collisionDetector)

            Object("Pokeball", Consts.CASE_SIZE * 3, Consts.CASE_SIZE * 3, collisionDetector, stash)

            var dialogItems: List<String>? by remember { mutableStateOf(null) }

            Hero(totalFrame, pressedKeys, collisionDetector, stash, dialogItems == null)

            if (lastUpKey == Key.Escape) {
                if (dialogItems != null) {
                    dialogItems = null
                } else {
                    dialogItems = stash.pickedUp.map { it.name }
                }
                lastUpKey = null
            }

            Dialog(dialogItems, pressedKeys, totalFrame, Modifier.align(Alignment.TopEnd)) {
                if (it == 0) {
                    dialogItems = listOf("Yes", "Yessir", "Oui")
                } else {
                    dialogItems = null
                }
            }
        }
    }
}

@Composable
fun Dialog(dialogItems: List<String>?, pressedKeys: Set<Key>, totalFrame: Int, modifier: Modifier = Modifier, onItemSelected: (Int) -> Unit = {}) {
    dialogItems.let { items ->
        if (!items.isNullOrEmpty()) {
            Dialog(
                width = 10,
                height = 10,
                pressedKeys = pressedKeys,
                totalFrame = totalFrame,
                items = items,
                onItemSelected = onItemSelected,
                modifier = modifier
            )
        }
    }
}
