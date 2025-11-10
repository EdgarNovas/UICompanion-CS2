package MarvelApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object MarvelApiCall {

    private const val BASE_URL = "https://gateway.marvel.com"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(10,TimeUnit.MINUTES)
        .readTimeout(10,TimeUnit.MINUTES)
        .writeTimeout(10,TimeUnit.MINUTES)
        .build()

    val apiService : MarvelApiInstance by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
            .create(MarvelApiInstance::class.java)
    }

}