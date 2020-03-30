package uz.codic.unidis.clients.details

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import uz.codic.unidis.R
import uz.codic.unidis.data.Order
import uz.codic.unidis.data.OrderProduct
import java.math.RoundingMode

class ProductAdapter(var list: HashMap<String,OrderProduct>)  : RecyclerView.Adapter<ProductAdapter.Viewholder>() {


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ProductAdapter.Viewholder {
        val layout = LayoutInflater.from(p0.context).inflate(R.layout.item_order_details, p0, false)
        return Viewholder(layout)
    }

    override fun getItemCount(): Int {
        return if (list.isEmpty()) {
            0
        } else {
            list.size
        }
    }

    override fun onBindViewHolder(p0: Viewholder, p1: Int) {
        p0.setItem(list.values.elementAt(p1))
    }

    class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var name = itemView.findViewById<TextView>(R.id.txt_order_model_id)
        var quantity = itemView.findViewById<TextView>(R.id.item_order_quantity)
        var price = itemView.findViewById<TextView>(R.id.item_order_price)
        var brand = itemView.findViewById<TextView>(R.id.item_order_brand)


        fun setItem(item: OrderProduct) {
            name.text = item.name
            quantity.text = item.quantity
            price.text = item.price.toString() + " $"
            brand.text = item.brand
        }


    }
}
