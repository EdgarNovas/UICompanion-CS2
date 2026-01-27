package com.example.apirest

import CS2API.CS2ApiInstance
import CS2API.CS2Case
import CS2API.CaseAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import retrofit2.Call
import retrofit2.Response
import android.view.View
import android.widget.ProgressBar

class Chest: AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CaseAdapter

    private lateinit var searchView: SearchView
    private val numColumns : Int = 2
    private var listaCompleta: List<CS2Case> = emptyList()

    private lateinit var progressBar: ProgressBar

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
            //it.setToolbarTitle("Chests")
            it.setToolbarTitle(getString(R.string.nav_chests_title))
            it.setMenuButtonAction {
                Toast.makeText(this, "Abrir Menú de Cofres", Toast.LENGTH_SHORT).show()
            }
        }

        // RecyclerView con 2 columnas
        recyclerView = findViewById(R.id.cases_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, numColumns)
        searchView = findViewById(R.id.search_view_cases)

        progressBar = findViewById(R.id.loading_spinner)
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        // Configurar Buscador
        setupBuscador()

        // Llamar a la API
        obtenerDatosDeCofres()

        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        BottomNavigation(this).setup(bottomNavView, R.id.nav_chests)
    }


    private fun setupBuscador() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarLista(newText)
                return true
            }
        })
    }

    private fun filtrarLista(texto: String?) {
        // Si no ha cargado la API, salimos
        if (listaCompleta.isEmpty()) return

        // Si el adaptador no está listo, salimos (seguridad)
        if (!::adapter.isInitialized) return

        if (texto.isNullOrEmpty()) {
            // Texto borrado -> Mostramos todo
            adapter.actualizarLista(listaCompleta)
        } else {
            // Texto escrito -> Filtramos la lista completa
            val listaFiltrada = listaCompleta.filter { item ->
                item.name?.contains(texto, ignoreCase = true) == true
            }
            adapter.actualizarLista(listaFiltrada)
        }
    }

    private fun obtenerDatosDeCofres() {
        CS2ApiInstance.api.getCrates().enqueue(object : retrofit2.Callback<List<CS2Case>> {
            override fun onResponse(call: Call<List<CS2Case>>, response: Response<List<CS2Case>>) {
                if (response.isSuccessful) {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    val todosLosItems = response.body() ?: emptyList()

                    Log.d("API_CS2", "Recibidos ${todosLosItems.size} items totales")


                    listaCompleta = todosLosItems.filter {
                        (it.name?.contains("Case", ignoreCase = true) == true) &&
                                !it.image.isNullOrEmpty()
                    }

                    Log.d("API_CS2", "Filtrados ${listaCompleta.size} cofres")

                    // Inicializamos el adaptador con la lista global
                    adapter = CaseAdapter(listaCompleta)
                    recyclerView.adapter = adapter

                } else {
                    FirebaseCrashlytics.getInstance().log( "Error en respuesta: ${response.code()}")
                    Toast.makeText(applicationContext, "Error API: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<CS2Case>>, t: Throwable) {
                FirebaseCrashlytics.getInstance().recordException(t)
                Toast.makeText(applicationContext, "Fallo de red: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}