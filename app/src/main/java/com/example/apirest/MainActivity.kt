package com.example.apirest

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.content.res.Configuration
import android.view.MotionEvent
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics


class MainActivity : AppCompatActivity() {
    private lateinit var analytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        analytics = FirebaseAnalytics.getInstance(this)
        //Esto es el inicio de los logs de errores
        FirebaseCrashlytics.getInstance().setCustomKey("AppStarted",true);

        val bundle = Bundle().apply {
            putString("portrait_orientation",(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT).toString())
        }

        val auth = FirebaseAuth.getInstance()

        val firebaseUser = auth.currentUser

        //Miro si hay algo en el movil de steam
        val sharedPref = getSharedPreferences("MisDatosSteam", Context.MODE_PRIVATE)
        val steamId = sharedPref.getString("steam_id", null)

        // UNO O EL OTRO
        if (firebaseUser != null || steamId != null) {
            // Si cualquiera de los dos existe, vamos al menú
            redirigirUsuario()
        } else {
            // Si no hay nadie, vamos al Login para que elija
            // Un pequeño delay para que se vea el Splash
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }, 2000)
        }


        analytics.logEvent("StartEvent", bundle)
    }

    //Ayudado con IA
    private fun redirigirUsuario() {
        val intent = Intent(this, MainMenu::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }



}