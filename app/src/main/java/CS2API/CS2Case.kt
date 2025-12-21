package CS2API

import com.google.gson.annotations.SerializedName

data class CS2Case(
    @SerializedName("id")
    val id: String?, // Ponemos ? por si viene nulo

    @SerializedName("name")
    val name: String?,

    @SerializedName("image")
    val image: String?,

    @SerializedName("description")
    val description: String?
)
