package CS2API

import com.google.gson.annotations.SerializedName

data class CS2Agent(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,

    // Los agentes suelen tener una imagen de cuerpo completo
    @SerializedName("image") val image: String?,

    // Campo extra útil: Para saber si es Terrorist o Counter-Terrorist
    @SerializedName("team") val team: TeamInfo?
)

data class TeamInfo(
    @SerializedName("name") val name: String?
)
