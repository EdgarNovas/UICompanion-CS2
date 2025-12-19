package com.example.apirest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView

class ToolbarFragment : Fragment() {

    // Usamos '?' para evitar errores de lateinit
    private var toolbarTitle: TextView? = null
    private var menuButton: ImageButton? = null

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


        pendingTitle?.let {
            toolbarTitle?.text = it
        }


        pendingClickListener?.let {
            menuButton?.setOnClickListener(it)
        }

        return view
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