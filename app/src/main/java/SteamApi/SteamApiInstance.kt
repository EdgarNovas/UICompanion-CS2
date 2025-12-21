package SteamApi
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SteamApiInstance {


    // Endpoint para obtener información básica de un usuario
    @GET("ISteamUser/GetPlayerSummaries/v0002/")
    fun getPlayerSummary(
        @Query("key") apiKey: String,
        @Query("steamids") steamId: String
    ): Call<SteamResponse>


}