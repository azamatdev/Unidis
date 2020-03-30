package uz.codic.unidis.clients.details

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.fragment_order_details.*
import kotlinx.android.synthetic.main.item_details_client.*
import uz.codic.unidis.R
import uz.codic.unidis.data.Order
import uz.codic.unidis.utils.Utils

class OrderDetailsDialog : DialogFragment() {

    var DIALOGTAG = "DialogTag"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window.setLayout(width, height)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return activity?.layoutInflater?.inflate(R.layout.fragment_order_details, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var order = arguments?.getSerializable(ARG_CAUGHT) as Order

        btn_cancel_order.setOnClickListener {
            dialog.cancel()
        }
        order_date.text = Utils.getDate(order.date,"dd.MM.yyyy")

        if(order.currency == "0"){
            details_order_debt.text = order.debt.toString() + " $"
            details_order_total_sale.text = (order.givenMoney.toDouble() + order.debt.toDouble()).toString()+ " $"
        }
        else{
            details_order_debt.text = order.debt.toString()
            details_order_total_sale.text = (order.givenMoney.toDouble() + order.debt.toDouble()).toString()
        }

        var productAdapter = ProductAdapter(order.products)
        order_details_recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        order_details_recycler.adapter = productAdapter

    }


    fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        private val ARG_CAUGHT = "order"
        fun newInstance(order : Order): OrderDetailsDialog {
            val args: Bundle = Bundle()
            args.putSerializable(ARG_CAUGHT, order)
            val fragment = OrderDetailsDialog()
            fragment.arguments = args
            return fragment
        }
    }
}