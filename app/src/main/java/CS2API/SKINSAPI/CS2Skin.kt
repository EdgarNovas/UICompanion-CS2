package CS2API.SKINSAPI

import com.google.gson.annotations.SerializedName

data class CS2Skin(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("image") val image: String?,
    @SerializedName("category") val category: Category? // Objeto anidado
)

data class Category(
    @SerializedName("name") val name: String? // Ej: "Pistols", "Rifles", "Heavy", "SMGs"
)
