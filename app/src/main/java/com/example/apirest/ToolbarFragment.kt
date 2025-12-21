package com.example.apirest

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate

class ToolbarFragment : Fragment() {

    // Usamos '?' para evitar errores de lateinit
    private var toolbarTitle: TextView? = null
    private var menuButton: ImageButton? = null
    private var settingsButton: ImageButton? = null

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
        // Crear el PopupMenu anclado al botón de ajustes
        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(R.menu.toolbar_menu, popup.menu) // Inflamos el XML del paso 1

        // A. Averiguar estado actual para marcar el Checkbox
        val sharedPref = requireContext().getSharedPreferences("AppConfig", Context.MODE_PRIVATE)
        val isDarkMode = sharedPref.getBoolean("dark_mode", true)

        // Buscamos el item del menú y le ponemos el check si corresponde
        popup.menu.findItem(R.id.action_dark_mode).isChecked = isDarkMode

        // B. Qué pasa al hacer click en una opción
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_dark_mode -> {

                    val nuevoEstado = !item.isChecked
                    item.isChecked = nuevoEstado

                    //Guardar en memoria
                    sharedPref.edit().putBoolean("dark_mode", nuevoEstado).apply()

                    // Aplicar tema
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


    // Función para cambiar el título desde fuera
    fun setToolbarTitle(title: String) {
        pendingTitle = title // Guardamos el dato por si acaso
        toolbarTitle?.text = title // Si la vista ya existe, lo cambiamos directamente
    }

    // Función para asignar la acción del botón
    fun setMenuButtonAction(listener: View.OnClickListener) {
        pendingClickListener = listener
        menuButton?.setOnClickListener(listener)
    }
}