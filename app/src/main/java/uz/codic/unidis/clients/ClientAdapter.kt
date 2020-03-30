package uz.codic.unidis.clients

import android.app.PendingIntent.getActivity
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import uz.codic.unidis.R
import uz.codic.unidis.clients.details.ClientDetailsActivity
import uz.codic.unidis.data.Client
import java.math.RoundingMode

class ClientAdapter : RecyclerView.Adapter<ClientAdapter.Viewholder>() {

    var  list = ArrayList<Client>()

    fun updateList(list: ArrayList<Client>){
        this.list = list
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ClientAdapter.Viewholder {
        val layout = LayoutInflater.from(p0.context).inflate(R.layout.item_client, p0,false)
        return Viewholder(layout)
    }

    override fun getItemCount(): Int {
        return if(list.isEmpty()){
            0
        } else{
            list.size
        }
    }

    override fun onBindViewHolder(p0: ClientAdapter.Viewholder, p1: Int) {
        var item = list[p1]

        p0.firstLetter.text = item.name.substring(0,1)
        p0.clientName.text = item.name
        p0.clientShopNumber.text = "№ " + item.row + " " + item.shopNumber
        p0.clientDebt.text = item.debt.toBigDecimal().setScale(1, RoundingMode.HALF_EVEN).toString() + " $"
        Log.d("TagCheckAdOU", item.clientId)

        p0.itemView.setOnClickListener {
            var intent = Intent(p0.itemView.context, ClientDetailsActivity::class.java)
            Log.d("TagCheckAd", item.clientId)
            intent.putExtra("client", list[p1])
            p0.itemView.context.startActivity(intent)
        }

        p0.setItem(p1)
    }

    class Viewholder(itemView : View) : RecyclerView.ViewHolder(itemView){

        var bgCircle = itemView.findViewById<ImageView>(R.id.bc_circle)
        var firstLetter = itemView.findViewById<TextView>(R.id.first_letter)
        var clientName = itemView.findViewById<TextView>(R.id.client_name)
        var clientShopNumber = itemView.findViewById<TextView>(R.id.client_shop_number)
        var clientDebt = itemView.findViewById<TextView>(R.id.client_debt)


        fun setItem(position: Int){

            when(position % 8){
                0 -> bgCircle.background = ContextCompat.getDrawable(itemView.context, R.drawable.circle_blue)
                1 -> bgCircle.background = ContextCompat.getDrawable(itemView.context, R.drawable.circle_green)
                2 -> bgCircle.background = ContextCompat.getDrawable(itemView.context, R.drawable.circle_orange)
                3 -> bgCircle.background = ContextCompat.getDrawable(itemView.context, R.drawable.circle_indigo)
                4 -> bgCircle.background = ContextCompat.getDrawable(itemView.context, R.drawable.circle_purple)
                5 -> bgCircle.background = ContextCompat.getDrawable(itemView.context, R.drawable.circle_pink)
                6 -> bgCircle.background = ContextCompat.getDrawable(itemView.context, R.drawable.circle_red)
                7 -> bgCircle.background = ContextCompat.getDrawable(itemView.context, R.drawable.circle_yellow)
            }
        }
    }
}
