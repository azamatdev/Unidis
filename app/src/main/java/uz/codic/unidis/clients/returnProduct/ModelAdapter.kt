package uz.codic.unidis.clients.returnProduct

import android.content.SharedPreferences
import android.support.design.widget.TextInputEditText
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.andexert.library.RippleView
import uz.codic.unidis.R
import uz.codic.unidis.data.ProductModel
import uz.codic.unidis.data.Warehouse
import java.util.prefs.AbstractPreferences

class ModelAdapter() : RecyclerView.Adapter<ModelAdapter.Viewholder>() {

    var productList = ArrayList<Warehouse>()

    companion object {
        var globalSelection = HashMap<String, Warehouse>()
    }

    fun updateList(productList: List<Warehouse>) {
        this.productList = productList as ArrayList<Warehouse>
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): Viewholder {
        val layout = LayoutInflater.from(p0.context).inflate(R.layout.item_return, p0, false)
        return Viewholder(layout)
    }

    override fun getItemCount(): Int {
        return if (productList.isEmpty()) {
            0
        } else {
            productList.size
        }
    }

    override fun onBindViewHolder(p0: Viewholder, p1: Int) {
        p0.model.text = productList[p1].productName

        p0.bindCounter(productList[p1].productId)

        p0.btnReturn.setOnClickListener {
            ModelAdapter.globalSelection[productList[p0.adapterPosition].productId] = productList[p1]
            p0.price.isEnabled = true
            p0.price.isFocusable = true
            p0.price.isFocusableInTouchMode = true
            p0.btnReturn.visibility = View.GONE
            p0.btnCounter.visibility = View.VISIBLE
        }

        p0.increment.setOnClickListener {
            p0.quantity.setText(p0.quantity.text.toString().toInt().inc().toString())
            if(ModelAdapter.globalSelection.containsKey(productList[p0.adapterPosition].productId))
            ModelAdapter.globalSelection[productList[p0.adapterPosition].productId]?.returnQuantity =
                    p0.quantity.text.toString().toInt()
        }
        p0.price.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!p0.price.text!!.isEmpty()){
                    Log.d("ProductId","" + productList[p0.adapterPosition].productId)
                    Log.d("ProductId","Price: " + ModelAdapter.globalSelection[productList[p0.adapterPosition].productId]?.returnPrice)
                    ModelAdapter.globalSelection[productList[p0.adapterPosition].productId]?.returnPrice =
                            s.toString().toDouble()
                }
            }
        })
        p0.decrement.setOnClickListener {
            if (p0.quantity.text.toString().toInt().dec() < 1) {
                ModelAdapter.globalSelection.remove(productList[p0.adapterPosition].productId)
                p0.price.setText("0.0")
                p0.btnCounter.visibility = View.GONE
                p0.btnReturn.visibility = View.VISIBLE

            } else {
                p0.quantity.setText(p0.quantity.text.toString().toInt().dec().toString())
                ModelAdapter.globalSelection[productList[p0.adapterPosition].productId]?.returnQuantity =
                        p0.quantity.text.toString().toInt()
            }
        }
    }

    class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val model = itemView.findViewById<TextView>(R.id.return_model_id)
        val price = itemView.findViewById<TextInputEditText>(R.id.return_price)
        val btnReturn = itemView.findViewById<TextView>(R.id.btn_return)
        val btnCounter = itemView.findViewById<LinearLayout>(R.id.btn_number_counter)
        val quantity = itemView.findViewById<EditText>(R.id.return_quantity)
        val increment = itemView.findViewById<RelativeLayout>(R.id.increment)
        val decrement = itemView.findViewById<RelativeLayout>(R.id.decrement)
        val line = itemView.findViewById<View>(R.id.return_line)

        fun bindCounter(productId: String) {
            if (ModelAdapter.globalSelection.containsKey(productId)) {
                val product = ModelAdapter.globalSelection[productId]
                btnReturn.visibility = View.GONE
                btnCounter.visibility = View.VISIBLE
                quantity.setText(product!!.returnQuantity.toString())
                price.setText(product.returnPrice.toString())
                price.isEnabled = true
                price.isFocusable = true
                price.isFocusableInTouchMode = true
            } else {
                btnCounter.visibility = View.GONE
                btnReturn.visibility = View.VISIBLE
                price.setText("0.0")
                price.isEnabled = false
                price.isFocusable = false
            }
        }


    }
}