package uz.codic.unidis.clients.returnProduct

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_return.*
import uz.codic.unidis.R
import uz.codic.unidis.data.Warehouse
import uz.codic.unidis.utils.Utils

class ReturnDialog : DialogFragment(), ReturnCallback {

    var DIALOGTAG = "DialogTag"
    val firestore = FirebaseFirestore.getInstance()
    var brandList = ArrayList<String>()
    var productList = ArrayList<Warehouse>()
    var productMap = HashMap<String, List<Warehouse>>()
    var modelAdapter = ModelAdapter()


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
        return activity?.layoutInflater?.inflate(R.layout.fragment_return, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model_recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        model_recycler.adapter = modelAdapter

        firestore.collection("brands").get().addOnSuccessListener {
            for (snapshot in it.documents) {
                brandList.add(snapshot["name"] as String)
            }

            val adapter = BrandAdapter(brandList, this)
            brand_recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            brand_recycler.adapter = adapter

            firestore.collection("warehouse").get().addOnSuccessListener {
                for (snapshot in it.documents) {
                    var product = snapshot.toObject(Warehouse::class.java)!!
                    product.productId = snapshot.id
                    productList.add(product)
                }
                for (brand in brandList) {
                    var list = ArrayList<Warehouse>()
                    for (product in productList) {
                        if (product.brandName == brand) {
                            list.add(product)
                        }
                    }
                    productMap[brand] = list
                }
                model_recycler.layoutAnimation =
                        AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
                onBrandClick(brandList[0])
            }
        }

        btn_back_return.setOnClickListener {
            dialog.cancel()
        }

        //1.Add back to warehouse
        //2.Add to report with minus number
        //3.Decrement debt from user
        btn_save.setOnClickListener {

            if(Utils.isNetworkAvailable(context)){
                if(!ModelAdapter.globalSelection.isEmpty()){
                    Log.d("TagCheck", "Inside")
                    return_save.visibility = View.GONE
                    anim_loading.visibility = View.VISIBLE
                    val batch = firestore.batch()
                    var totalMoneyBack = 0.0
                    //1.Add back to warehouse
                    for (product in ModelAdapter.globalSelection.values) {
//                        Log.d("TagCheck", "Loop: " + product.returnQuantity)
//                        Log.d("TagCheck", "Loop: " + product.returnPrice)
//                    batch.update(
//                        firestore.collection("warehouse").document(product.productId),
//                        "quantity", FieldValue.increment(product.returnQuantity.toLong())
//                    )
                        firestore.collection("warehouse").document(product.productId)
                            .update("quantity", FieldValue.increment(product.returnQuantity.toLong()))
                        totalMoneyBack += product.returnPrice
                    }

                    //2.Add to report with minus number
                    var cash = HashMap<String, Any>()
                    cash["currency"] = 0

                    cash["amount"] = -totalMoneyBack
                    cash["orderId"] = ""
                    cash["date"] = System.currentTimeMillis().toString()
                    firestore.collection("cash").add(cash)
//                    batch.set(
//                        firestore.collection("cash").document(),
//                        cash
//                    )

                    //3.Decrement debt from user
                    firestore.collection("clients").document(arguments?.get("clientId").toString())
                        .update("debt", FieldValue.increment(-totalMoneyBack))
                        .addOnCompleteListener {
                            anim_loading.visibility = View.GONE
                            anim_success.visibility = View.VISIBLE
                            anim_success.playAnimation()
                            anim_success.addAnimatorListener(object : Animator.AnimatorListener {
                                override fun onAnimationRepeat(animation: Animator?) {
                                }

                                override fun onAnimationEnd(animation: Animator?) {
                                    anim_success.visibility = View.GONE
                                    return_save.visibility = View.VISIBLE
                                    Toast.makeText(
                                        context,
                                        "Muavaffaqiyatli yozildi",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    ModelAdapter.globalSelection.clear()
                                    modelAdapter.notifyDataSetChanged()
                                }

                                override fun onAnimationCancel(animation: Animator?) {
                                }

                                override fun onAnimationStart(animation: Animator?) {
                                }
                            }
                            )
                        }

//                    batch.update(firestore.collection("clients").document(arguments?.get("clientId").toString()),
//                        "debt", FieldValue.increment(-totalMoneyBack))

//                    Log.d("TagCheck", "Total " + totalMoneyBack)
//                    batch.commit().addOnCompleteListener {
//                        anim_loading.visibility = View.GONE
//                        anim_success.visibility = View.VISIBLE
//                        anim_success.playAnimation()
//                        anim_success.addAnimatorListener(object : Animator.AnimatorListener {
//                            override fun onAnimationRepeat(animation: Animator?) {
//                            }
//
//                            override fun onAnimationEnd(animation: Animator?) {
//                                anim_success.visibility = View.GONE
//                                return_save.visibility = View.VISIBLE
//                                Toast.makeText(
//                                    context,
//                                    "Muavaffaqiyatli yozildi",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                ModelAdapter.globalSelection.clear()
//                                modelAdapter.notifyDataSetChanged()
//                            }
//
//                            override fun onAnimationCancel(animation: Animator?) {
//                            }
//
//                            override fun onAnimationStart(animation: Animator?) {
//                            }
//                        }
//                        )
//                    }
                }
                else{
                    Toast.makeText(
                        context,
                        "Maxsulot tanlang!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            else
                Toast.makeText(
                    context,
                    "Internet aloqasi yo'q!",
                    Toast.LENGTH_SHORT
                ).show()

        }
    }

    override fun onFirstClick() {
        val view = brand_recycler.findViewHolderForAdapterPosition(0)?.itemView
        if (view != null) {
            view.findViewById<RelativeLayout>(R.id.brand_relative)!!.background =
                    ContextCompat.getDrawable(context!!, R.drawable.bc_brand_unselected)
            view.findViewById<TextView>(R.id.brandName)!!.setTextColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.textMainColor
                )
            )
        }
    }

    override fun onBrandClick(brandName: String) {
        modelAdapter.updateList(productMap.get(brandName)!!)
    }

    fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        private val ARG_CAUGHT = "order"
        fun newInstance(clientId: String): ReturnDialog {
            val args: Bundle = Bundle()
            args.putString("clientId", clientId)
            val fragment = ReturnDialog()
            return fragment
        }
    }
}