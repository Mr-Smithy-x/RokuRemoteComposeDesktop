package nyc.charlton.roku

/**
 *  <app id="tvinput.hdmi2" type="tvin" version="1.0.0" ui-location="tvinput.hdmi2">PlayStation 5</app>
 */

sealed interface Application {
    val id: String
    val name: String
    val type: String
    val version: String

    data class Default(override val id: String, override val name: String, override val type: String, override val version: String): Application
    data class Active(override val id: String, override val name: String, override val type: String, override val version: String, val uiLocation: String): Application
}

val Application.relativeIconUrl get() = "/query/icon/$id"

sealed class AppState {
    data object None: AppState()
    data class Apps(val apps: List<Application>): AppState() {
        val inputs get() = apps.filter { it.type == "tvin" }
        val tvapps get() = apps.filter { it.type != "tvin" }
    }
    data class Active(val app: Application): AppState()
}