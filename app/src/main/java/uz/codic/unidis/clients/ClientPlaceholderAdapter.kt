package uz.codic.unidis.clients

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import uz.codic.unidis.R
import uz.codic.unidis.data.Client

class ClientPlaceholderAdapter : RecyclerView.Adapter<ClientPlaceholderAdapter.Viewholder>() {


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): Viewholder {
        val layout = LayoutInflater.from(p0.context).inflate(R.layout.item_placeholder_client, p0,false)
        return Viewholder(layout)
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(p0: Viewholder, p1: Int) {

    }

    class Viewholder(itemView : View) : RecyclerView.ViewHolder(itemView){


    }
}