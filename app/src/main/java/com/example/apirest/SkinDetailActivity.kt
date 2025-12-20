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

        // 1. Recibir datos del Intent
        val name = intent.getStringExtra("EXTRA_NAME") ?: "Sin Nombre"
        val image = intent.getStringExtra("EXTRA_IMAGE")
        val category = intent.getStringExtra("EXTRA_CATEGORY") ?: "General"
        val description = intent.getStringExtra("EXTRA_DESC") ?: "No hay descripción disponible para este objeto."
        // Nota: La API de CS2 a veces no da precio directo, puedes pasar un string vacío si no hay.
        val price = intent.getStringExtra("EXTRA_PRICE")

        // 2. Configurar Toolbar (Botón atrás)
        val toolbarFragment = supportFragmentManager.findFragmentById(R.id.toolbar_fragment_container) as? ToolbarFragment
        toolbarFragment?.let {
            it.setToolbarTitle("Detalle")
            it.setMenuButtonAction { finish() } // Vuelve atrás
        }

        // 3. Vincular vistas y poner datos
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


        // 4. Configurar Bottom Navigation (Solo visual)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_weapons // O la que corresponda
        // Aquí no ponemos listener de navegación para no complicar, o puedes copiar el de las otras actividades
    }

}