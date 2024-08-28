package nyc.charlton.roku.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.jetbrains.skia.Image
import java.net.URL
import javax.imageio.ImageIO

object Request {

    private fun client() = HttpClient(OkHttp) {
        engine {
            config {
                followRedirects(true)
            }
        }
        expectSuccess = true
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.BODY
            filter { request ->
                request.url.host.contains("192.168")
            }
            sanitizeHeader { header ->
                header == HttpHeaders.Authorization
            }
        }
    }

    fun loadImage(url: String): ImageBitmap {
        return try {
            ImageIO.read(URL(url)).toComposeImageBitmap()
        }catch (e: Exception){
            ImageBitmap(120.dp.value.toInt(),120.dp.value.toInt())
        }
    }

    suspend fun load(url: String): ImageBitmap {
        val image = client().use { client ->
            client.get(url).readBytes()
        }
        return Image.makeFromEncoded(image).toComposeImageBitmap()
    }

    suspend fun get(
        url: String,
        query: Map<String, Any?> = mapOf(),
        headers: Map<String, Any?> = mapOf()
    ): HttpResponse {
        val client = client()
        val get = client.get(url) {
            query.forEach {
                this.parameter(it.key, it.value)
            }
            headers.forEach {
                headers {
                    append(it.key, it.value.toString())
                }
            }
        }
        client.close()
        return get
    }

    suspend fun post(
        url: String,
        body: Map<String, Any?> = mapOf(),
        headers: Map<String, Any?> = mapOf()
    ): HttpResponse {
        val client = client()
        val post = client.post(url) {
            //setBody(body)
            headers.forEach {
                headers {
                    append(it.key, it.value.toString())
                }
            }
        }
        client.close()
        return post
    }

}