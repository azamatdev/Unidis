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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_edit_product.*
import uz.codic.unidis.R
import uz.codic.unidis.data.Product
import uz.codic.unidis.data.Warehouse

class CartDialogFragment : DialogFragment(){

    val firestore = FirebaseFirestore.getInstance()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return activity?.layoutInflater?.inflate(R.layout.fragment_edit_product, container, false)
    }

    lateinit var product: Product
    lateinit var mCallback: CartCallback

    fun setCallback(cartCallback: CartCallback){
        mCallback = cartCallback
    }

    lateinit var warehouseProduct : Warehouse

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        product = arguments?.getSerializable(ARG_CAUGHT) as Product
        cart_dialog_model_id.text = product.name

       firestore.collection("warehouse").document(product.productId)
            .get().addOnCompleteListener {
                warehouseProduct = it.result?.toObject(Warehouse::class.java)!!
               cart_edtxt_price.setText(product.salesPrice.toString())
               cart_edtxt_quantity.setText(product.quantity)
           }


        cart_edtxt_price.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != ""){
                    if (s.toString().toDouble() < warehouseProduct.inputPrice.toDouble())
                        cart_edtxt_price.error = "Narx kirim narxidan kam!"
                }
            }
        })
        cart_edtxt_quantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != ""){
                    if (s.toString().toInt() > warehouseProduct.quantity.toInt())
                        cart_edtxt_quantity.error = "Tovar soni yetarli emas"
                }
            }
        })


        cart_btn_cancel.setOnClickListener {
            dialog.cancel()
        }

        cart_btn_add.setOnClickListener {
            if(!cart_edtxt_price.text.toString().isEmpty() &&
                !cart_edtxt_quantity.text.toString().isEmpty()){
                if(cart_edtxt_quantity.text.toString().toInt() <=  warehouseProduct.quantity.toInt()
                   && cart_edtxt_price.text.toString().toDouble() >= warehouseProduct.inputPrice.toDouble()){
                    product.quantity = cart_edtxt_quantity.text.toString()
                    product.salesPrice = cart_edtxt_price.text.toString().toDouble()
                    mCallback.onOrderEditClick(product)
                    dialog.cancel()
                }
                else
                    Toast.makeText(view.context, "Ma'lumotlar noto'gri!", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(view.context, "Ma'lumotlarni kiriting!", Toast.LENGTH_SHORT).show()
            }
        }

    }


    companion object {
        private val ARG_CAUGHT = "warehouse"
        fun newInstance(product: Product): CartDialogFragment {
            val args: Bundle = Bundle()
            args.putSerializable(ARG_CAUGHT, product)
            val fragment = CartDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }
}