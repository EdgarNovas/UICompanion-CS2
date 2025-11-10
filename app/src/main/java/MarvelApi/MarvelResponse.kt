package MarvelApi

data class MarvelResponse(
    val code : Int,
    val status: String,
    val data: MarvelData
)

data class MarvelData(
    val results: List<MarvelCharacter>
)

data class MarvelCharacter(
    val id: Int,
    val name: String,
    val description:String,
    val thumbnail: Thumbnail
)

data class Thumbnail(
    val path : String,
    val extension: String
)

