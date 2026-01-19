package CS2API

import com.google.gson.annotations.SerializedName

data class CS2Agent(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,


    @SerializedName("image") val image: String?,

    // Para saber si es Terrorist o Counter-Terrorist
    @SerializedName("team") val team: TeamInfo?
)

data class TeamInfo(
    @SerializedName("name") val name: String?
)
