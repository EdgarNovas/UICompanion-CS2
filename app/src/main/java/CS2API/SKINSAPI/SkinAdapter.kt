package CS2API.SKINSAPI

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.apirest.R

class SkinAdapter(private var skins: List<CS2Skin>) : RecyclerView.Adapter<SkinAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.imgSkin)
        val name: TextView = view.findViewById(R.id.tvSkinName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_skin, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val skin = skins[position]
        holder.name.text = skin.name ?: "Sin nombre"

        Glide.with(holder.itemView.context)
            .load(skin.image)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(holder.img)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent =
                android.content.Intent(context, com.example.apirest.SkinDetailActivity::class.java)

            // Pasamos los datos a la otra pantalla
            intent.putExtra("EXTRA_NAME", skin.name)
            intent.putExtra("EXTRA_IMAGE", skin.image)
            intent.putExtra("EXTRA_CATEGORY", skin.category?.name)
            intent.putExtra("EXTRA_DESC", skin.description)
            intent.putExtra("EXTRA_PRICE", skin.price)


            context.startActivity(intent)
        }
    }

    override fun getItemCount() = skins.size

    fun actualizarLista(nuevaLista: List<CS2Skin>) {
        this.skins = nuevaLista
        notifyDataSetChanged() // Esto avisa a Android de que repinte la lista
    }
}