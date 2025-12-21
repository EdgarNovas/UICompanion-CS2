package com.example.apirest

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.crashlytics.FirebaseCrashlytics

class SkinDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        tvName.text = name
        tvCategory.text = category
        tvDesc.text = description

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
    }

}