package com.example.apirest

import CS2API.FavoriteItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FavoritesAdapter(
    private val lista: MutableList<FavoriteItem>,
    private val onDeleteClick: (FavoriteItem) -> Unit // Acción al pulsar borrar
) : RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.img_fav_item)
        val nombre: TextView = view.findViewById(R.id.txt_fav_name)
        val cat: TextView = view.findViewById(R.id.txt_fav_cat)
        val btnDelete: ImageButton = view.findViewById(R.id.btn_fav_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorito, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]

        holder.nombre.text = item.name
        holder.cat.text = item.category

        // Cargar imagen
        Glide.with(holder.itemView.context)
            .load(item.image)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(holder.img)

        // Configurar botón borrar
        holder.btnDelete.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount() = lista.size
}