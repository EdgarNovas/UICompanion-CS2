package SteamApi

import com.google.gson.annotations.SerializedName

// Estructura de la respuesta general de Steam
data class SteamResponse(
    val response: SteamPlayerResponse
)


data class SteamPlayerResponse(
    val players: List<SteamPlayerSummary>
)

// Los datos reales del jugador
data class SteamPlayerSummary(
    @SerializedName("steamid")
    val steamId: String,

    @SerializedName("personaname")
    val personaName: String,

    @SerializedName("avatarfull")
    val avatarFull: String,

    @SerializedName("profilestate")
    val profileState: Int,

)