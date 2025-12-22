package SteamApi;
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object SteamApiCall {


    private const val BASE_URL = "https://api.steampowered.com/"

    // Cliente HTTP con timeouts
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(10,TimeUnit.MINUTES)
        .readTimeout(10,TimeUnit.MINUTES)
        .writeTimeout(10,TimeUnit.MINUTES)
        .build()

    // Instancia de Retrofit con carga perezosa
    val apiService : SteamApiInstance by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
            .create(SteamApiInstance::class.java)
    }

}