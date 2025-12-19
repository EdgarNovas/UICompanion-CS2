package CasesAPI

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apirest.R
import com.bumptech.glide.Glide

class CaseAdapter(private val cases: List<CS2Case>) : RecyclerView.Adapter<CaseAdapter.ViewHolder>() {

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
    }

    override fun getItemCount() = cases.size
}