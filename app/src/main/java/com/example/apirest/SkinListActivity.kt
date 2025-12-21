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
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Response

class SkinListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryToFilter: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_skin_list)


        //Recibir la categoría del arma
        categoryToFilter = intent.getStringExtra("CATEGORY_NAME") ?: ""
        title = "Skins de $categoryToFilter"

        val toolbarFragment = supportFragmentManager.findFragmentById(R.id.toolbar_fragment_container) as? ToolbarFragment
        toolbarFragment?.let {
            // Ponemos el nombre de la categoría como título
            it.setToolbarTitle(categoryToFilter)

            // Configuramos el botón del menú para que haga "Atrás"

            it.setMenuButtonAction {
                finish()
            }
        }

        recyclerView = findViewById(R.id.skins_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        obtenerSkinsFiltradas()


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_weapons
    }

    private fun obtenerSkinsFiltradas() {
        CS2ApiInstance.api.getSkins().enqueue(object : retrofit2.Callback<List<CS2Skin>> {
            override fun onResponse(call: Call<List<CS2Skin>>, response: Response<List<CS2Skin>>) {
                if (response.isSuccessful) {
                    val allSkins = response.body() ?: emptyList()



                    val filteredList = allSkins.filter { skin ->
                        skin.category?.name?.contains(categoryToFilter, ignoreCase = true) == true

                    }

                    recyclerView.adapter = SkinAdapter(filteredList)

                    if (filteredList.isEmpty()) {
                        Toast.makeText(applicationContext, "No se encontraron skins de tipo $categoryToFilter", Toast.LENGTH_SHORT).show()
                    }

                }
            }

            override fun onFailure(call: Call<List<CS2Skin>>, t: Throwable) {
                Toast.makeText(applicationContext, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}