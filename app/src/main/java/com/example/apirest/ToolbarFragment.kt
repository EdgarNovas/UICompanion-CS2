package com.example.apirest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.apirest.R

class ToolbarFragment : Fragment() {

    private lateinit var toolbarTitle: TextView
    private lateinit var menuButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_toolbar, container, false)

        toolbarTitle = view.findViewById(R.id.toolbar_title)
        menuButton = view.findViewById(R.id.btn_menu)

        return view
    }

    fun setToolbarTitle(title: String) {
        if (::toolbarTitle.isInitialized) {
            toolbarTitle.text = title
        }
    }

    fun setMenuButtonAction(listener: View.OnClickListener) {
        if (::menuButton.isInitialized) {
            menuButton.setOnClickListener(listener)
        }
    }
}