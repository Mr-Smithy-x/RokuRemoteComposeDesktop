package nyc.charlton.roku

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import io.ktor.client.statement.*
import kotlinx.coroutines.*
import nyc.charlton.roku.utils.Discover
import nyc.charlton.roku.utils.Request.get
import nyc.charlton.roku.utils.Request.post
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xml.sax.SAXException
import java.io.IOException
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import kotlin.coroutines.CoroutineContext


private operator fun Element.get(key: String, index: Int = 0): Node? {
    return this.getElementsByTagName(key).item(index)
}

object Roku : CoroutineScope {


    private val _rokuDevices: MutableState<List<Device.RokuDevice>> = mutableStateOf(listOf())
    private val _deviceState: MutableState<Device> = mutableStateOf(Device.None)
    private val _infoState: MutableState<Info> = mutableStateOf(Info.None)
    private val _appState: MutableState<AppState> = mutableStateOf(AppState.None)
    private val _activeAppState: MutableState<AppState> = mutableStateOf(AppState.None)


    val rokuDevices: State<List<Device.RokuDevice>> get() = _rokuDevices
    val deviceState: State<Device> get() = _deviceState
    val infoState: State<Info> get() = _infoState
    val appState: State<AppState> get() = _appState
    val activeAppState: State<AppState> get() = _activeAppState

    val url get() = deviceState.value.url ?: throw NullPointerException("Device not set")

    fun setRokuDevices(rokuDevices: List<Device.RokuDevice>) {
        this._rokuDevices.value = rokuDevices
    }

    fun setRokuDevice(rokuDevice: Device.RokuDevice): Job {
        _deviceState.value = rokuDevice
        return getDeviceData()
    }

    private fun getDeviceData() = launch {
        val url = this@Roku.url
        val deviceInfoResponse = get("$url/query/device-info")
        val activeAppResponse = get("$url/query/active-app")
        val appsResponse = get("$url/query/apps")
        //val tvChannelResponse = get("$url/query/tv-channels")

        val apps = parseAppString(appsResponse.bodyAsText())
        _appState.value = AppState.Apps(apps)

        val activeApp = parseActiveApp(activeAppResponse.bodyAsText())
        _activeAppState.value = activeApp?.let { AppState.Active(it) } ?: AppState.None
        //val tvChannel = parseActiveTvChannels(activeAppResponse.bodyAsText())


        val deviceInfo = parseDeviceInfo(deviceInfoResponse.bodyAsText())
        if (deviceInfo != null) {
            _infoState.value = deviceInfo
        }
    }

    private fun parseDeviceInfo(bodyAsText: String): Info.DeviceInfo? {
        val builderFactory =
            DocumentBuilderFactory.newInstance()
        var builder: DocumentBuilder? = null
        try {
            builder = builderFactory.newDocumentBuilder()
            try {
                val document: Document = builder.parse(
                    bodyAsText.byteInputStream()
                )
                if (document.childNodes.length < 1) return null
                val element: Element = document.documentElement //.childNodes.item(1)

                return Info.DeviceInfo(
                    element["udn"]?.textContent,
                    element["serial-number"]?.textContent,
                    element["device-id"]?.textContent,
                    element["advertising-id"]?.textContent,
                    element["vendor-name"]?.textContent,
                    element["model-name"]?.textContent,
                    element["model-number"]?.textContent,
                    element["model-region"]?.textContent,
                    element["is-tv"]?.textContent.toBoolean(),
                    element["is-stick"]?.textContent.toBoolean(),
                    element["screen-size"]?.textContent?.toInt()?:0,
                    element["panel-id"]?.textContent?.toInt()?:0,
                    element["mobile-has-live-tv"]?.textContent.toBoolean(),
                    element["ui-resolution"]?.textContent,
                    element["tuner-type"]?.textContent,
                    element["supports-ethernet"]?.textContent.toBoolean(),
                    element["wifi-mac"]?.textContent,
                    element["wifi-driver"]?.textContent,
                    element["has-wifi-5G-support"]?.textContent.toBoolean(),
                    element["ethernet-mac"]?.textContent,
                    element["network-type"]?.textContent,
                    element["network-name"]?.textContent,
                    element["friendly-device-name"]?.textContent,
                    element["friendly-model-name"]?.textContent,
                    element["default-device-name"]?.textContent,
                    element["user-device-name"]?.textContent,
                    element["user-device-location"]?.textContent,
                    element["build-number"]?.textContent,
                    element["software-version"]?.textContent,
                    element["software-build"]?.textContent?.toInt()?:0,
                    element["lightning-base-build-number"]?.textContent,
                    element["ui-build-number"]?.textContent,
                    element["ui-software-version"]?.textContent,
                    element["ui-software-build"]?.textContent?.toInt()?:0,
                    element["secure-device"]?.textContent.toBoolean(),
                    element["language"]?.textContent,
                    element["country"]?.textContent,
                    element["locale"]?.textContent,
                    element["time-zone-auto"]?.textContent.toBoolean(),
                    element["time-zone"]?.textContent,
                    element["time-zone-name"]?.textContent,
                    element["time-zone-tz"]?.textContent,
                    element["time-zone-offset"]?.textContent?.toInt()?:0,
                    element["clock-format"]?.textContent,
                    element["uptime"]?.textContent?.toInt()?:0,
                    element["power-mode"]?.textContent,
                    element["supports-suspend"]?.textContent.toBoolean(),
                    element["supports-find-remote"]?.textContent.toBoolean(),
                    element["supports-audio-guide"]?.textContent.toBoolean(),
                    element["supports-rva"]?.textContent.toBoolean(),
                    element["has-hands-free-voice-remote"]?.textContent.toBoolean(),
                    element["developer-enabled"]?.textContent.toBoolean(),
                    element["search-enabled"]?.textContent.toBoolean(),
                    element["search-channels-enabled"]?.textContent.toBoolean(),
                    element["voice-search-enabled"]?.textContent.toBoolean(),
                    element["supports-private-listening"]?.textContent.toBoolean(),
                    element["supports-private-listening-dtv"]?.textContent.toBoolean(),
                    element["supports-warm-standby"]?.textContent.toBoolean(),
                    element["headphones-connected"]?.textContent.toBoolean(),
                    element["supports-audio-settings"]?.textContent.toBoolean(),
                    element["expert-pq-enabled"]?.textContent,
                    element["supports-ecs-textedit"]?.textContent.toBoolean(),
                    element["supports-ecs-microphone"]?.textContent.toBoolean(),
                    element["supports-wake-on-wlan"]?.textContent.toBoolean(),
                    element["supports-airplay"]?.textContent.toBoolean(),
                    element["has-play-on-roku"]?.textContent.toBoolean(),
                    element["has-mobile-screensaver"]?.textContent.toBoolean(),
                    element["support-url"]?.textContent,
                    element["grandcentral-version"]?.textContent,
                    element["supports-trc"]?.textContent,
                    element["trc-version"]?.textContent,
                    element["trc-channel-version"]?.textContent,
                    element["av-sync-calibration-enabled"]?.textContent
                )
                //item.attributes.getNamedItem("id").textContent,
                //item.attributes.getNamedItem("type").textContent,
                //item.attributes.getNamedItem("version").textContent,
                //item.attributes.getNamedItem("ui-location").textContent,

            } catch (e: SAXException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } catch (e: ParserConfigurationException) {
            e.printStackTrace()
        }
        return null
    }

    private fun parseActiveApp(bodyAsText: String): Application.Active? {
        val builderFactory =
            DocumentBuilderFactory.newInstance()
        var builder: DocumentBuilder? = null
        try {
            builder = builderFactory.newDocumentBuilder()
            try {
                val document: Document = builder.parse(
                    bodyAsText.byteInputStream()
                )
                if (document.childNodes.length < 1) return null
                val item = document.documentElement.childNodes.item(1)
                return Application.Active(
                    item.attributes.getNamedItem("id").textContent,
                    item.textContent,
                    item.attributes.getNamedItem("type").textContent,
                    item.attributes.getNamedItem("version").textContent,
                    item.attributes.getNamedItem("ui-location").textContent,
                )
            } catch (e: SAXException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } catch (e: ParserConfigurationException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * <apps>
     *  <app id="tvinput.hdmi1" type="tvin" version="1.0.0">HDMI 1 (ARC)</app>
     * </apps>
     */
    private fun parseAppString(bodyAsText: String): List<Application> {
        val builderFactory =
            DocumentBuilderFactory.newInstance()
        var builder: DocumentBuilder? = null
        try {
            builder = builderFactory.newDocumentBuilder()
            try {
                val document: Document = builder.parse(
                    bodyAsText.byteInputStream()
                )
                val childNodes = document.documentElement.childNodes
                val apps = arrayListOf<Application>()
                for (i in 0..<childNodes.length) {
                    val item = childNodes.item(i)
                    if (item.nodeName == "app") {
                        val app = Application.Default(
                            item.attributes.getNamedItem("id").textContent,
                            item.textContent,
                            item.attributes.getNamedItem("type").textContent,
                            item.attributes.getNamedItem("version").textContent
                        )
                        apps.add(app)
                    }
                }
                return apps.sortedByDescending { it.type }
            } catch (e: SAXException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } catch (e: ParserConfigurationException) {
            e.printStackTrace()
        }
        return emptyList()
    }


    fun volumeUp(points: Int = 1) = launch {
        repeat(points) {
            post(Command.VOLUME_UP.build(url))
        }
    }

    fun volumeDown(points: Int = 1) = launch {
        repeat(points) {
            post(Command.VOLUME_DOWN.build(url))
        }
    }

    fun volumeMute() = launch {
        post(Command.VOLUME_MUTE.build(url))
    }

    fun left() = launch {
        post(Command.LEFT.build(url))
    }

    fun right() = launch {
        post(Command.RIGHT.build(url))
    }

    fun up() = launch {
        post(Command.UP.build(url))
    }

    fun down() = launch {
        post(Command.DOWN.build(url))
    }

    fun select() = launch {
        post(Command.SELECT.build(url))
    }

    fun discover(func: (devices: List<Device.RokuDevice>) -> Unit) = launch {
        val devices = Discover.discover()
        withContext(Dispatchers.Default) {
            func(devices)
        }
    }

    fun launchApp(application: Application) = launch {
        post("$url/launch/${application.id}", mapOf("contentId" to application.id))
    }

    fun powerOn() = launch {
        post(Command.POWERON.build(url))
    }

    fun powerOff() = launch {
        post(Command.POWEROFF.build(url))
    }

    fun home()= launch {
        post(Command.HOME.build(url))
    }

    fun back() = launch {
        post(Command.BACK.build(url))
    }

    fun info() = launch {
        post(Command.INFO.build(url))
    }

    fun doCmd(cmd: Command) = launch {
        post(cmd.build(url))
    }



    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

}
