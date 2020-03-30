package uz.codic.unidis.sales

import android.content.Intent
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_sales_brand.view.*
import uz.codic.unidis.R
import uz.codic.unidis.sales.order.OrderActivity

class SalesBrandAdapter : RecyclerView.Adapter<SalesBrandAdapter.ViewHolder>() {

    var list: List<String> = ArrayList<String>()

    fun updateList(list: List<String>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SalesBrandAdapter.ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.item_sales_brand, p0, false);
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: SalesBrandAdapter.ViewHolder, p1: Int) {
        holder.brandName.text = list[p1]

        holder.itemColors(p1)

        holder.itemView.setOnClickListener {
                val intent = Intent(holder.itemView.context, OrderActivity::class.java)
                intent.putExtra("brandPosition", p1)
                holder.itemView.context.startActivity(intent)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val brandName = itemView.sales_brand_name
        val color = itemView.sales_color
        val rippleEffect = itemView.brands_ripple_effect
        fun itemColors(p1: Int) {
            when (p1 % 5) {
                0 -> {
                    color.setBackgroundColor(ActivityCompat.getColor(itemView.context, R.color.redLight))
                }
                1 -> {
                    color.setBackgroundColor(ActivityCompat.getColor(itemView.context, R.color.indigo))
                }
                2 -> {
                    color.setBackgroundColor(ActivityCompat.getColor(itemView.context, R.color.blue))
                }
                3 -> {
                    color.setBackgroundColor(ActivityCompat.getColor(itemView.context, R.color.green))
                }
                4 -> {
                    color.setBackgroundColor(ActivityCompat.getColor(itemView.context, R.color.orange))
                }
            }
        }
    }

}