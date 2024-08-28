import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import nyc.charlton.remote.ui.RokuDeviceMain
import nyc.charlton.roku.*

@Composable
@Preview
fun App(device: Device) {
    MaterialTheme {
        when (device) {
            Device.None -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(onClick = {
                        discover()
                    }) {
                        Text("Discover")
                    }
                }
            }

            is Device.RokuDevice -> {
                val state by remember { Roku.appState }
                RokuDeviceMain(state)
            }
        }
    }
}

fun discover() {
    Roku.discover {
        if (it.isNotEmpty()) {
            Roku.setRokuDevices(it)
            if(it.size == 1) {
                Roku.setRokuDevice(it[0])
            }
        } else discover()
    }
}

fun main() = application {
    val state = rememberWindowState(size = DpSize(800.dp, 380.dp))
    //http://192.168.11.181:8060/query/device-info
    val device: Device by remember { Roku.deviceState }
    val devices: List<Device.RokuDevice> by remember { Roku.rokuDevices }
    Window(
        visible = true,
        onKeyEvent = {
            if (it.type != KeyEventType.KeyUp) {
                return@Window true
            }
            if (Roku.deviceState.value !is Device.None) {
                when {
                    it.isShiftPressed -> {
                        when (it.key) {
                            Key.DirectionUp -> Roku.volumeUp()
                            Key.DirectionDown -> Roku.volumeDown()
                        }
                    }

                    it.isMetaPressed -> {
                        //fried chicken, fries, and burger, lime soda, ice cream
                        when (it.key) {
                            Key.DirectionLeft -> Roku.left()
                            Key.DirectionRight -> Roku.right()
                            Key.DirectionUp -> Roku.up()
                            Key.DirectionDown -> Roku.down()
                            Key.Enter -> Roku.select()
                            Key.Escape -> Roku.home()
                            Key.Back, Key.Delete, Key.Backspace -> Roku.back()
                            Key.Apostrophe, Key.Grave -> Roku.info()
                            Key.One, Key.Two, Key.Three, Key.Four, Key.Five -> {
                                val number = (it.key.nativeKeyCode - 0x30)
                                when (number) {
                                    1 -> Roku.doCmd(Command.INPUT_HDMI1)
                                    2 -> Roku.doCmd(Command.INPUT_HDMI2)
                                    3 -> Roku.doCmd(Command.INPUT_HDMI3)
                                    4 -> Roku.doCmd(Command.INPUT_HDMI4)
                                    else -> Unit
                                }
                            }
                        }
                    }

                    else -> {

                    }
                }
            }
            true
        },
        onCloseRequest = ::exitApplication,
        resizable = false,
        title = "Roku",
        state = state
    ) {
        MenuBar {
            if (device is Device.None) {
                Menu("Device") {
                    Item("Discover") {
                        discover()
                    }
                    for (rokuDevice in devices) {
                        Item("Connect: ${rokuDevice.ip}") {
                            Roku.setRokuDevice(rokuDevice)
                        }
                    }
                }
            } else {
                Menu("Devices") {
                    for (rokuDevice in devices) {
                        Item("Connect: ${rokuDevice.ip}") {
                            Roku.setRokuDevice(rokuDevice)
                        }
                    }
                }
            }
        }
        App(device)
    }
}
