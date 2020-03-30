package uz.codic.unidis.clients.details

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import uz.codic.unidis.R
import uz.codic.unidis.cart.CartDialogFragment
import uz.codic.unidis.data.Order
import uz.codic.unidis.data.OrderProduct
import uz.codic.unidis.data.Product
import uz.codic.unidis.utils.Utils
import java.math.RoundingMode

class DetailsOrderAdapter(val callback: DetailsCallback) : RecyclerView.Adapter<DetailsOrderAdapter.Viewholder>() {

    var list = ArrayList<Order>()

    fun updateList(list: ArrayList<Order>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): DetailsOrderAdapter.Viewholder {
        val layout = LayoutInflater.from(p0.context).inflate(R.layout.item_details_client, p0, false)
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
        p0.setItem(list[p1])

        p0.itemView.setOnClickListener {
            callback.onItemClick(list[p1])
        }
    }

    class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var date = itemView.findViewById<TextView>(R.id.details_date)
        var givenMoney = itemView.findViewById<TextView>(R.id.details_given_money)
        var debt = itemView.findViewById<TextView>(R.id.details_debt)
        var total = itemView.findViewById<TextView>(R.id.details_total_sales)


        fun setItem(item: Order) {
            date.text = Utils.getDate(item.date, "dd.MM.yyyy")
            if (item.currency == "0"){
                givenMoney.text = item.givenMoney + " $"
                debt.text = item.debt.toString() + " $"
                total.text = getTotalAmount(item.products).toString() + " $"

            }
            else{
                givenMoney.text = item.givenMoney
                debt.text = item.debt.toString()
                total.text = (getTotalAmount(item.products)*item.exchangeRate.toDouble()).toString()
            }

        }

        fun getTotalAmount(products: HashMap<String, OrderProduct>) : Double {
            var total = 0.0
            for (orderProduct in products) {
                total += (orderProduct.value.price * orderProduct.value.quantity.toInt())
            }
            return total.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
        }
    }
}
