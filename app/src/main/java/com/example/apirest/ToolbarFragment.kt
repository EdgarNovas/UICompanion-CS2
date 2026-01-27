package com.example.apirest

import CS2API.FavoriteItem
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ToolbarFragment : Fragment() {
    private val databaseUrl = "https://cscompanion-ba26b-default-rtdb.europe-west1.firebasedatabase.app/"
    // Usamos '?' para evitar errores de lateinit
    private var toolbarTitle: TextView? = null
    private var menuButton: ImageButton? = null
    private var settingsButton: ImageButton? = null

    private val paddingRecicler : Int =30

    // Variables para guardar los datos si la vista aún no está lista
    private var pendingTitle: String? = null
    private var pendingClickListener: View.OnClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_toolbar, container, false)

        toolbarTitle = view.findViewById(R.id.toolbar_title)
        menuButton = view.findViewById(R.id.btn_menu)

        settingsButton = view.findViewById(R.id.btn_settings)


        pendingTitle?.let {
            toolbarTitle?.text = it
        }


        pendingClickListener?.let {
            menuButton?.setOnClickListener(it)
        }


        settingsButton?.setOnClickListener {
            mostrarMenuOpciones(it)
        }

        return view
    }

    private fun mostrarMenuOpciones(view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(R.menu.toolbar_menu, popup.menu)

        // Estado Dark Mode
        val sharedPref = requireContext().getSharedPreferences("AppConfig", Context.MODE_PRIVATE)
        val isDarkMode = sharedPref.getBoolean("dark_mode", true)
        popup.menu.findItem(R.id.action_dark_mode).isChecked = isDarkMode

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                //  CLICK EN FAVORITOS
                R.id.action_favorites -> {
                    mostrarDialogoFavoritos()
                    true
                }
                // CLICK EN MODO OSCURO
                R.id.action_dark_mode -> {
                    val nuevoEstado = !isDarkMode
                    sharedPref.edit().putBoolean("dark_mode", nuevoEstado).apply()
                    if (nuevoEstado) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    //IA
    private fun mostrarDialogoFavoritos() {
        val context = requireContext() // En fragment usamos requireContext()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            Toast.makeText(context, "Debes iniciar sesión", Toast.LENGTH_SHORT).show()
            return
        }

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Mis Favoritos")

        // Crear Lista (RecyclerView)
        val recycler = RecyclerView(context)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.setPadding(paddingRecicler, paddingRecicler, paddingRecicler, paddingRecicler)

        val listaFavoritos = mutableListOf<FavoriteItem>()

        // Adaptador con borrado
        val adapter = FavoritesAdapter(listaFavoritos) { itemABorrar ->
            // Borrar de Firebase
            FirebaseDatabase.getInstance(databaseUrl)
                .getReference("usuarios")
                .child(userId)
                .child("favoritos")
                .child(itemABorrar.id)
                .removeValue()
                .addOnSuccessListener {
                    Toast.makeText(context, "Eliminado", Toast.LENGTH_SHORT).show()
                    listaFavoritos.remove(itemABorrar)
                    recycler.adapter?.notifyDataSetChanged()
                }
        }
        recycler.adapter = adapter
        builder.setView(recycler)

        // Cargar datos de Firebase
        val ref = FirebaseDatabase.getInstance(databaseUrl)
            .getReference("usuarios")
            .child(userId)
            .child("favoritos")

        ref.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                listaFavoritos.clear()
                for (child in snapshot.children) {
                    val item = child.getValue(FavoriteItem::class.java)
                    if (item != null) {
                        listaFavoritos.add(item)
                    }
                }
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(context, "No tienes favoritos aún", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setPositiveButton("Cerrar", null)
        builder.show()
    }


    // Función para cambiar el título desde fuera
    fun setToolbarTitle(title: String) {
        pendingTitle = title
        toolbarTitle?.text = title // Si la vista ya existe, lo cambiamos directamente
    }

    // Función para asignar la acción del botón
    fun setMenuButtonAction(listener: View.OnClickListener) {
        pendingClickListener = listener
        menuButton?.setOnClickListener(listener)
    }
}