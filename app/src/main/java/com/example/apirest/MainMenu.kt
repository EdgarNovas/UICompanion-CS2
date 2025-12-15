package com.example.apirest

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainMenu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        // 1. Recibir los datos del Intent que mandó SteamLogin
        val steamName = intent.getStringExtra("STEAM_NAME") ?: "Usuario"
        val steamId = intent.getStringExtra("STEAM_ID") ?: "ID no encontrado"
        val steamAvatarUrl = intent.getStringExtra("STEAM_AVATAR")

        // 2. Vincular con los elementos de la vista (activity_main_menu.xml)
        val textName = findViewById<TextView>(R.id.text_name)
        val textEmail = findViewById<TextView>(R.id.text_email) // Usaremos esto para el ID
        val textInitial = findViewById<TextView>(R.id.user_initial) // La letra grande

        // 3. Poner los textos
        textName.text = steamName
        textEmail.text = "ID: $steamId" // Steam no da email público, mostramos el ID

        // Poner la inicial del nombre en el círculo
        if (steamName.isNotEmpty()) {
            textInitial.text = steamName.first().uppercase()
        }

        // 4. (Opcional) Cargar la imagen real si quieres reemplazar la inicial
        // Si quieres ver la foto real, necesitas una librería como Glide o Picasso.
        // Si no la tienes instalada, puedes saltarte este paso.
        /*
        val avatarView = findViewById<ImageView>(R.id.tu_image_view_si_existiera)
        Glide.with(this).load(steamAvatarUrl).into(avatarView)
        */
    }
}