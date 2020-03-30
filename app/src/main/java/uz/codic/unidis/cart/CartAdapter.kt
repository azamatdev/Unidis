package uz.codic.unidis.cart

import android.support.v4.app.ActivityCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_cart_item.view.*
import uz.codic.unidis.R
import uz.codic.unidis.data.Product
import java.math.RoundingMode

class CartAdapter : RecyclerView.Adapter<CartAdapter.ViewHolder>(){

    var list :List<Product> = ArrayList<Product>()
    lateinit var cartCallback : CartCallback

    fun setCallback(cartCallback: CartCallback){
        this.cartCallback = cartCallback
    }
    fun updateList(list: List<Product>){
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CartAdapter.ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.item_cart_item, p0, false);
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(p0: CartAdapter.ViewHolder, p1: Int) {
        p0.setItem(list[p1], p1)

        p0.btnDelete.setOnClickListener {
            cartCallback.deleteProduct(list[p1], p1)
        }

        p0.btnEdit.setOnClickListener {
            cartCallback.editProduct(list[p1])
        }
    }

    class ViewHolder(itemview : View) : RecyclerView.ViewHolder(itemview){
        val modelId = itemview.txt_model_id
        val quantity  = itemview.item_quantity
        val price  = itemview.item_price
        val total  = itemview.item_total
        val btnDelete = itemview.btn_delete
        val btnEdit = itemview.btn_edit
        val item_color = itemView.item_color
        fun setItem(product: Product, position: Int){
            modelId.text = product.name
            quantity.text = product.quantity
            price.text = product.salesPrice.toString()
            total.text = (product.quantity.toDouble() * product.salesPrice).toBigDecimal().setScale(1, RoundingMode.UP).toString()

            when(position%5){
                0->item_color.setBackgroundColor(ActivityCompat.getColor(itemView.context, R.color.redLight))
                1->item_color.setBackgroundColor(ActivityCompat.getColor(itemView.context, R.color.indigo))
                2->item_color.setBackgroundColor(ActivityCompat.getColor(itemView.context, R.color.blue))
                3->item_color.setBackgroundColor(ActivityCompat.getColor(itemView.context, R.color.green))
                4->item_color.setBackgroundColor(ActivityCompat.getColor(itemView.context, R.color.orange))
            }
        }



    }
}