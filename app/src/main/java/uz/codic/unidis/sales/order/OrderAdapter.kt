package uz.codic.unidis.sales.order

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_order_product.view.*
import uz.codic.unidis.R
import uz.codic.unidis.data.Warehouse

class OrderAdapter : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    var list: List<Warehouse> = ArrayList<Warehouse>()
    lateinit var mCallback: OrderCallback
    fun setCallback(callback: OrderCallback) {
        this.mCallback = callback
    }

    fun updateList(list: List<Warehouse>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.item_order_product, p0, false);
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
        holder.setItem(list[p1])

        holder.itemView.setOnClickListener {
            mCallback.onItemClick(list[p1])
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val modelId = itemView.product_model_id
        val quantity = itemView.edtxt_product_quantity
        val price = itemView.product_price
        val rippleEffect = itemView.item_ripple_order
        fun setItem(warehouse: Warehouse) {
            modelId.text = warehouse.productName
            quantity.text = warehouse.quantity.toString()
            if (warehouse.salesPrice == 0.0)
                price.text = warehouse.inputPrice.toString() + " $"
            else
                price.text = warehouse.salesPrice.toString() + " $"


        }
    }

}
