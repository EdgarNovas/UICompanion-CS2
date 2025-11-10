package MarvelApi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.sql.Timestamp

interface MarvelApiInstance {

    @GET("v1/public/characters")
    fun getCharacters(
        @Query("apikey") apiKey: String,
        @Query("ts") timestamp: String,
        @Query("hash") hash : String,
        @Query("limit") limit : Int = 20
    ): Call<MarvelResponse>
}