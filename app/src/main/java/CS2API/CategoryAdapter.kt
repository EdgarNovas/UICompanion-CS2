package CS2API

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apirest.R

// Recibe la lista de nombres y una función lambda para manejar el click
class CategoryAdapter(
    private val categories: List<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Usaremos un layout simple que sea solo un botón o un texto bonito
        val buttonText: TextView = view.findViewById(R.id.tvCategoryName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Crearemos un layout específico para estos botones
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_button, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoryName = categories[position]

        // Ponemos el texto en mayúsculas para que quede mejor
        holder.buttonText.text = categoryName.uppercase()

        // Configuramos el click
        holder.itemView.setOnClickListener {
            onClick(categoryName)
        }
    }

    override fun getItemCount() = categories.size
}