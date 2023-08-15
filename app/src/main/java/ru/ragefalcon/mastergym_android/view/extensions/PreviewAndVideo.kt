package ru.ragefalcon.mastergym_android.view.extensions

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import bdElement.BaseAssignment
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import ru.ragefalcon.mastergym_android.R

class PreviewAndVideo {
}

@Composable
fun BaseAssignment.getPreview(modifier: Modifier){
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(data = "https://vz-d6d857fb-fb1.b-cdn.net/${this.bunny_guid}/thumbnail.jpg").apply(block = fun ImageRequest.Builder.() {
                placeholder(R.drawable.app_logo)
            }).build()
    )

    Image(
        painter = painter,
        contentDescription = "Assignment preview",
        modifier = modifier,
//        colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply {
//            setToSaturation(0f)
//        }),
        contentScale = ContentScale.Crop
    )

}