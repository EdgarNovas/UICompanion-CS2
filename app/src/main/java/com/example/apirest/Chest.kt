package com.example.apirest

import CasesAPI.CS2ApiInstance
import CasesAPI.CS2Case
import CasesAPI.CaseAdapter
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apirest.ToolbarFragment
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback

class Chest: AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CaseAdapter

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

        // 2. Inicializar RecyclerView con 2 columnas (como tu GridLayout anterior)
        recyclerView = findViewById(R.id.cases_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // 3. Llamar a la API
        obtenerDatosDeCofres()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.selectedItemId = R.id.nav_chests

    }

    private fun obtenerDatosDeCofres() {
        // Mostramos un Toast para saber que intentó cargar
        // Toast.makeText(this, "Cargando cofres...", Toast.LENGTH_SHORT).show()

        CS2ApiInstance.api.getCrates().enqueue(object : retrofit2.Callback<List<CS2Case>> {
            override fun onResponse(call: Call<List<CS2Case>>, response: Response<List<CS2Case>>) {
                if (response.isSuccessful) {
                    val todosLosItems = response.body() ?: emptyList()

                    Log.d("API_CS2", "Recibidos ${todosLosItems.size} items totales")

                    // Filtramos solo los que tienen "Case" en el nombre y tienen imagen
                    val casesFiltradas = todosLosItems.filter {
                        (it.name?.contains("Case", ignoreCase = true) == true) &&
                                !it.image.isNullOrEmpty()
                    }

                    Log.d("API_CS2", "Filtrados ${casesFiltradas.size} cofres")

                    // Asignamos al adaptador
                    adapter = CaseAdapter(casesFiltradas)
                    recyclerView.adapter = adapter

                } else {
                    Log.e("API_CS2", "Error en respuesta: ${response.code()}")
                    Toast.makeText(applicationContext, "Error API: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<CS2Case>>, t: Throwable) {
                Log.e("API_CS2", "Fallo total: ${t.message}")
                Toast.makeText(applicationContext, "Fallo de red: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}