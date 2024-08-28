package nyc.charlton.remote.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nyc.charlton.roku.Application
import nyc.charlton.roku.Roku
import nyc.charlton.roku.relativeIconUrl
import nyc.charlton.roku.utils.Request

@Composable
@Preview
fun AppCard(
    application: Application = Application.Default("0", "Netflix", "App", "1.0"),
    widthInt: Int = 160
) {
    val width = widthInt.dp
    val height = (widthInt * .75).dp
    Card(
        modifier = Modifier.padding(8.dp).widthIn(min = width, max = width).heightIn(min = height, max = height)
            .clickable {
                Roku.launchApp(application)
            }) {
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            if (application.type == "tvin") {
                Text(application.name)
                Spacer(modifier = Modifier.padding(4.dp))
                Text(application.version)
            } else {
                val image by remember { mutableStateOf(Request.loadImage(Roku.url + application.relativeIconUrl)) }
                Image(
                    bitmap = image,
                    application.name,
                    modifier = Modifier.widthIn(min = width, max = width).heightIn(min = height, max = height)
                )
            }

        }
    }
}