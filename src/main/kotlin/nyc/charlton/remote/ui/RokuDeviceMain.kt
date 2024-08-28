package nyc.charlton.remote.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nyc.charlton.roku.AppState
import nyc.charlton.roku.Roku

@Composable
@Preview
fun RokuDeviceMain(state: AppState) {
    Column(modifier = Modifier.padding(16.dp, 8.dp)) {
        Row(
            modifier = Modifier.padding(16.dp, 8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                Roku.volumeUp()
            }) {
                Text("Volume Up")
            }
            Spacer(Modifier.padding(8.dp))
            Button(onClick = {
                Roku.volumeDown()
            }) {
                Text("Volume Down")
            }
            Spacer(Modifier.padding(8.dp))
            Button(onClick = {
                Roku.volumeMute()
            }) {
                Text("Volume Mute")
            }
            Spacer(Modifier.padding(8.dp))
            Button(onClick = {
                Roku.powerOn()
            }) {
                Text("Power On")
            }
            Spacer(Modifier.padding(8.dp))
            Button(onClick = {
                Roku.powerOff()
            }) {
                Text("Power Off")
            }
        }
        when (state) {
            is AppState.Apps -> {
                if (state.apps.isNotEmpty()) {
                    Text("Roku Apps")
                    Spacer(modifier = Modifier.padding(4.dp))
                    if (state.inputs.isNotEmpty()) {
                        LazyRow {
                            items(state.inputs.filter { it.type == "tvin" }) {
                                AppCard(it, 120)
                            }
                        }
                    }
                    if (state.tvapps.isNotEmpty()) {
                        LazyRow {
                            items(state.tvapps.filter { it.type != "tvin" }) {
                                AppCard(it)
                            }
                        }
                    }
                } else {
                    Row {
                        Text("No Apps")
                    }
                }
            }

            else -> {
                Row {
                    Text("No Apps")
                }
            }
        }
    }
}