package com.example.apirest

import SteamApi.SteamApiCall
import SteamApi.SteamResponse
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SteamLogin : AppCompatActivity() {
    // ⚠️ PEGA AQUÍ TU API KEY DE STEAM
    private val STEAM_API_KEY = "1C85CB737EE670AD0DFDE181DBACAC46"
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_steam_login)
        // Creamos el WebView programáticamente (o úsalo desde tu layout XML)
        val webView = WebView(this)
        setContentView(webView)

        // Configuración básica
        webView.settings.javaScriptEnabled = true

        // 1. Construimos la URL de llamada a Steam (OpenID)
        val realm = "https://steamcommunity.com" // Tu dominio (puede ser ficticio para pruebas)
        val url = "https://steamcommunity.com/openid/login?" +
                "openid.ns=http://specs.openid.net/auth/2.0&" +
                "openid.mode=checkid_setup&" +
                "openid.return_to=$realm/signin&" + // A dónde vuelve tras el login
                "openid.realm=$realm&" +
                "openid.identity=http://specs.openid.net/auth/2.0/identifier_select&" +
                "openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select"

        // 2. Cargamos la URL
        webView.loadUrl(url)


        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {

                val returnPath = "https://steamcommunity.com/signin"

                if (url != null && url.startsWith(returnPath)) {

                    val steamId = extractSteamId(url)

                    if (steamId != null) {
                        Toast.makeText(applicationContext, "ID: $steamId", Toast.LENGTH_LONG).show()

                        // ⭐️ LLAMADA CORRECTA A LA API ⭐️
                        obtenerDatosDelUsuarioConRetrofit(steamId)
                    }

                    // Devolver TRUE detiene la carga en el WebView
                    return true
                }

                // Para las demás URLs, carga normalmente
                return false
            }
        }
    }


    // Función para limpiar la URL y sacar solo el número ID
    private fun extractSteamId(url: String): String? {
        val uri = Uri.parse(url)
        val claimedId = uri.getQueryParameter("openid.claimed_id")

        // El formato suele ser: https://steamcommunity.com/openid/id/76561198XXXXXXXXX
        // Necesitamos solo los números del final
        return claimedId?.substringAfterLast("/")
    }

    private fun obtenerDatosDelUsuarioConRetrofit(steamId: String) {

        // ⚠️ Usa tu propia API Key de Steam aquí
        val MY_STEAM_KEY = "1C85CB737EE670AD0DFDE181DBACAC46"


        val call = SteamApiCall.apiService.getPlayerSummary(
            apiKey = MY_STEAM_KEY,
            steamId = steamId
        )


        call.enqueue(object : Callback<SteamResponse> {

            override fun onResponse(call: Call<SteamResponse>, response: Response<SteamResponse>) {
                if (response.isSuccessful) {

                    val player = response.body()?.response?.players?.firstOrNull()

                    if (player != null) {
                        val nombre = player.personaName
                        val avatarUrl = player.avatarFull

                        Toast.makeText(applicationContext, "Bienvenido: $nombre", Toast.LENGTH_LONG).show()

                        // Llama a la función que cambia de Activity
                        irAHome(steamId, nombre, avatarUrl)
                    } else {
                        Toast.makeText(applicationContext, "Error: Datos de Steam no encontrados.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "Error HTTP: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }


            override fun onFailure(call: Call<SteamResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Error de red: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
    private fun irAHome(id: String, nombre: String, avatar: String) {

        //Crear/Abrir el archivo de preferencias donde guardare cosas de steam

        val sharedPref = getSharedPreferences("MisDatosSteam", MODE_PRIVATE)

        //Escribir datos
        val editor = sharedPref.edit()
        editor.putString("steam_id", id)      // Guardo ID
        editor.putString("steam_name", nombre) // Guardo Nombre
        editor.putString("steam_avatar", avatar) // Guardo Avatar

        //Guardar cambios
        editor.apply()

        //Ir al menú principal
        val intent = Intent(this, Chest::class.java)

        startActivity(intent)
        finish()
    }
}
