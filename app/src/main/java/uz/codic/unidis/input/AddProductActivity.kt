package uz.codic.unidis.input

import android.animation.Animator
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_add_product.*
import uz.codic.unidis.R
import uz.codic.unidis.data.ProductModel
import java.util.HashMap

class AddProductActivity : AppCompatActivity() {


    internal var firestore = FirebaseFirestore.getInstance()

    var brands: ArrayList<String> = ArrayList<String>()
    var products: ArrayList<ProductModel> = ArrayList<ProductModel>()

    lateinit var brandsListener: ListenerRegistration
    lateinit  var productListener: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        getBrandNames()

        getProductList()

        onAddBrandClick()

        onAddModelIdClick()

        btn_back.setOnClickListener {
            finish()
            overridePendingTransition(0,R.anim.slide_to_right)
        }
    }

    private fun getProductList() {
        productListener = firestore.collection("products")
            .addSnapshotListener(EventListener { queryDocumentSnapshots, e ->
                if (e != null) {
                    Log.w("TAG", "listen:error", e)
                    return@EventListener
                }
                if (!products.isEmpty()) {
                    products.clear()
                }
                for (dc in queryDocumentSnapshots!!.documents) {
                    val product = ProductModel()
                    product.name = dc.data!!["name"] as String
                    product.brandName = dc.data!!["brandName"] as String
                    products.add(product)
                }
            })
    }

    private fun getBrandNames() {
        brandsListener = firestore.collection("brands")
            .addSnapshotListener(EventListener { queryDocumentSnapshots, e ->
                if (e != null) {
                    Log.w("TAG", "listen:error", e)
                    return@EventListener
                }
                if (!brands.isEmpty()) {
                    brands.clear()
                }
                for (dc in queryDocumentSnapshots!!.documents) {
                    brands.add(dc.data!!["name"] as String)
                    showBrandList(brands)
                }
            })

    }

    private fun showBrandList(brandList: List<String>) {
        val adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, brandList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_brands.setAdapter(adapter)
    }


    fun onAddBrandClick() {
        add_brand.setOnClickListener {
            if (!edtxt_brand_name.text.toString().isEmpty()) {
                if (!isBrandAlreadyExists(edtxt_brand_name.getText().toString())) {
                    val map = HashMap<String, Any>()
                    map["name"] = edtxt_brand_name.text.toString()

                    firestore.collection("brands").add(map).addOnSuccessListener {
                        anim_success_brand.visibility = View.VISIBLE
                        anim_success_brand.playAnimation()
                        Toast.makeText(this@AddProductActivity, "Muavaffaqiyatli qo'shildi!", Toast.LENGTH_SHORT).show()
                        anim_success_brand.addAnimatorListener(object : Animator.AnimatorListener{
                            override fun onAnimationRepeat(animation: Animator?) {
                            }

                            override fun onAnimationEnd(animation: Animator?) {
                                anim_success_brand.visibility = View.GONE

                            }

                            override fun onAnimationCancel(animation: Animator?) {
                            }

                            override fun onAnimationStart(animation: Animator?) {
                            }
                        })
                    }
                } else {
                    Toast.makeText(this, "Bu brend allaqachon qo'shilgan", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Bo'sh maydon", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isBrandAlreadyExists(name: String): Boolean {
        for (brandName in brands) {
            if (name.toLowerCase() == brandName.toLowerCase())
                return true
        }
        return false
    }

    private fun isModelAlreadyExists(modelID: String, brandName: String): Boolean {
        for (product in products) {
            if (product.name != ""&& product.brandName != "")
                if (modelID.toLowerCase() == product.name.toLowerCase() && brandName.toLowerCase() == product.brandName.toLowerCase())
                    return true
        }
        return false
    }

    fun onAddModelIdClick() {
        add_model.setOnClickListener {
            if (!edtxt_model_id.text.toString().isEmpty()) {
                if (!isModelAlreadyExists(
                        edtxt_model_id.text.toString(),
                        spinner_brands.selectedItem.toString()
                    )
                ) {
                    val map = HashMap<String, Any>()
                    map["brandName"] = spinner_brands.getSelectedItem().toString()
                    map["name"] = edtxt_model_id.getText().toString()
                    firestore.collection("products").add(map)
                        .addOnSuccessListener {
                            Toast.makeText(this@AddProductActivity, "Muavaffaqiyatli qo'shildi!", Toast.LENGTH_SHORT).show()
                            anim_success_model.visibility = View.VISIBLE
                            anim_success_model.playAnimation()
                            anim_success_model.addAnimatorListener(object : Animator.AnimatorListener{
                                override fun onAnimationRepeat(animation: Animator?) {
                                }

                                override fun onAnimationEnd(animation: Animator?) {
                                    anim_success_model.visibility = View.GONE
                                }

                                override fun onAnimationCancel(animation: Animator?) {
                                }

                                override fun onAnimationStart(animation: Animator?) {
                                }
                            })
                        }
                } else {
                    Toast.makeText(this, "Bu model allaqachon qo'shilgan", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this, "Bo'sh maydon", Toast.LENGTH_SHORT).show()
            }
        }
    }


    public override fun onStop() {
        brandsListener.remove()
        productListener.remove()
        super.onStop()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0,R.anim.slide_to_right)
    }

}
