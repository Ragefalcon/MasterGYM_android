package ru.ragefalcon.mastergym_android.view.elements

import android.text.Html
import android.widget.TextView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun TextHtml(str: String = "<p><b>This</b> is a <a href=\"https://example.com\">link</a> in HTML format.</p>", modifier: Modifier = Modifier){

    val context = LocalContext.current

    Box(modifier = modifier) {
        AndroidView(
            modifier =
            Modifier.clickable {
//                shouldShowControls = shouldShowControls.not()
            }.clipToBounds(),
            factory = {
                TextView(context).apply {
                    text = Html.fromHtml(str, Html.FROM_HTML_MODE_COMPACT)
                    /*
                                        layoutParams =
                                            FrameLayout.LayoutParams(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.MATCH_PARENT
                                            )
                    */
                }
            }
        )
    }
}