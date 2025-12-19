package CS2API

import retrofit2.Call
import retrofit2.http.GET

interface CS2ApiService {
    @GET("ByMykel/CSGO-API/main/public/api/en/crates.json")
    fun getCrates(): Call<List<CS2Case>>

    @GET("ByMykel/CSGO-API/main/public/api/en/agents.json")
    fun getAgents(): Call<List<CS2Agent>>

}

