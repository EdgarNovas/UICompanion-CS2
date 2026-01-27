package com.example.apirest

import CS2API.FavoriteItem
import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.FirebaseAnalytics.Param

class SkinDetailActivity : AppCompatActivity() {

    // Favoritos
    private lateinit var btnFavorite: FloatingActionButton
    private lateinit var analytics: FirebaseAnalytics
    private var isFavorite = false
    private val database = FirebaseDatabase.getInstance("https://cscompanion-ba26b-default-rtdb.europe-west1.firebasedatabase.app/").getReference("usuarios")
    private var myUserId: String = ""


    //Funciones

    private var skinName: String = ""
    private var skinImage: String = ""
    private var skinCategory: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analytics = FirebaseAnalytics.getInstance(this)
        enableEdgeToEdge()
        setContentView(R.layout.activity_skin_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        //Botón atrás
        val toolbarFragment = supportFragmentManager.findFragmentById(R.id.toolbar_fragment_container) as? ToolbarFragment
        toolbarFragment?.let {
            it.setToolbarTitle("Detalle")
            it.setMenuButtonAction { finish() } // Vuelve atrás
        }

        // Ayuda IA para pasarlo bien sin errores
        // Recibir datos del Intent
        val name = intent.getStringExtra("EXTRA_NAME") ?: "Sin Nombre"
        val image = intent.getStringExtra("EXTRA_IMAGE")
        val category = intent.getStringExtra("EXTRA_CATEGORY") ?: "General"
        val description = intent.getStringExtra("EXTRA_DESC") ?: "No hay descripción disponible para este objeto."
        val price = intent.getStringExtra("EXTRA_PRICE")


        if (name == null || image == null) {
            // Esto no debería pasar
            val error = Exception("SkinDetailActivity recibió datos NULOS. Name: $name")
            FirebaseCrashlytics.getInstance().recordException(error)
            //Salir si pasa eso
            finish()
            return
        }

        // Vincular vistas y poner datos
        val imgDetail = findViewById<ImageView>(R.id.detail_image)
        val tvName = findViewById<TextView>(R.id.detail_name)
        val tvCategory = findViewById<TextView>(R.id.detail_category)
        val tvDesc = findViewById<TextView>(R.id.detail_description)
        val tvPrice = findViewById<TextView>(R.id.detail_price)
        btnFavorite = findViewById(R.id.btn_favorite)

        tvName.text = name
        tvCategory.text = category
        tvDesc.text = description

        skinName = name
        skinImage = image
        skinCategory = category


        if (!price.isNullOrEmpty()) {
            tvPrice.text = price
        } else {
            tvPrice.text = "No disponible"
        }

        // Cargar imagen con Glide
        Glide.with(this)
            .load(image)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(imgDetail)

        // ID del usuario
        myUserId = obtenerMiIdentificador()

        // Comprobar si ya es favorito
        checkIfFavorite()


        btnFavorite.setOnClickListener {
            toggleFavorite()
        }

    }


    private fun obtenerMiIdentificador(): String {
        // Firebase
        val userFirebase = FirebaseAuth.getInstance().currentUser
        if (userFirebase != null) {
            return userFirebase.uid
        }
        // 2. Intentamos Steam
        val sharedPref = getSharedPreferences("MisDatosSteam", Context.MODE_PRIVATE)
        return sharedPref.getString("steam_id", "anonimo") ?: "anonimo"
    }

    private fun checkIfFavorite() {
        val idSeguro = sanitizeKey(skinName)
        // Ruta: usuarios -> ID -> favoritos -> NOMBRE_SKIN
        database.child(myUserId).child("favoritos").child(idSeguro).get()
            .addOnSuccessListener { snapshot ->
                isFavorite = snapshot.exists()
                actualizarIcono()
            }
    }

    private fun toggleFavorite() {
        if (myUserId.isEmpty()) {
            Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show()
            return
        }

        val idSeguro = sanitizeKey(skinName)
        val ref = database.child(myUserId).child("favoritos").child(idSeguro)

        // Deshabilitamos el botón un momento para evitar doble click rápido
        btnFavorite.isEnabled = false

        if (isFavorite) {
            // ELIMINAR
            ref.removeValue().addOnSuccessListener {
                isFavorite = false
                actualizarIcono()
                Toast.makeText(this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                btnFavorite.isEnabled = true
            }.addOnFailureListener { btnFavorite.isEnabled = true }
        } else {
            // Creamos el objeto a guardar
            val item = FavoriteItem(idSeguro, skinName, skinImage, skinCategory)

            ref.setValue(item).addOnSuccessListener {
                isFavorite = true
                actualizarIcono()
                Toast.makeText(this, "Añadido a favoritos", Toast.LENGTH_SHORT).show()
                btnFavorite.isEnabled = true

                val bundle = Bundle()

                bundle.putString(Param.ITEM_ID, idSeguro)
                bundle.putString(Param.ITEM_NAME, skinName)
                bundle.putString(Param.ITEM_CATEGORY, skinCategory)

                analytics.logEvent("favorite_in_game", bundle)

            }.addOnFailureListener {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
                btnFavorite.isEnabled = true
            }
        }
    }

    private fun actualizarIcono() {
        if (isFavorite) {
            // Usa icono relleno
            btnFavorite.setImageResource(R.drawable.ic_star_filled)
        } else {
            // Usa icono de borde
            btnFavorite.setImageResource(R.drawable.ic_star_border)
        }
    }

    //ayudado conIA
    // Firebase no permite guardar claves con symbols como ".", "#", "$", "[", "]"
    // Las skins de CS2 tienen "|" (ej: AK-47 | Redline), así que lo limpiamos
    private fun sanitizeKey(original: String): String {
        return original.replace(".", "")
            .replace("#", "")
            .replace("$", "")
            .replace("[", "")
            .replace("]", "")
            .replace("/", "")
            .replace("|", "") // Quitamos la barra vertical típica de skins
            .trim()
    }
}

