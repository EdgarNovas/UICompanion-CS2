package CS2API

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.apirest.R

class AgentAdapter(private var agents: List<CS2Agent>) : RecyclerView.Adapter<AgentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.imgAgent)
        val name: TextView = view.findViewById(R.id.tvAgentName)
        val team: TextView = view.findViewById(R.id.tvAgentTeam)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_agent, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val agent = agents[position]

        holder.name.text = agent.name ?: "Agente desconocido"
        holder.team.text = agent.team?.name ?: "Sin equipo"

        Glide.with(holder.itemView.context)
            .load(agent.image)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(holder.img)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context

            val intent =
                android.content.Intent(context, com.example.apirest.SkinDetailActivity::class.java)
            intent.putExtra("EXTRA_NAME", agent.name)
            intent.putExtra("EXTRA_IMAGE", agent.image)

            intent.putExtra("EXTRA_CATEGORY", "Agent")
            intent.putExtra("EXTRA_DESC", agent.description)

            context.startActivity(intent)
        }

    }

    override fun getItemCount() = agents.size

    fun actualizarLista(nuevaLista: List<CS2Agent>) {
        this.agents = nuevaLista
        notifyDataSetChanged()
    }
}