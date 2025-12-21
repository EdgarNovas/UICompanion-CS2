package com.example.apirest

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Toast
import com.example.apirest.R
import android.content.Intent

import CS2API.AgentAdapter
import CS2API.CS2Agent
import CS2API.CS2ApiInstance
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import retrofit2.Call
import retrofit2.Response

class Character: AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AgentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_character)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbarFragment = supportFragmentManager.findFragmentById(R.id.toolbar_fragment_container)
                as? ToolbarFragment

        toolbarFragment?.let {
            it.setToolbarTitle("Characters")
            it.setToolbarTitle(getString(R.string.nav_characters_title))

            it.setMenuButtonAction {
                Toast.makeText(this, "Abrir Menú de Personajes", Toast.LENGTH_SHORT).show()
            }
        }

        recyclerView = findViewById(R.id.agents_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // 2 columnas

        obtenerAgentes()

        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        BottomNavigation(this).setup(bottomNavView, R.id.nav_characters)
    }


    private fun obtenerAgentes() {
        CS2ApiInstance.api.getAgents().enqueue(object : retrofit2.Callback<List<CS2Agent>> {
            override fun onResponse(call: Call<List<CS2Agent>>, response: Response<List<CS2Agent>>) {
                if (response.isSuccessful) {
                    val agentsList = response.body() ?: emptyList()

                    //Filtrar para quitar los que no tengan imagen
                    val validAgents = agentsList.filter { !it.image.isNullOrEmpty() }

                    adapter = AgentAdapter(validAgents)
                    recyclerView.adapter = adapter
                } else {
                    FirebaseCrashlytics.getInstance().log( "Error en respuesta: ${response.code()}")
                    Toast.makeText(this@Character, "Error API: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CS2Agent>>, t: Throwable) {
                FirebaseCrashlytics.getInstance().recordException(t)
                Toast.makeText(this@Character, "Error Red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}