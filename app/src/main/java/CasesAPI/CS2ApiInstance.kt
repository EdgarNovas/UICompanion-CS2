package CasesAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
object CS2ApiInstance {
    private const val BASE_URL = "https://raw.githubusercontent.com/"

    val api: CS2ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CS2ApiService::class.java)
    }
}