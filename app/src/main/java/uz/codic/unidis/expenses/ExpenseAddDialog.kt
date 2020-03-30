package uz.codic.unidis.expenses

import android.animation.Animator
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_add_expense.*
import uz.codic.unidis.R
import uz.codic.unidis.data.Warehouse
import uz.codic.unidis.sales.order.OrderCallback
import uz.codic.unidis.utils.Utils
import uz.codic.unidis.utils.toast

class ExpenseAddDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return activity?.layoutInflater?.inflate(R.layout.fragment_add_expense, container, false)
    }

    lateinit var mCallback: ExpenseCallback

    fun setCallback(orderCallback: ExpenseCallback) {
        mCallback = orderCallback
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_add.setOnClickListener {
            if (Utils.isNetworkAvailable(context)) {
                if (edtxt_expense_reason.text.toString().isNotEmpty()
                    && edtxt_expense_reason.text.toString().isNotEmpty()
                ) {
                    lnr_send.visibility = View.GONE
                    anim_loading.visibility = View.VISIBLE
                    val currency = if (switch_rate.isChecked) 1 else 0

                    var hashMap = HashMap<String, Any>()
                    hashMap["reason"] = edtxt_expense_reason.text.toString()
                    hashMap["currency"] = currency
                    hashMap["amount"] = edtxt_expense_amount.text.toString().toDouble()
                    hashMap["date"] = System.currentTimeMillis()

                    val firestore = FirebaseFirestore.getInstance()
                    val batch = firestore.batch()
                    batch.set(
                        firestore.collection("expenses").document(),
                        hashMap
                    )

                    var hashMapCash = HashMap<String, Any>()
                    hashMapCash["date"] = System.currentTimeMillis()
                    hashMapCash["currency"] = currency
                    hashMapCash["amount"] = -edtxt_expense_amount.text.toString().toDouble()

                    batch.set(
                        firestore.collection("cash").document(),
                        hashMapCash
                    )

                    batch.commit()
                        .addOnSuccessListener {
                            anim_loading.visibility = View.GONE
                            anim_success.visibility = View.VISIBLE
                            anim_success.playAnimation()
                            anim_success.addAnimatorListener(object : Animator.AnimatorListener {
                                override fun onAnimationRepeat(animation: Animator?) {
                                }

                                override fun onAnimationEnd(animation: Animator?) {
                                    Toast.makeText(context, "Muavaffaqiyatli qo'shildi!", Toast.LENGTH_SHORT).show()
                                    lnr_send.visibility = View.VISIBLE
                                    dialog.cancel()
                                }

                                override fun onAnimationCancel(animation: Animator?) {
                                }

                                override fun onAnimationStart(animation: Animator?) {
                                }
                            })
                        }
//                mCallback.onAddClick(edtxt_expense_reason.text.toString(),edtxt_expense_amount.text.toString(), currency)

                } else {
                    toast(context!!,"Ma'lumotlarni kiriting")
                }
            } else {
                Toast.makeText(context , "Iltimos, internet aloqasini tiklang!", Toast.LENGTH_SHORT).show()
            }


        }

        btn_cancel.setOnClickListener()
        {
            dialog.cancel()
        }
    }

    fun onSuccess() {

    }


    companion object {
        fun newInstance(): ExpenseAddDialog {
            val args: Bundle = Bundle()
            val fragment = ExpenseAddDialog()
            fragment.arguments = args
            return fragment
        }
    }
}