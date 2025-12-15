package SteamApi
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SteamApiInstance {

    // Endpoint para obtener información básica de un usuario
    // ISteamUser/GetPlayerSummaries/v0002/
    @GET("ISteamUser/GetPlayerSummaries/v0002/")
    fun getPlayerSummary(
        @Query("key") apiKey: String,      // Tu API Key de Steam
        @Query("steamids") steamId: String // El ID 64 del usuario
    ): Call<SteamResponse>

    // Puedes añadir más endpoints aquí, por ejemplo:
    // @GET("IPlayerService/GetOwnedGames/v0001/")
    // fun getOwnedGames( ... )

}