package uz.codic.unidis.sales.order

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

class OrderDialogFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return activity?.layoutInflater?.inflate(R.layout.fragment_add_product, container, false)
    }

    lateinit var warehouse: Warehouse
    lateinit var mCallback: OrderCallback

    fun setCallback(orderCallback: OrderCallback) {
        mCallback = orderCallback
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        warehouse = arguments?.getSerializable(ARG_CAUGHT) as Warehouse

        dialog_model_id.text = warehouse.productName

        btn_cancel.setOnClickListener {
            dialog.cancel()
        }

        if (warehouse.salesPrice == 0.0)
            edtxt_price.setText(warehouse.inputPrice.toString())
        else
            edtxt_price.setText(warehouse.salesPrice.toString())

        edtxt_price.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != "") {
                    if (s.toString().toDouble() < warehouse.inputPrice)
                        edtxt_price.error = "Narx kirim narxidan kam!"
                }
            }
        })

        edtxt_quantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != "") {
                    if (s.toString().toInt() > warehouse.quantity)
                        edtxt_quantity.error = "Tovar soni yetarli emas"
                }
            }
        })

        btn_add.setOnClickListener {
            if (!edtxt_price.text.toString().isEmpty()
                && !edtxt_quantity.text.toString().isEmpty()
            ) {
                if (edtxt_price.text.toString().toDouble() >= warehouse.salesPrice
                    && edtxt_quantity.text.toString().toInt() <= warehouse.quantity
                ) {
                    warehouse.quantity = edtxt_quantity.text.toString().toInt()
                    warehouse.salesPrice = edtxt_price.text.toString().toDouble()
                    mCallback.onOrderAddClick(warehouse)
                    dialog.cancel()
                } else {
                    Toast.makeText(view.context, "To'gri ma'lumot kiriting!", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(view.context, "Ma'lumotlarni kiriting!", Toast.LENGTH_SHORT).show()
            }
        }

    }


    companion object {
        private val ARG_CAUGHT = "warehouse"
        fun newInstance(warehouse: Warehouse): OrderDialogFragment {
            val args: Bundle = Bundle()
            args.putSerializable(ARG_CAUGHT, warehouse)
            val fragment = OrderDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }
}