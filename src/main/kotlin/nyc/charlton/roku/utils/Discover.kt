package nyc.charlton.roku.utils

import nyc.charlton.roku.Device
import java.net.*

object Discover {

    const val ST_ECP = "roku:ecp"
    const val ST_DIAL = "urn:dial-multiscreen-org:service:dial:1"
    const val ST_CONSOLE = "device-discovery-protocol-version:00030010"

    fun discover (timeout: Int = 2, retries: Int = 1, st: String = ST_ECP): List<Device.RokuDevice> {
        val group = Pair("239.255.255.250", 1900)
        val devices = arrayListOf<Device.RokuDevice>()
        val message = arrayOf(
            "M-SEARCH * HTTP/1.1",
            "HOST: ${group.first}:${group.second}",
            "MAN: \"ssdp:discover\"",
            "ST: $st",
            "MX: 3",
            "",
            "",
        ).joinToString("\r\n")

        repeat(retries) {
            val socket = DatagramSocket()
            /**
             *
             */
            socket.setOption(StandardSocketOptions.SO_REUSEADDR, true)
            socket.setOption(StandardSocketOptions.IP_MULTICAST_TTL, 2)
            val m = message.encodeToByteArray()
            val datagramPacket = DatagramPacket(m, m.size, InetSocketAddress(group.first, group.second))
            socket.send(datagramPacket)

            while (true){
                try {
                    socket.soTimeout = 1000 * timeout
                    val buffer = ByteArray(1024) // Adjust buffer size based on expected data
                    // Create a DatagramPacket to receive data
                    val p = DatagramPacket(buffer, buffer.size)
                    // Receive a datagram packet from the socket (blocking call)
                    socket.receive(p)
                    // Process the received data
                    val data = p.data.copyOfRange(0, p.length) // Extract data up to received length
                    val receivedString = String(data) // Convert to String if applicable
                    val senderAddress = p.address // Get sender's IP address
                    val senderPort = p.port // Get sender's port number
                    //println("""$receivedString""")
                    val size = receivedString.split("\r\n")
                    val params = size.subList(1, size.size).map {
                        if (it.contains(":")) {
                            val split = it.split(":", limit = 2)
                            try {
                                Pair(split[0].trim(), split[1].trim())
                            } catch (e: Exception) {
                                println(it)
                                println("...")
                                Pair("","")
                            }
                        } else {
                            Pair("", "")
                        }
                    }.filter { it.first.isNotBlank() }.toMap()
                    val d = params["LOCATION"]?.parseIpAndPort() ?: continue
                    devices.add(Device.RokuDevice(d.first, d.second))
                }catch (e: Exception) {
                    //e.printStackTrace()
                    break
                }
            }
        }
        return devices.distinct()
    }

}

fun String.parseIpAndPort(): Pair<String, Int>? {
    var addressWithoutProtocol = if (startsWith("http://", true)) {
        substring(7) // Remove "http://" if present (case-insensitive)
    } else if (startsWith("https://", true)) {
        substring(8) // Remove "https://" if present (case-insensitive)
    } else {
        this
    }
    if(addressWithoutProtocol.endsWith("/")) {
        addressWithoutProtocol = addressWithoutProtocol.substring(0 until addressWithoutProtocol.length-1)
    }
    val parts = addressWithoutProtocol.split(":")
    println(parts)
    if (parts.size != 2) return null

    val ipAddress: String
    try {
        ipAddress = InetAddress.getByName(parts[0]).hostAddress ?: return null
    } catch (e: UnknownHostException) {
        return null
    }

    val port = try {
        parts[1].toInt()
    } catch (e: NumberFormatException) {
        return null
    }

    return Pair(ipAddress, port)
}
