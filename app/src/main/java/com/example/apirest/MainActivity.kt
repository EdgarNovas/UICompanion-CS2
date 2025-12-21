package com.example.apirest

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.content.res.Configuration
import android.view.MotionEvent
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    private lateinit var analytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        analytics = FirebaseAnalytics.getInstance(this)

        val bundle = Bundle().apply {
            putString("portrait_orientation",(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT).toString())
        }

        val auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            // Comprobamos si tenemos los datos de Steam guardados
            redirigirUsuario()
            return
        }


        analytics.logEvent("MyFirstEvent", bundle)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event?.action == MotionEvent.ACTION_DOWN){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }
        return super.onTouchEvent(event)

    }

    private fun redirigirUsuario() {
        val sharedPref = getSharedPreferences("MisDatosSteam", Context.MODE_PRIVATE)
        val steamId = sharedPref.getString("steam_id", null)
        val steamName = sharedPref.getString("steam_name", "Usuario")
        val steamAvatar = sharedPref.getString("steam_avatar", null)

        if (steamId != null) {
            // Tiene Firebase Y tiene datos de Steam -> Directo al Menú
            val intent = Intent(this, MainMenu::class.java).apply {
                putExtra("STEAM_ID", steamId)
                putExtra("STEAM_NAME", steamName)
                putExtra("STEAM_AVATAR", steamAvatar)
                // Esto borra el historial para que no pueda volver atrás al Splash ///IA
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        } else {
            // Tiene Firebase pero NO tiene datos de Steam -> A Login de Steam
            val intent = Intent(this, SteamLogin::class.java)
            startActivity(intent)
            finish()
        }
    }



}