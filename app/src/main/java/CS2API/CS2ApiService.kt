package CS2API

import CS2API.SKINSAPI.CS2Skin


import retrofit2.Call
import retrofit2.http.GET

interface CS2ApiService {
    @GET("ByMykel/CSGO-API/main/public/api/en/crates.json")
    fun getCrates(): Call<List<CS2Case>>

    @GET("ByMykel/CSGO-API/main/public/api/en/agents.json")
    fun getAgents(): Call<List<CS2Agent>>

    @GET("ByMykel/CSGO-API/main/public/api/en/skins.json")
    fun getSkins(): Call<List<CS2Skin>>

}

