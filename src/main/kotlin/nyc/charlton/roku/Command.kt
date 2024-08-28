package nyc.charlton.roku

import java.util.*

enum class Command(val path: String) {
    BACK("/keypress/Back"),
    BACKSPACE("/keypress/Backspace"),
    CHANNEL_DOWN("/keypress/ChannelDown"),
    CHANNEL_UP("/keypress/ChannelUp"),
    DOWN("/keypress/Down"),
    ENTER("/keypress/Enter"),
    FIND_REMOTE("/keypress/FindRemote"),
    FORWARD("/keypress/Fwd"),
    HOME("/keypress/Home"),
    INFO("/keypress/Info"),
    INPUT_AV1("/keypress/InputAV1"),
    INPUT_HDMI1("/keypress/InputHDMI1"),
    INPUT_HDMI2("/keypress/InputHDMI2"),
    INPUT_HDMI3("/keypress/InputHDMI3"),
    INPUT_HDMI4("/keypress/InputHDMI4"),
    INPUT_TUNER("/keypress/InputTuner"),
    LEFT("/keypress/Left"),
    LITERAL("/keypress/Lit_%s"),
    PLAY("/keypress/Play"),
    POWER("/keypress/Power"),
    POWEROFF("/keypress/PowerOff"),
    POWERON("/keypress/PowerOn"),
    REPLAY("/keypress/InstantReplay"),
    REVERSE("/keypress/Rev"),
    RIGHT("/keypress/Right"),
    SEARCH("/search/browse"),
    SELECT("/keypress/Select"),
    UP("/keypress/Up"),
    VOLUME_DOWN("/keypress/VolumeDown"),
    VOLUME_MUTE("/keypress/VolumeMute"),
    VOLUME_UP("/keypress/VolumeUp"),
    KEY_UP(""),
    KEY_DOWN("");

    override fun toString(): String {
        return when (this) {
            REVERSE -> "Rev"
            FORWARD -> "Fwd"
            REPLAY -> "InstantReplay"
            LITERAL -> "Lit"
            POWERON -> "PowerOn"
            POWEROFF -> "PowerOff"
            INPUT_HDMI1, INPUT_HDMI2, INPUT_HDMI3, INPUT_HDMI4, INPUT_AV1 -> {
                val split = this.name.split("_").toMutableList()
                split[0] = split[0].lowercase().capitalize()
                return split.joinToString("")
            }

            else -> {
                val split = this.name.lowercase().split("_")
                val joinToString =
                    split.map { it -> it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } }
                joinToString.joinToString("")
            }
        }
    }
}

fun Command.build(baseUrl: String, vararg args: String): String {
    return baseUrl + when (this) {
        Command.SEARCH -> ""
        Command.LITERAL -> this.path.format(args[0])
        Command.KEY_UP -> ""
        Command.KEY_DOWN -> ""
        else -> this.path
    }
}