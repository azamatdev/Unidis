package uz.codic.unidis.clients.returnProduct

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import uz.codic.unidis.R
import uz.codic.unidis.clients.details.ProductAdapter
import uz.codic.unidis.data.OrderProduct

class BrandAdapter(var list: ArrayList<String>,  var callback : ReturnCallback) : RecyclerView.Adapter<BrandAdapter.Viewholder>() {

    var lastView : RelativeLayout? = null
    var lastTextView : TextView? = null
    var isFirstSelected = true
    var lastPosition = 0

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): Viewholder {
        val layout = LayoutInflater.from(p0.context).inflate(R.layout.item_brand_return, p0, false)
        return Viewholder(layout)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return if (list.isEmpty()) {
            0
        } else {
            list.size
        }
    }

    override fun onBindViewHolder(p0: Viewholder, p1: Int) {
        p0.setItem(list[p1])

        if(p1 == 0 && isFirstSelected){
            p0.brandBackground.background = ContextCompat.getDrawable(p0.itemView.context, R.drawable.bc_brand_selected)
            lastView = p0.brandBackground
            p0.name.setTextColor( ContextCompat.getColor(p0.itemView.context, R.color.white))
            lastTextView = p0.name
            isFirstSelected = false
        }
        p0.itemView.setOnClickListener {
            if(lastPosition != p1){
//                if(isFirstSelected){
//                    callback.onFirstClick()
//                    isFirstSelected = false
//                }
                lastView?.background = ContextCompat.getDrawable(p0.itemView.context, R.drawable.bc_brand_unselected)
                lastTextView?.setTextColor( ContextCompat.getColor(p0.itemView.context, R.color.textMainColor))
            }
            lastPosition = p1
            lastView = p0.brandBackground
            lastTextView = p0.name
            callback.onBrandClick(list[p1])
            p0.brandBackground.background = ContextCompat.getDrawable(p0.itemView.context, R.drawable.bc_brand_selected)
            p0.name.setTextColor( ContextCompat.getColor(p0.itemView.context, R.color.white))
        }
    }

    class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.brandName)
        val brandBackground = itemView.findViewById<RelativeLayout>(R.id.brand_relative)
        fun setItem(item: String) {
            name.text = item
        }


    }
}