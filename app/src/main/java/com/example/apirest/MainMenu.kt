package com.example.apirest

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.FirebaseDatabase

class MainMenu : AppCompatActivity() {
    private lateinit var btnCloseSesion: Button
    private lateinit var btnSalirApp: Button
    private lateinit var btnEditar: ImageButton
    private lateinit var txtNombre: TextView
    private lateinit var imgAvatar: ImageView

    private val database = FirebaseDatabase.getInstance().getReference("usuarios")
    private var myUserId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        //Inicializar Vistas
        txtNombre = findViewById(R.id.text_name)
        imgAvatar = findViewById(R.id.user_avatar_image)
        btnEditar = findViewById(R.id.btn_editar_perfil)
        btnCloseSesion = findViewById(R.id.btn_cerrar_sesion)
        btnSalirApp = findViewById(R.id.btn_salir)

        // 2. Toolbar
        val toolbarFragment = supportFragmentManager.findFragmentById(R.id.toolbar_fragment_container) as? ToolbarFragment
        toolbarFragment?.let {
            it.setToolbarTitle(getString(R.string.nav_profile_title))
            // El botón del toolbar también sale de la app
            it.setMenuButtonAction { salirDeAplicacion() }
        }

        // CALCULAR ID ÚNICO
        myUserId = obtenerMiIdentificador()

        // CARGAR PERFIL
        cargarPerfilInteligente()


        btnEditar.setOnClickListener {
            mostrarDialogoEditar()
        }

        btnCloseSesion.setOnClickListener {
            cerrarSesion()
        }

        btnSalirApp.setOnClickListener {
            salirDeAplicacion()
        }

        // Navegación
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        BottomNavigation(this).setup(bottomNavView, R.id.nav_profile)
    }


    //
    private fun obtenerMiIdentificador(): String {
        //  Firebase
        val userFirebase = FirebaseAuth.getInstance().currentUser
        if (userFirebase != null) {
            return userFirebase.uid
        }

        // Steam
        val sharedPref = getSharedPreferences("MisDatosSteam", Context.MODE_PRIVATE)
        val steamId = sharedPref.getString("steam_id", null)
        if (steamId != null) {
            return steamId
        }

        //Error
        return "error_no_id"
    }


    private fun cargarPerfilInteligente() {
        //Preguntar a Realtime Database si hay datos personalizados
        database.child(myUserId).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // ¡HAY DATOS PERSONALIZADOS!
                val nombreGuardado = snapshot.child("nombre").value.toString()
                val avatarGuardado = snapshot.child("avatarUrl").value.toString()

                actualizarUI(nombreGuardado, avatarGuardado)
                FirebaseCrashlytics.getInstance().log("Perfil cargado desde Database: $nombreGuardado")
            } else {
                // NO HAY DATOS EN DB -> Cargar los originales (Steam/Google)
                cargarDatosOriginales()
            }
        }.addOnFailureListener {
            // Si falla internet, intentamos cargar los originales
            cargarDatosOriginales()
        }
    }

    private fun cargarDatosOriginales() {
        val userFirebase = FirebaseAuth.getInstance().currentUser
        if (userFirebase != null) {
            val nombre = userFirebase.displayName ?: "Usuario Firebase"
            val foto = userFirebase.photoUrl.toString()
            actualizarUI(nombre, foto)
        } else {
            val sharedPref = getSharedPreferences("MisDatosSteam", Context.MODE_PRIVATE)
            val steamName = sharedPref.getString("steam_name", "Usuario Steam")
            val steamAvatar = sharedPref.getString("steam_avatar", "")
            actualizarUI(steamName, steamAvatar)
        }
    }

    private fun actualizarUI(nombre: String?, urlImagen: String?) {
        txtNombre.text = nombre ?: "Sin Nombre"

        if (!urlImagen.isNullOrEmpty()) {
            Glide.with(this)
                .load(urlImagen)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .circleCrop()
                .into(imgAvatar)
        }
    }

    //IA
    private fun mostrarDialogoEditar() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Editar Perfil")

        // Crear layout del diálogo
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)

        val inputNombre = EditText(this)
        inputNombre.hint = "Nuevo Nombre"
        inputNombre.setText(txtNombre.text)
        layout.addView(inputNombre)

        val inputUrl = EditText(this)
        inputUrl.hint = "URL de Imagen (pegar link)"
        layout.addView(inputUrl)

        builder.setView(layout)

        builder.setPositiveButton("Guardar") { _, _ ->
            val nuevoNombre = inputNombre.text.toString()
            val nuevaUrl = inputUrl.text.toString()

            if (nuevoNombre.isNotEmpty()) {
                guardarEnBaseDeDatos(nuevoNombre, nuevaUrl)
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun guardarEnBaseDeDatos(nombre: String, url: String) {
        val mapaDatos = mapOf(
            "nombre" to nombre,
            "avatarUrl" to url
        )

        // Guardamos en: usuarios -> [TU_ID] -> {nombre, avatarUrl}
        database.child(myUserId).setValue(mapaDatos)
            .addOnSuccessListener {
                Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                // Recargamos la UI directamente
                actualizarUI(nombre, url)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar: ${it.message}", Toast.LENGTH_SHORT).show()
                FirebaseCrashlytics.getInstance().recordException(it)
            }
    }

    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        getSharedPreferences("MisDatosSteam", Context.MODE_PRIVATE).edit().clear().apply()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun salirDeAplicacion() {
        finishAffinity()
    }

}