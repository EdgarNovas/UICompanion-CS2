package com.example.apirest

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Toast
import com.example.apirest.ToolbarFragment

class Chest: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chest)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbarFragment = supportFragmentManager.findFragmentById(R.id.toolbar_fragment_container)
                as? ToolbarFragment

        toolbarFragment?.let {
            it.setToolbarTitle("Cofres")
            it.setToolbarTitle(getString(R.string.nav_chests_title))

            it.setMenuButtonAction {
                Toast.makeText(this, "Abrir Menú de Cofres", Toast.LENGTH_SHORT).show()
            }
        }
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.selectedItemId = R.id.nav_chests

    }
}