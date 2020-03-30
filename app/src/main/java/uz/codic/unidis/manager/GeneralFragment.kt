package uz.codic.unidis.manager

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_general.*

import uz.codic.unidis.R
import uz.codic.unidis.data.InfoGeneral
import uz.codic.unidis.data.Order
import uz.codic.unidis.data.OrderProduct
import uz.codic.unidis.utils.getFormattedNumber
import uz.codic.unidis.utils.toast
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList


class GeneralFragment : Fragment(), StatisticsListener {

    val firestore = FirebaseFirestore.getInstance()
    lateinit var brandListener: ListenerRegistration
    var ordersListener: ListenerRegistration? = null


    var adapter = GeneralAdapter()
    var orderList = ArrayList<InfoGeneral>()
    var generalList = ArrayList<InfoGeneral>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_general, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = activity as ManagerActivity
        activity.setStatisticsListener(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        general_info_recyler.layoutManager = GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false)
        general_info_recyler.adapter = adapter
        general_info_recyler.layoutAnimation =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
        general_info_recyler.isNestedScrollingEnabled = false

        getStatistics()
    }

    private fun getStatistics() {
        firestore.collection("brands").get().addOnSuccessListener {
            for (snapshot in it.documents) {
                var info = InfoGeneral()
                info.brandName = snapshot["name"] as String
                generalList.add(info)
            }

            ordersListener = firestore.collection("orders")
                .addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
                    if (e != null) {
                        Log.w("Tag", "listen:error", e)
                        return@EventListener
                    }

                    if (!orderList.isEmpty())
                        orderList.clear()
                    for (dc in snapshots!!.documents) {
                        var document = dc.toObject(Order::class.java)
                        for (order in document!!.products.values) {
                            var infoGeneral = InfoGeneral()
                            infoGeneral.brandName = order.brand
                            infoGeneral.quantity = order.quantity.toInt()
                            infoGeneral.profit = order.profit
                            infoGeneral.price = order.price
                            orderList.add(infoGeneral)
                        }
                    }
                    doCalculation()
                })
        }

    }

    private fun doCalculation() {
        var totalSales = 0.0
        var totalProfit = 0.0
        for (j in 0 until orderList.size) {
            for (i in 0 until generalList.size) {
                if (generalList[i].brandName == orderList[j].brandName) {
                    generalList[i].profit += orderList[j].profit
                    generalList[i].quantity += orderList[j].quantity
                }
            }

            totalProfit += orderList[j].profit
            totalSales += (orderList[j].quantity * orderList[j].price)
        }


        if(total_amount_profit != null && total_amount_sale != null ){
            total_amount_sale.text = getFormattedNumber(totalSales) + " $"
            total_amount_profit.text = getFormattedNumber(totalProfit) + " $"
            loading.visibility = View.GONE
            adapter.updateList(generalList)
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            GeneralFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    fun getSevendaysBeforeTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.time = Date(System.currentTimeMillis())
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        return calendar.getTime().time
    }

    override fun onPause() {
        ordersListener?.remove()
        super.onPause()
    }


}
