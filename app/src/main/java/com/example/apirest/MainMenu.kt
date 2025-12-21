package com.example.apirest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainMenu : AppCompatActivity() {
    private lateinit var btnCloseSesion : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        val toolbarFragment = supportFragmentManager.findFragmentById(R.id.toolbar_fragment_container)
                as? ToolbarFragment

        toolbarFragment?.let {
            it.setToolbarTitle("Profile")
            it.setToolbarTitle(getString(R.string.nav_profile_title))
            it.setMenuButtonAction {
                Toast.makeText(this, "Menú de Perfil", Toast.LENGTH_SHORT).show()
            }

            val btnSalir = findViewById<Button>(R.id.btn_salir)
            btnSalir.setOnClickListener {
                salirDeAplicacion()
            }
        }

        crearPerfil()

        btnCloseSesion = findViewById<Button>(R.id.btn_cerrar_sesion)

        btnCloseSesion.setOnClickListener { cerrarSesion() }

        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        BottomNavigation(this).setup(bottomNavView, R.id.nav_profile)
    }


    private fun crearPerfil() {
        //Abrimos el archivo de preferencias
        val sharedPref = getSharedPreferences("MisDatosSteam", MODE_PRIVATE)

        val mySteamName = sharedPref.getString("steam_name", "Usuario Desconocido")
        val mySteamAvatarUrl = sharedPref.getString("steam_avatar", null) // Recuperamos la URL


        val txtNombreCompleto = findViewById<TextView>(R.id.text_name)

        val imgAvatar = findViewById<ImageView>(R.id.user_avatar_image)


        txtNombreCompleto.text = mySteamName


        if (mySteamAvatarUrl != null) {
            Glide.with(this)
                .load(mySteamAvatarUrl) // La URL que guardaste
                .placeholder(R.drawable.ic_launcher_foreground) // Imagen mientras carga
                .error(R.drawable.ic_launcher_foreground) // Imagen si falla
                .into(imgAvatar) // Donde ponerla
        }

        Toast.makeText(this, "Hola de nuevo, $mySteamName", Toast.LENGTH_SHORT).show()
    }

    private fun cerrarSesion() {
        val sharedPref = getSharedPreferences("MisDatosSteam", MODE_PRIVATE)
        val editor = sharedPref.edit()

        editor.clear()
        editor.apply()


        val intent = Intent(this, SteamLogin::class.java)
        startActivity(intent)
        finish()
    }

    private fun salirDeAplicacion() {
        finishAffinity()
    }

}