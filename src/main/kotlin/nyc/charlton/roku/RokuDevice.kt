package nyc.charlton.roku

sealed class Device {
    data object None: Device()
    data class RokuDevice(val ip: String, val port: Int) : Device()
}

val Device.url get(): String? {
    return when(this) {
        Device.None -> null
        is Device.RokuDevice -> "http://$ip:$port"
    }
}