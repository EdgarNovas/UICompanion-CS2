package com.example.apirest

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apirest.ToolbarFragment
import CS2API.CS2ApiInstance
import CS2API.CategoryAdapter
import CS2API.SKINSAPI.CS2Skin
import androidx.activity.enableEdgeToEdge
import retrofit2.Call
import retrofit2.Response

class Weapons: AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_weapons)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbarFragment = supportFragmentManager.findFragmentById(R.id.toolbar_fragment_container)
                as? ToolbarFragment

        toolbarFragment?.let {
            it.setToolbarTitle("Armas")
            it.setToolbarTitle(getString(R.string.nav_weapons_title))

            it.setMenuButtonAction {
                Toast.makeText(this, "Abrir Menú de Armas", Toast.LENGTH_SHORT).show()
            }
        }


        recyclerView = findViewById(R.id.categories_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // 2 columnas de botones

        //generar botones
        generarCategoriasAutomaticamente()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.selectedItemId = R.id.nav_weapons

    }


    private fun generarCategoriasAutomaticamente() {
        // Reutilizamos la llamada de skins.json que ya creaste
        CS2ApiInstance.api.getSkins().enqueue(object : retrofit2.Callback<List<CS2Skin>> {
            override fun onResponse(call: Call<List<CS2Skin>>, response: Response<List<CS2Skin>>) {
                if (response.isSuccessful) {
                    val allSkins = response.body() ?: emptyList()

                    // --- AQUÍ ESTÁ LA LÓGICA DE EXTRACCIÓN ---
                    // 1. Recorremos todas las skins
                    // 2. Sacamos el nombre de la categoría (it.category?.name)
                    // 3. 'distinct()' elimina duplicados (para que no salga "Pistols" 500 veces)
                    // 4. 'sorted()' los ordena alfabéticamente

                    val categoriasUnicas = allSkins
                        .mapNotNull { it.category?.name } // Extrae nombres, ignora nulos
                        .distinct()                       // Solo únicos
                        .sorted()                         // Ordena A-Z
                    // Opcional: Si quieres quitar "Gloves" o cosas que no sean armas:
                    // .filter { it != "Gloves" && it != "Knives" }

                    // Configurar el adaptador con la lista generada
                    recyclerView.adapter =
                        CategoryAdapter(categoriasUnicas) { categoriaSeleccionada ->
                            // Esto se ejecuta cuando haces click en un botón generado
                            abrirListaSkins(categoriaSeleccionada)
                        }

                } else {
                    Toast.makeText(this@Weapons, "Error cargando categorías", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CS2Skin>>, t: Throwable) {
                Toast.makeText(this@Weapons, "Fallo de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun abrirListaSkins(categoria: String) {
        val intent = Intent(this, SkinListActivity::class.java)
        intent.putExtra("CATEGORY_NAME", categoria)
        startActivity(intent)
    }


}