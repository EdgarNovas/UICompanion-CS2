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
import android.util.Log
import androidx.activity.enableEdgeToEdge
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.analytics.FirebaseAnalytics
import retrofit2.Call
import retrofit2.Response
import android.view.View
import android.widget.ProgressBar

class Weapons: AppCompatActivity() {

    val numOfRows : Int = 2

    private lateinit var progressBar: ProgressBar
    private lateinit var analytics: FirebaseAnalytics
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analytics = FirebaseAnalytics.getInstance(this)
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
            it.setToolbarTitle("Weapons")
            it.setToolbarTitle(getString(R.string.nav_weapons_title))

            it.setMenuButtonAction {
                Toast.makeText(this, "Abrir Menú de Armas", Toast.LENGTH_SHORT).show()
            }
        }


        recyclerView = findViewById(R.id.categories_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, numOfRows)

        progressBar = findViewById(R.id.loading_spinner)
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        //generar botones
        generarCategoriasAutomaticamente()

        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        BottomNavigation(this).setup(bottomNavView, R.id.nav_weapons)

    }


    private fun generarCategoriasAutomaticamente() {

        CS2ApiInstance.api.getSkins().enqueue(object : retrofit2.Callback<List<CS2Skin>> {
            override fun onResponse(call: Call<List<CS2Skin>>, response: Response<List<CS2Skin>>) {
                if (response.isSuccessful) {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    val allSkins = response.body() ?: emptyList()

                    val categoriasUnicas = allSkins
                        .mapNotNull { it.category?.name }
                        .distinct()
                        .sorted()

                    recyclerView.adapter =
                        CategoryAdapter(categoriasUnicas) { categoriaSeleccionada ->

                            abrirListaSkins(categoriaSeleccionada)
                        }

                } else {
                    FirebaseCrashlytics.getInstance().log( "Error en respuesta: ${response.code()}")
                    Toast.makeText(this@Weapons, "Error cargando categorías", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CS2Skin>>, t: Throwable) {
                FirebaseCrashlytics.getInstance().recordException(t)
                Toast.makeText(this@Weapons, "Fallo de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun abrirListaSkins(categoria: String) {
        val bundle = Bundle()

        bundle.putString("category_name", categoria)

        analytics.logEvent("select_weapon_category", bundle)

        Log.d("Analytics", "Evento enviado: Categoría $categoria")

        val intent = Intent(this, SkinListActivity::class.java)
        intent.putExtra("CATEGORY_NAME", categoria)
        startActivity(intent)
    }


}