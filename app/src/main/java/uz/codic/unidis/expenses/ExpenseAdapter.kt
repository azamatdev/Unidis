package uz.codic.unidis.expenses

import android.support.v4.app.ActivityCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.item_cart_item.view.*
import uz.codic.unidis.R
import uz.codic.unidis.cart.CartAdapter
import uz.codic.unidis.cart.CartCallback
import uz.codic.unidis.data.Expense
import uz.codic.unidis.data.Product
import uz.codic.unidis.utils.getFormattedNumber
import java.math.RoundingMode
import java.text.SimpleDateFormat

class ExpenseAdapter : RecyclerView.Adapter<ExpenseAdapter.ViewHolder>() {

    var list: List<Expense> = ArrayList<Expense>()
    lateinit var cartCallback: CartCallback
    val df = SimpleDateFormat("dd MMM,yyyy")
    fun setCallback(cartCallback: CartCallback) {
        this.cartCallback = cartCallback
    }

    fun updateList(list: List<Expense>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.item_expenses, p0, false);
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val item = list[p1]
        p0.reason.text = item.reason
        p0.date.text = df.format(item.date)
        if(item.currency == 0){
            p0.amount.text = getFormattedNumber(item.amount) + " $"
        }
        else{
            p0.amount.text = getFormattedNumber(item.amount)
        }
    }

    class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val reason = itemview.findViewById<TextView>(R.id.expense_reason)
        val amount= itemview.findViewById<TextView>(R.id.expense_amount)
        val date = itemview.findViewById<TextView>(R.id.expense_date)

    }

}