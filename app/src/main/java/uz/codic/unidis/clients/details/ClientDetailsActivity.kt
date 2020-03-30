package uz.codic.unidis.clients.details

import android.animation.Animator
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlinx.android.synthetic.main.activity_client_details.*
import uz.codic.unidis.R
import uz.codic.unidis.clients.returnProduct.ReturnActivity
import uz.codic.unidis.data.Client
import uz.codic.unidis.data.Order
import uz.codic.unidis.utils.Utils
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*


class ClientDetailsActivity : AppCompatActivity(), DetailsCallback {

    var adapter = DetailsOrderAdapter(this)
    val database = FirebaseFirestore.getInstance()

    var clientOrders = ArrayList<Order>()

    lateinit var animation: LayoutAnimationController
    lateinit var client : Client

    lateinit var clientDebtListener : ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_details)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        client = intent.getSerializableExtra("client") as Client

        clientDebtListener =
                database.collection("clients").document(client.clientId)
                    .addSnapshotListener(EventListener<DocumentSnapshot> { snapshot, e ->
                        if (e != null) {
                            return@EventListener
                        }
                        if (snapshot != null && snapshot.exists()) {
                            details_edtxt_debt.setText(snapshot["debt"].toString())
                        } else {
                        }
                    })
        details_name.text = client.name
        details_shop_number.text = client.row + " " + client.shopNumber
        details_edtxt_debt.setText( client.debt.toBigDecimal().setScale(1, RoundingMode.HALF_EVEN).toString() )

        details_orders_recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        details_orders_recycler.adapter = adapter
        animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down);

        database.collection("orders").whereEqualTo("clientId", client.clientId)
            .addSnapshotListener(this, EventListener<QuerySnapshot> { snapshots, e ->
                if (e != null) {
                    Log.w("Error", "listen:error", e)
                    return@EventListener
                }
                if (!clientOrders.isEmpty()) {
                    clientOrders.clear()
                }

                for (dc in snapshots!!.documents) {
                    var order = dc.toObject(Order::class.java)!!
//                    order.date = getDate(order.date, "dd.MM.yyyy")
                    clientOrders.add(order)
                }
                details_orders_recycler.layoutAnimation = animation
                details_orders_recycler.adapter = adapter
                adapter.updateList(clientOrders)
            })

        btn__back_details.setOnClickListener {
            finish()
        }

        fab_return.setOnClickListener {
//            val fm = supportFragmentManager
//            val orderDetails =
//                ReturnDialog.newInstance(clientId = client.clientId)
//            orderDetails.show(fm, "fragment_return")
            val returnActivity = Intent(this, ReturnActivity::class.java)
            returnActivity.putExtra("clientId", client.clientId)
            startActivity(returnActivity)
            overridePendingTransition(R.anim.slide_from_right, 0)
        }

        btn_update_debt.setOnClickListener {
            if(Utils.isNetworkAvailable(this))
            database.collection("clients").document(client.clientId)
                .update("debt", details_edtxt_debt.text.toString().toBigDecimal().setScale(2, RoundingMode.UP)
                    .toDouble())
                .addOnSuccessListener {
                    Toast.makeText(this@ClientDetailsActivity, "Muavaffaqiyatli yangilandi!", Toast.LENGTH_SHORT).show()
                    anim_success_debt.visibility = View.VISIBLE
                    anim_success_debt.playAnimation()
                    Utils.hideKeyboard(this@ClientDetailsActivity)
                    anim_success_debt.addAnimatorListener(object : Animator.AnimatorListener{
                        override fun onAnimationRepeat(animation: Animator?) {
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            anim_success_debt.visibility = View.GONE
                        }

                        override fun onAnimationCancel(animation: Animator?) {
                        }

                        override fun onAnimationStart(animation: Animator?) {
                        }
                    })
                }
            else
                Toast.makeText(this, "Internet alqoasi yo'q!", Toast.LENGTH_SHORT).show()
        }
    }


    fun getDate(milliSeconds: Long, dateFormat: String): String {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat, Locale.US)
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    override fun onPause() {
        clientDebtListener.remove()
        super.onPause()
    }

    override fun onResume() {
        if(client != null)
        clientDebtListener =
                database.collection("clients").document(client.clientId)
                    .addSnapshotListener(EventListener<DocumentSnapshot> { snapshot, e ->
                        if (e != null) {
                            return@EventListener
                        }
                        if (snapshot != null && snapshot.exists()) {
                            details_edtxt_debt.setText(snapshot["debt"].toString())
                        } else {
                        }
                    })
        super.onResume()
    }

    override fun onItemClick(order: Order) {
        val fm = supportFragmentManager
        val orderDetails =
            OrderDetailsDialog.newInstance(order)
        orderDetails.show(fm, "fragment_order")
    }
}
