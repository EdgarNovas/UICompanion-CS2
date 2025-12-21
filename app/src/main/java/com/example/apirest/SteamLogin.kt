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

    private val STEAM_API_KEY = "1C85CB737EE670AD0DFDE181DBACAC46"
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //IA PARA HACER AYUDARME CON EL LOGIN DE STEAM


        val webView = WebView(this)
        setContentView(webView)

        // Configuración básica
        webView.settings.javaScriptEnabled = true


        val realm = "https://steamcommunity.com"
        val url = "https://steamcommunity.com/openid/login?" +
                "openid.ns=http://specs.openid.net/auth/2.0&" +
                "openid.mode=checkid_setup&" +
                "openid.return_to=$realm/signin&" + // A dónde vuelve tras el login
                "openid.realm=$realm&" +
                "openid.identity=http://specs.openid.net/auth/2.0/identifier_select&" +
                "openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select"


        webView.loadUrl(url)


        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {

                val returnPath = "https://steamcommunity.com/signin"

                if (url != null && url.startsWith(returnPath)) {

                    val steamId = extractSteamId(url)

                    if (steamId != null) {
                        Toast.makeText(applicationContext, "ID: $steamId", Toast.LENGTH_LONG).show()


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
        //Ayuda de IA
        val claimedId = uri.getQueryParameter("openid.claimed_id")

        return claimedId?.substringAfterLast("/")
    }

    private fun obtenerDatosDelUsuarioConRetrofit(steamId: String) {

        val call = SteamApiCall.apiService.getPlayerSummary(
            apiKey = this.STEAM_API_KEY,
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
        val intent = Intent(this, MainMenu::class.java)

        startActivity(intent)
        finish()
    }
}
