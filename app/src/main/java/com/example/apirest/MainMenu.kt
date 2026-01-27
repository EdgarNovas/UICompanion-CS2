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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.FirebaseDatabase

class MainMenu : AppCompatActivity() {
    private lateinit var btnCloseSesion: Button
    private lateinit var btnSalirApp: Button

    private lateinit var btnChat : Button
    private lateinit var btnEditar: ImageButton
    private lateinit var txtNombre: TextView
    private lateinit var imgAvatar: ImageView

    private val sides : Int =50
    private val top : Int =40
    private val bottom : Int =10


    private val database = FirebaseDatabase.getInstance("https://cscompanion-ba26b-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("usuarios")
    private var myUserId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        // Inicializar
        txtNombre = findViewById(R.id.text_name)
        imgAvatar = findViewById(R.id.user_avatar_image)
        btnEditar = findViewById(R.id.btn_editar_perfil)
        btnCloseSesion = findViewById(R.id.btn_cerrar_sesion)
        btnSalirApp = findViewById(R.id.btn_salir)
        btnChat = findViewById(R.id.btnChat)


        // Toolbar
        val toolbarFragment = supportFragmentManager.findFragmentById(R.id.toolbar_fragment_container) as? ToolbarFragment
        toolbarFragment?.let {
            it.setToolbarTitle(getString(R.string.nav_profile_title))
            it.setMenuButtonAction { salirDeAplicacion() }
        }

        // ID
        myUserId = obtenerMiIdentificador()

        //PERFIL
        cargarPerfilInteligente()

        // Listeners
        btnEditar.setOnClickListener { mostrarDialogoEditar() }
        btnCloseSesion.setOnClickListener { cerrarSesion() }
        btnSalirApp.setOnClickListener { salirDeAplicacion() }
        btnChat.setOnClickListener { mostrarChat()  }

        // Navegacion
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        BottomNavigation(this).setup(bottomNavView, R.id.nav_profile)
    }

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
            return "steam_$steamId"
        }
        return "error_no_id"
    }

    private fun cargarPerfilInteligente() {
        // Intentamos leer de la base de datos
        database.child(myUserId).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // EXISTE EN LA NUBE
                val nombre = snapshot.child("nombre").getValue(String::class.java)
                val avatar = snapshot.child("avatarUrl").getValue(String::class.java)

                actualizarUI(nombre, avatar)

            } else {
                // NO EXISTE
                crearUsuarioNuevoEnNube()
            }
        }.addOnFailureListener {
            // ERROR/OFFLINE
            mostrarDatosTemporalesOffline()
            Toast.makeText(this, "Modo Offline: No se pudo conectar a la nube", Toast.LENGTH_SHORT).show()
        }
    }

    // Esta función solo se llama si el usuario NO existe en la DB
    private fun crearUsuarioNuevoEnNube() {
        var nombreInicial = "Usuario"
        var avatarInicial = ""

        val userFirebase = FirebaseAuth.getInstance().currentUser
        if (userFirebase != null) {
            val email = userFirebase.email
            nombreInicial = email?.substringBefore("@") ?: "Usuario Nuevo"
        } else {
            val sharedPref = getSharedPreferences("MisDatosSteam", Context.MODE_PRIVATE)
            nombreInicial = sharedPref.getString("steam_name", "Usuario Steam") ?: "Usuario Steam"
            avatarInicial = sharedPref.getString("steam_avatar", "") ?: ""
        }

        // Guardamos en la nube
        guardarEnBaseDeDatos(nombreInicial, avatarInicial)
    }

    // Esta función muestra datos visuales sin tocar la DB (Protección contra borrado)
    private fun mostrarDatosTemporalesOffline() {
        var nombreInicial = "Cargando..."
        val userFirebase = FirebaseAuth.getInstance().currentUser

        if (userFirebase != null) {
            nombreInicial = userFirebase.email?.substringBefore("@") ?: "Usuario"
        } else {
            val sharedPref = getSharedPreferences("MisDatosSteam", Context.MODE_PRIVATE)
            nombreInicial = sharedPref.getString("steam_name", "Usuario Steam") ?: "Usuario Steam"
        }
        actualizarUI(nombreInicial, "")
    }

    private fun actualizarUI(nombre: String?, urlImagen: String?) {
        txtNombre.text = nombre ?: "Sin Nombre"

        if (!urlImagen.isNullOrEmpty()) {
            Glide.with(this)
                .load(urlImagen)
                .placeholder(R.drawable.ic_launcher_foreground) // Mientras carga
                .error(R.drawable.ic_launcher_foreground) // Si falla
                .circleCrop()
                .into(imgAvatar)
        } else {
            imgAvatar.setImageResource(R.drawable.ic_launcher_foreground)
        }
    }

    private fun mostrarChat()
    {
        startActivity(Intent(this, ChatActivity::class.java))
    }
    //Ayudado con IA
    private fun mostrarDialogoEditar() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Editar Perfil")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(sides, top, sides, bottom)

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

        database.child(myUserId).setValue(mapaDatos)
            .addOnSuccessListener {
                Toast.makeText(this, "Guardado correctamente", Toast.LENGTH_SHORT).show()
                actualizarUI(nombre, url)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar: ${it.message}", Toast.LENGTH_SHORT).show()
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