package com.example.apirest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainMenu : AppCompatActivity() {
    private lateinit var btnCloseSesion : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        crearPerfil()

        btnCloseSesion = findViewById<Button>(R.id.btn_cerrar_sesion)

        btnCloseSesion.setOnClickListener { cerrarSesion() }

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigation.selectedItemId = R.id.nav_characters

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile -> {
                    startActivity(Intent(this, MainMenu::class.java))
                    true
                }
                R.id.nav_weapons -> {
                    startActivity(Intent(this, Weapons::class.java))
                    true
                }
                R.id.nav_characters -> {
                    startActivity(Intent(this, Character::class.java))
                    true
                }
                R.id.nav_chests -> {
                    startActivity(Intent(this, Chest::class.java))
                    true
                }
                else -> false
            }
        }
    }


    private fun crearPerfil()
    {
        // Abrimos el archivo de preferencias
        val sharedPref = getSharedPreferences("MisDatosSteam", MODE_PRIVATE)

        //Leemos los datos (el segundo parámetro es el valor por defecto si no encuentra nada)
        val mySteamId = sharedPref.getString("steam_id", null)
        val mySteamName = sharedPref.getString("steam_name", "Usuario Desconocido")

        val txtNombreCompleto = findViewById<TextView>(R.id.text_name)
        val txtInicial = findViewById<TextView>(R.id.user_initial)

        if (mySteamId != null) {
            // Poner el nombre
            txtNombreCompleto.text = mySteamName

            // B. Poner solo la primera letra (Logica segura)
            if (!mySteamName.isNullOrEmpty()) {
                // Cogemos la primera letra la pasamos a String
                val letra = mySteamName[0].toString().uppercase()
                txtInicial.text = letra
            } else {
                txtInicial.text = "?" // Por si acaso viene vacío
            }
            //Quitado por los nombres
            // toolbarFragment.setToolbarTitle(mySteamName)


            Toast.makeText(this, "Hola de nuevo, $mySteamName", Toast.LENGTH_SHORT).show()
        } else {
            // No hay nadie logueado, quizás deberías enviarlo al Login
        }
    }

    private fun cerrarSesion() {
        val sharedPref = getSharedPreferences("MisDatosSteam", MODE_PRIVATE)
        val editor = sharedPref.edit()

        editor.clear()
        editor.apply()

        // Redirigir al usuario a la pantalla de Login de nuevo
        val intent = Intent(this, SteamLogin::class.java)
        startActivity(intent)
        finish()
    }

}