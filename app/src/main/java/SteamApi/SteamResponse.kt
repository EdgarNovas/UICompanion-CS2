package SteamApi

import com.google.gson.annotations.SerializedName

// Estructura de la respuesta general de Steam
data class SteamResponse(
    val response: SteamPlayerResponse
)

// Contenedor del array de jugadores (aunque solo pedimos uno)
data class SteamPlayerResponse(
    val players: List<SteamPlayerSummary>
)

// Los datos reales que queremos del jugador
data class SteamPlayerSummary(
    @SerializedName("steamid")
    val steamId: String,

    @SerializedName("personaname")
    val personaName: String, // El nombre que se muestra en Steam

    @SerializedName("avatarfull")
    val avatarFull: String,  // URL de la foto de perfil grande

    @SerializedName("profilestate")
    val profileState: Int,   // 1 si tiene configurado el perfil

    // Puedes añadir más campos como:
    // @SerializedName("communityvisibilitystate")
    // val communityVisibilityState: Int
)