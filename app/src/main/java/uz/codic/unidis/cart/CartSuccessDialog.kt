package uz.codic.unidis.cart

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_add_product.*
import uz.codic.unidis.R
import uz.codic.unidis.data.Warehouse
import uz.codic.unidis.sales.order.OrderCallback
import uz.codic.unidis.sales.order.OrderDialogFragment

class CartSuccessDialog: DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return activity?.layoutInflater?.inflate(R.layout.fragment_add_product, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


    companion object {
        private val ARG_CAUGHT = "cart"
        fun newInstance(warehouse: Warehouse): CartDialogFragment {
            val args: Bundle = Bundle()
            args.putSerializable(ARG_CAUGHT, warehouse)
            val fragment = CartDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }
}