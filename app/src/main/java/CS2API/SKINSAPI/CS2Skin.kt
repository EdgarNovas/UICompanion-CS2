package CS2API.SKINSAPI

import com.google.gson.annotations.SerializedName

data class CS2Skin(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("image") val image: String?,
    @SerializedName("category") val category: Category?,
    @SerializedName("description") val description: String?,
    @SerializedName("price") val price: String?
)

data class Category(
    @SerializedName("name") val name: String? // Ej: "Pistols", "Rifles", "Heavy", "SMGs"
)
