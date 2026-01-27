package com.example.apirest

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import CS2API.CS2ApiInstance
import CS2API.SKINSAPI.CS2Skin
import CS2API.SKINSAPI.SkinAdapter
import android.widget.ProgressBar
import android.view.View
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import retrofit2.Call
import retrofit2.Response

class SkinListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryToFilter: String

    private lateinit var adapter: SkinAdapter
    private lateinit var searchView: SearchView

    private var listaCompleta: List<CS2Skin> = emptyList()
    //ProgressBar ayudado con IA
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_skin_list)



        //Inicializar vistas
        recyclerView = findViewById(R.id.skins_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        searchView = findViewById(R.id.search_view_skins)


        progressBar = findViewById(R.id.loading_spinner)
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        //  Recibir categoría
        categoryToFilter = intent.getStringExtra("CATEGORY_NAME") ?: ""

        val toolbarFragment = supportFragmentManager.findFragmentById(R.id.toolbar_fragment_container) as? ToolbarFragment
        toolbarFragment?.let {
            it.setToolbarTitle(categoryToFilter)
            it.setMenuButtonAction { finish() }
        }

        // Configurar buscador
        setupBuscador()

        // Llenar la lista
        obtenerSkinsFiltradas()
    }

    private fun setupBuscador() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false // Buscamos al escribir
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarLista(newText)
                return true
            }
        })
    }

    private fun filtrarLista(texto: String?) {
        // Si no hemos descargado nada aún, salimos
        if (listaCompleta.isEmpty()) return

        if (texto.isNullOrEmpty()) {
            // Si borra el texto, mostramos la LISTA COMPLETA
            adapter.actualizarLista(listaCompleta)
        } else {
            // Si escribe, filtramos sobre la LISTA COMPLETA
            val listaFiltrada = listaCompleta.filter { skin ->
                skin.name?.contains(texto, ignoreCase = true) == true
            }
            adapter.actualizarLista(listaFiltrada)
        }
    }

    private fun obtenerSkinsFiltradas() {
        CS2ApiInstance.api.getSkins().enqueue(object : retrofit2.Callback<List<CS2Skin>> {
            override fun onResponse(call: Call<List<CS2Skin>>, response: Response<List<CS2Skin>>) {
                if (response.isSuccessful) {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    val allSkins = response.body() ?: emptyList()

                    // Guardamos las skins
                    listaCompleta = allSkins.filter { skin ->
                        skin.category?.name?.contains(categoryToFilter, ignoreCase = true) == true
                    }

                    // Usamos el skin adapter para mostrar las skins
                    adapter = SkinAdapter(listaCompleta)

                    // Asignamos ese adaptador al Recycler
                    recyclerView.adapter = adapter

                    if (listaCompleta.isEmpty()) {
                        Toast.makeText(applicationContext, "No se encontraron skins de tipo $categoryToFilter", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<List<CS2Skin>>, t: Throwable) {

                FirebaseCrashlytics.getInstance().recordException(t)
                Toast.makeText(applicationContext, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}