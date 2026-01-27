package CS2API

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apirest.R
import com.bumptech.glide.Glide

import CS2API.CS2Case
class CaseAdapter(private var cases: List<CS2Case>) : RecyclerView.Adapter<CaseAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.imgCase)
        val name: TextView = view.findViewById(R.id.tvCaseName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_case, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = cases[position]
        holder.name.text = item.name
        Glide.with(holder.itemView.context).load(item.image).into(holder.img)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context

            val intent =
                android.content.Intent(context, com.example.apirest.SkinDetailActivity::class.java)

            intent.putExtra("EXTRA_NAME", item.name)
            intent.putExtra("EXTRA_IMAGE", item.image)

            intent.putExtra("EXTRA_CATEGORY", "Contenedor / Caja")
            intent.putExtra("EXTRA_DESC", item.description)

            context.startActivity(intent)
        }

    }

    override fun getItemCount() = cases.size

    fun actualizarLista(nuevaLista: List<CS2Case>) {
        this.cases = nuevaLista
        notifyDataSetChanged()
    }
}