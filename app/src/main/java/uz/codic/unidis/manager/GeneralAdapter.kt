package uz.codic.unidis.manager

import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import uz.codic.unidis.R
import uz.codic.unidis.clients.ClientAdapter
import uz.codic.unidis.clients.details.ClientDetailsActivity
import uz.codic.unidis.data.Client
import uz.codic.unidis.data.InfoGeneral
import java.math.RoundingMode

class GeneralAdapter() : RecyclerView.Adapter<GeneralAdapter.Viewholder>() {

    var  list = ArrayList<InfoGeneral>()

    fun updateList(list: ArrayList<InfoGeneral>){
        this.list = list
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): GeneralAdapter.Viewholder {
        val layout = LayoutInflater.from(p0.context).inflate(R.layout.item_general_info, p0,false)
        return Viewholder(layout)
    }

    override fun getItemCount(): Int {
        return if(list.isEmpty()){
            0
        } else{
            list.size
        }
    }

    override fun onBindViewHolder(p0: Viewholder, p1: Int) {
        var item = list[p1]

        p0.brand.text = item.brandName
        p0.quantity.text = item.quantity.toString() + " ta"
        p0.profit.text = item.profit.toBigDecimal().setScale(2, RoundingMode.HALF_EVEN).toString() + " $"
//        p0.setItem(p1)
    }

    class Viewholder(itemView : View) : RecyclerView.ViewHolder(itemView){

        var brand = itemView.findViewById<TextView>(R.id.info_brand)
        var quantity = itemView.findViewById<TextView>(R.id.info_quantity)
        var profit = itemView.findViewById<TextView>(R.id.info_profit)
        var lnr = itemView.findViewById<LinearLayout>(R.id.info_lnr)

        fun setItem(position: Int){

            when(position % 6){
                0 -> lnr.background = ContextCompat.getDrawable(itemView.context, R.drawable.bc_info_orange)
                1 -> lnr.background = ContextCompat.getDrawable(itemView.context, R.drawable.bc_info_pink)
                2 -> lnr.background = ContextCompat.getDrawable(itemView.context, R.drawable.bc_info_blue)
                3 -> lnr.background = ContextCompat.getDrawable(itemView.context, R.drawable.bc_info_red)
                4 -> lnr.background = ContextCompat.getDrawable(itemView.context, R.drawable.bc_info_indigo)
                5 -> lnr.background = ContextCompat.getDrawable(itemView.context, R.drawable.bc_info_green)
            }
        }
    }
}