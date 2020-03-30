package uz.codic.unidis.clients.returnProduct

import android.animation.Animator
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_return.*
import uz.codic.unidis.R
import uz.codic.unidis.data.Warehouse
import uz.codic.unidis.utils.Utils

class ReturnActivity : AppCompatActivity(), ReturnCallback {

    var DIALOGTAG = "DialogTag"
    val firestore = FirebaseFirestore.getInstance()
    var brandList = ArrayList<String>()
    var productList = ArrayList<Warehouse>()
    var productMap = HashMap<String, List<Warehouse>>()
    var modelAdapter = ModelAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_return)
        model_recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        model_recycler.adapter = modelAdapter

        firestore.collection("brands").get().addOnSuccessListener {
            for (snapshot in it.documents) {
                brandList.add(snapshot["name"] as String)
            }

            val adapter = BrandAdapter(brandList, this)
            brand_recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
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
                        AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down)
                onBrandClick(brandList[0])
            }
        }

        btn_back_return.setOnClickListener {
            finish()
        }

        //1.Add back to warehouse
        //2.Add to report with minus number
        //3.Decrement debt from user
        btn_save.setOnClickListener {

            if (Utils.isNetworkAvailable(this)) {
                if (!ModelAdapter.globalSelection.isEmpty()) {
                    Log.d("TagCheck", "Inside")
                    return_save.visibility = View.GONE
                    anim_loading.visibility = View.VISIBLE
                    val batch = firestore.batch()
                    var totalMoneyBack = 0.0
                    //1.Add back to warehouse
                    for (product in ModelAdapter.globalSelection.values) {
//                        Log.d("TagCheck", "Loop: " + product.returnQuantity)
//                        Log.d("TagCheck", "Loop: " + product.returnPrice)
//                        batch.update(
//                            firestore.collection("warehouse").document(product.productId),
//                            "quantity", FieldValue.increment(product.returnQuantity.toLong())
//                        )
                        firestore.collection("warehouse").document(product.productId)
                            .update("quantity", FieldValue.increment(product.returnQuantity.toLong()))
                        totalMoneyBack += (product.returnPrice * product.returnQuantity)
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
                    firestore.collection("clients").document(intent.getStringExtra("clientId").toString())
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
                                        this@ReturnActivity,
                                        "Muavaffaqiyatli yozildi",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    ModelAdapter.globalSelection.clear()
                                    modelAdapter.notifyDataSetChanged()
                                    Utils.hideKeyboard(this@ReturnActivity)
                                }

                                override fun onAnimationCancel(animation: Animator?) {
                                }

                                override fun onAnimationStart(animation: Animator?) {
                                }
                            }
                            )
                        }

//                    batch.update(
//                        firestore.collection("clients").document(intent.getStringExtra("clientId").toString()),
//                        "debt", FieldValue.increment(-totalMoneyBack)
//                    )
//
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
//                                    this@ReturnActivity,
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
                } else {
                    Toast.makeText(
                        this,
                        "Maxsulot tanlang!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else
                Toast.makeText(
                    this,
                    "Internet aloqasi yo'q!",
                    Toast.LENGTH_SHORT
                ).show()

        }
    }

    override fun onBrandClick(brandName: String) {
        modelAdapter.updateList(productMap.get(brandName)!!)
    }

    override fun onFirstClick() {

    }
}
