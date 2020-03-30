package uz.codic.unidis.cart

import android.animation.Animator
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_cart.*
import uz.codic.unidis.R
import uz.codic.unidis.data.Product
import uz.codic.unidis.sales.order.OrderViewModel
import uz.codic.unidis.utils.Utils
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.collections.HashMap

class CartActivity : AppCompatActivity(), CartCallback {

    lateinit var adapter: CartAdapter
    private lateinit var orderViewModel: OrderViewModel
    private var clientId = ""
    var currency: Double = 8400.0

    lateinit var productList: List<Product>

    val firestore = FirebaseFirestore.getInstance()
    lateinit var listener: ListenerRegistration
    val dec = DecimalFormat("###,###")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        adapter = CartAdapter()
        adapter.setCallback(this)
        cart_recycler.layoutManager = LinearLayoutManager(this)
        cart_recycler.adapter = adapter

        orderViewModel = ViewModelProviders.of(this).get(OrderViewModel::class.java)
        orderViewModel.getAllProducts().observe(this, Observer { t ->
            adapter.updateList(t as List<Product>)
            productList = t
            cart_total_price.text = getTotalPrice(t).toString() + " $"
        })

        hashtag_choice.setOnCheckedChangeListener { group, checkedId, isChecked ->
            if (isChecked) {
                edtxt_client_search.setText("#")
                clientId = "#"
            } else {
                edtxt_client_search.setText("")
                clientId = ""
            }
        }


        btn_back.setOnClickListener { finish() }

        //Switch logic
        switch_rate.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                cart_total_price.text = (cart_total_price.text.toString().split(" ")[0].toDouble() * currency)
                    .toBigDecimal().setScale(1, RoundingMode.UP).toString().format(dec) + " so'm"
            } else {
                cart_total_price.text = (cart_total_price.text.toString().split(" ")[0].toDouble() / currency)
                    .toBigDecimal().setScale(2, RoundingMode.UP).toString() + " $"
            }
            if (!edtxt_client_money.text.toString().isEmpty()) {
                if (isChecked) {
                    currency_amount.text = edtxt_client_money.text.toString()
                    currency_label.text = " $"

                    edtxt_client_money.setText((currency_amount.text.toString().toDouble() * currency).toString())
                } else {
                    edtxt_client_money.setText(currency_amount.text)
                    currency_label.text = " so'm"

                    currency_amount.text = (edtxt_client_money.text.toString().toDouble() * currency).toString()
                }
                edtxt_client_money.setSelection(edtxt_client_money.text.length)
            }

        }

        edtxt_client_money.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!edtxt_client_money.text.toString().isEmpty()) {
                    if (switch_rate.isChecked) {
                        currency_amount.text = (edtxt_client_money.text.toString().toDouble() / currency).toBigDecimal()
                            .setScale(1, RoundingMode.UP).toString()
                        if (s.toString().toDouble() / currency > cart_total_price.text.toString().split(" ")[0].toDouble()) {
                            edtxt_client_money.error = "Berilgan so'mma ortiq"
                        }
                    } else {
                        currency_amount.text = (edtxt_client_money.text.toString().toDouble() * currency).toString()
                        if (s.toString().toDouble() > cart_total_price.text.toString().split(" ")[0].toDouble()) {
                            edtxt_client_money.error = "Berilgan so'mma ortiq"
                        }
                    }
                } else
                    currency_amount.text = "0.0"


            }
        })

        edtxt_client_search.setOnClickListener {
            if (Utils.isNetworkAvailable(this)) {
                if (hashtag_choice.isChecked(R.id.hashtag))
                    hashtag.isChecked = false
                val dialog = SearchDialogFragment()
                dialog.setCallback(this)
                val ft = supportFragmentManager.beginTransaction()
                dialog.show(ft, "DialogTag")
                window.setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                )
            } else
                Toast.makeText(this, "Internet aloqasi yo'q", Toast.LENGTH_SHORT).show()
        }

        cart_send.setOnClickListener {
            if (Utils.isNetworkAvailable(this)) {

                if (!productList.isEmpty()) {
                    if (!edtxt_client_search.text.toString().isEmpty() && !edtxt_client_money.text.toString().isEmpty()) {
                        lnr_send.visibility = View.GONE
                        anim_loading.visibility = View.VISIBLE

                        var debtInDollar = (cart_total_price.text.toString().split(" ")[0].toDouble() -
                                edtxt_client_money.text.toString().toDouble())
                            .toBigDecimal()
                            .setScale(1, RoundingMode.HALF_EVEN).toDouble()
                        if (switch_rate.isChecked) {
                            debtInDollar = (debtInDollar / currency)
                        }


                        var map = HashMap<String, Any>()
                        map["clientId"] = clientId
                        map["givenMoney"] = edtxt_client_money.text.toString()
                        if (switch_rate.isChecked) {
                            map["currency"] = "1"
                        } else
                            map["currency"] = "0"
                        map["debt"] = debtInDollar

                        map["productAmount"] = getTotalQuantity(productList).toString()
                        map["date"] = System.currentTimeMillis()
                        map["exchangeRate"] = currency.toString()
                        var nestedMap = HashMap<String, Any>()
                        for (product in productList) {
                            var orderProduct = HashMap<String, Any>()
                            orderProduct["productId"] = product.productId
                            orderProduct["name"] = product.name
                            orderProduct["brand"] = product.brand
                            orderProduct["price"] = product.salesPrice
                            orderProduct["quantity"] = product.quantity
                            orderProduct["profit"] = (product.salesPrice - product.inputPrice).toBigDecimal()
                                .setScale(1, RoundingMode.HALF_EVEN).toDouble()
                            nestedMap[product.productId] = orderProduct
                        }
                        map["products"] = nestedMap

                        val batch = firestore.batch()

                        for (product in productList) {
                            batch.update(
                                firestore.collection("warehouse").document(product.productId),
                                "quantity", FieldValue.increment(-product.quantity.toLong())
                            )
                        }

                        batch.update(
                            firestore.collection("clients").document(clientId),
                            "debt",
                            FieldValue.increment(debtInDollar)
                        )

                       
                        firestore.collection("orders").add(map)
                            .addOnSuccessListener { it ->
                                var cash = HashMap<String, Any>()
                                if (switch_rate.isChecked)
                                    cash["currency"] = 1
                                else
                                    cash["currency"] = 0

                                cash["amount"] = edtxt_client_money.text.toString().toDouble()
                                cash["orderId"] = it.id
                                cash["date"] = System.currentTimeMillis()
                                batch.set(
                                    firestore.collection("cash").document(),
                                    cash
                                )

                                batch.commit().addOnCompleteListener {
                                    anim_loading.visibility = View.GONE
                                    anim_success.visibility = View.VISIBLE
                                    anim_success.playAnimation()
                                    anim_success.addAnimatorListener(object : Animator.AnimatorListener {
                                        override fun onAnimationRepeat(animation: Animator?) {
                                        }

                                        override fun onAnimationEnd(animation: Animator?) {
                                            anim_success.visibility = View.GONE
                                            lnr_send.visibility = View.VISIBLE
                                            Toast.makeText(
                                                this@CartActivity,
                                                "Muavaffaqiyatli yozildi",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            clearAllData()
                                        }

                                        override fun onAnimationCancel(animation: Animator?) {
                                        }

                                        override fun onAnimationStart(animation: Animator?) {
                                        }
                                    }
                                    )
                                }

                            }



                    } else
                        Toast.makeText(this, "Iltimos ma'lumotlarni kiriting!", Toast.LENGTH_SHORT).show()
                } else
                    Toast.makeText(this, "Savat bo'sh", Toast.LENGTH_SHORT).show()
            } else
                Toast.makeText(this, "Internet aloqasi yo'q", Toast.LENGTH_SHORT).show()


        }


    }

    fun getTotalQuantity(list: List<Product>): Int {
        var total = 0
        for (client in list) {
            total += client.quantity.toInt()
        }
        return total
    }

    fun getTotalPrice(list: List<Product>): Double {
        var total = 0.0
        for (client in list) {
            total += client.salesPrice * client.quantity.toInt()
        }
        return total.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
    }


    override fun deleteProduct(product: Product, position: Int) {
        orderViewModel.deleteProduct(product)
    }

    override fun editProduct(product: Product) {
        if (Utils.isNetworkAvailable(this)) {
            val fm = supportFragmentManager
            val cartDialogFragment: CartDialogFragment =
                CartDialogFragment.newInstance(product)
            cartDialogFragment.setCallback(this)
            cartDialogFragment.show(fm, "fragment_edit_name")
        } else
            Toast.makeText(this, "Internet aloqasi yo'q", Toast.LENGTH_SHORT).show()


    }

    override fun onOrderEditClick(product: Product) {
        orderViewModel.updateProduct(product)
    }

    override fun onSearchClick(client: Client) {
        edtxt_client_search.setText(client.name)
        clientId = client.clientId
    }

    fun clearAllData() {
        orderViewModel.deleteAllProduct()
        clientId = ""
        if (hashtag_choice.isChecked(R.id.hashtag))
            hashtag.isChecked = false
        edtxt_client_search.setText("")
        edtxt_client_money.setText("")

    }
}
