package uz.codic.unidis.input

import android.animation.Animator
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_input.*
import uz.codic.unidis.R
import uz.codic.unidis.data.ProductModel
import uz.codic.unidis.data.Warehouse
import uz.codic.unidis.utils.Utils
import java.util.HashMap

class InputActivity : AppCompatActivity() {



    var firestore = FirebaseFirestore.getInstance()
    var brandList = ArrayList<String>()
    var productList = ArrayList<ProductModel>()
    var warehouseList = ArrayList<Warehouse>()

    lateinit var brandsListener: ListenerRegistration
    lateinit var productListener: ListenerRegistration
    lateinit var warehouseListener: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        getBrandName()

        getProductList()

        getWareHouseList()

        listenForBrandChange()

        listenForProductChange()

        addToWarehouse()

        fab_add_products.setOnClickListener {
            val addProduct = Intent(this, AddProductActivity::class.java)
            startActivity(addProduct)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

        btn_back.setOnClickListener {
            finish()
            overridePendingTransition(0,R.anim.slide_to_right)
        }
    }

    private fun getBrandName() {
        brandsListener = firestore.collection("brands")
            .addSnapshotListener(EventListener<QuerySnapshot> { queryDocumentSnapshots, e ->
                if (e != null) {
                    Log.w("TAG", "listen:error", e)
                    return@EventListener
                }
                if (!brandList.isEmpty()) {
                    brandList.clear()
                }
                for (dc in queryDocumentSnapshots!!.documents) {
                    brandList.add(dc.data!!["name"] as String)
                }
                showBrandList(brandList)
            })
    }

    private fun getProductList() {
        productListener = firestore.collection("products")
            .addSnapshotListener(EventListener<QuerySnapshot> { queryDocumentSnapshots, e ->
                if (e != null) {
                    Log.w("TAG", "listen:error", e)
                    return@EventListener
                }
                if (!productList.isEmpty()) {
                    productList.clear()
                }
                for (dc in queryDocumentSnapshots!!.documents) {
                    val product = ProductModel()
                    product.productId = dc.id
                    product.name = dc.data!!["name"] as String
                    product.brandName = dc.data!!["brandName"] as String
                    productList.add(product)
                }
                showProductList(spinner_brands.selectedItem.toString())
            })
    }

    private fun getWareHouseList() {
        warehouseListener = firestore.collection("warehouse")
            .addSnapshotListener(EventListener<QuerySnapshot> { queryDocumentSnapshots, e ->
                if (e != null) {
                    Log.w("TAG", "listen:error", e)
                    return@EventListener
                }
                if (!warehouseList.isEmpty()) {
                    warehouseList.clear()
                }
                for (dc in queryDocumentSnapshots!!.documents) {
                    val warehouse = dc.toObject(Warehouse::class.java)
                    assert(warehouse != null)
                    warehouse!!.productId = dc.id
                    warehouseList.add(warehouse)
                }

                val product = getSelectedProduct(spinner_products.selectedItem.toString())
                if (product!!.productId != "") {
                    if (getWarehouseObject(product.productId) != null) {
                        updateWarehouseData(getWarehouseObject(product.productId)!!)
                    } else {
                        //                                warehousePrice.setText("");
                        warehouse_quantity.text = ""
                    }
                }
            })
    }

    private fun listenForBrandChange() {
        spinner_brands.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if(spinner_brands.selectedItem != null)
                    showProductList(spinner_brands.selectedItem.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun listenForProductChange() {
        spinner_products.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val product = getSelectedProduct(spinner_products.getSelectedItem().toString())
                if (getWarehouseObject(product!!.productId) != null) {
                    updateWarehouseData(getWarehouseObject(product.productId)!!)
                } else {
                    //                    warehousePrice.setText("");
                    warehouse_quantity.text = ""
                }
                //                edtxtPrice.setText("");
                edtxt_product_quantity.setText("")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun addToWarehouse() {
        add_product_warehouse.setOnClickListener {
            if (Utils.isNetworkAvailable(this@InputActivity)) {
                val product: ProductModel?
                //if quantity is specified
                if (!edtxt_product_quantity.getText().toString().isEmpty()) {
                    //if the product is chosen
                    if (getSelectedProduct(spinner_products.selectedItem.toString()) != null) {
                        product = getSelectedProduct(spinner_products.selectedItem.toString())

                        //if product is already added to database
                        //need to update
                        if ((getWarehouseObject(product!!.productId) != null && edtxt_product_quantity.text.toString() != ".")) {

                            //                                    Double total = (inputPrice * inputQuantity + warehousePrice * warehouseQuantity) /
                            //                                            (inputQuantity + warehouseQuantity);

                            val quantity = java.lang.Long.parseLong(edtxt_product_quantity.text.toString())
                            firestore.collection("warehouse").document(product.productId)
                                .update("quantity", FieldValue.increment(quantity))
                                .addOnCompleteListener {
                                    anim_success_warehouse.setVisibility(View.VISIBLE)
                                    anim_success_warehouse.playAnimation()
                                    Toast.makeText(this, "Muavaffaqiyatli yangilandi!", Toast.LENGTH_SHORT)
                                        .show()
                                    edtxt_product_quantity.setText("")
                                    anim_success_warehouse.addAnimatorListener(object : Animator.AnimatorListener {
                                        override fun onAnimationStart(animation: Animator) {

                                        }

                                        override fun onAnimationEnd(animation: Animator) {
                                            anim_success_warehouse.setVisibility(View.GONE)
                                        }

                                        override fun onAnimationCancel(animation: Animator) {

                                        }

                                        override fun onAnimationRepeat(animation: Animator) {

                                        }
                                    })
                                }
                        } else {
                            if (edtxt_product_quantity.getText().toString() != ".") {
                                val map = HashMap<String, Any>()
                                map["brandName"] = product.brandName
                                map["productName"] = product.name
                                map["quantity"] = Integer.parseInt(edtxt_product_quantity.text.toString())
                                map["inputPrice"] = 0.0
                                map["salesPrice"] = 0.0
                                firestore.collection("warehouse").document(product.productId)
                                    .set(map).addOnCompleteListener {
                                        anim_success_warehouse.setVisibility(View.VISIBLE)
                                        anim_success_warehouse.playAnimation()
                                        anim_success_warehouse.addAnimatorListener(object : Animator.AnimatorListener {
                                            override fun onAnimationStart(animation: Animator) {

                                            }

                                            override fun onAnimationEnd(animation: Animator) {
                                                Toast.makeText(
                                                    this@InputActivity,
                                                    "Muavaffaqiyatli qo'shildi!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                edtxt_product_quantity.setText("")
                                                anim_success_warehouse.setVisibility(View.GONE)
                                            }

                                            override fun onAnimationCancel(animation: Animator) {

                                            }

                                            override fun onAnimationRepeat(animation: Animator) {

                                            }
                                        })
                                    }
                            }
                        }// the product is not added to database
                    } else
                        Toast.makeText(this, "Iltimos, maxsulotni tanlang", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Iltimos, maxsulot sonini kiriting!", Toast.LENGTH_SHORT).show()
                }
            } else {
                showMessage("Internet aloqasi yo'q")
            }
        }
    }

    private fun getWarehouseObject(productId: String): Warehouse? {
        for (warehouse in warehouseList) {
            if (warehouse.productId == productId)
                return warehouse
        }
        return null
    }

    private fun updateWarehouseData(warehouse: Warehouse) {
        //        if (warehouse.getInputPrice() != 0.0)
        ////            warehousePrice.setText("");
        //        else
        ////            warehousePrice.setText(warehouse.getInputPrice() + " $");
        warehouse_quantity.text = (warehouse.quantity).toString() + " ta"
    }

    fun getSelectedProduct(name: String): ProductModel? {
        for (product in productList) {
            if (product.name == name) {
                return product
            }
        }
        return null
    }

    private fun showBrandList(brandList: List<String>) {
        val adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, brandList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_brands.setAdapter(adapter)
    }

    private fun showProductList(brandName: String) {
        val productNames = java.util.ArrayList<String>()

        for (product in productList) {
            if (product.brandName== brandName)
                productNames.add(product.name)
        }
        val adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, productNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_products.adapter = adapter
    }

    fun showMessage(message: String) {
        val snackbar = Snackbar
            .make(this.findViewById(R.id.coordinator_layout), message, Snackbar.LENGTH_LONG)
            .setAction("Sozlamalar "
            ) { startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) }

        // Changing message text color
        snackbar.setActionTextColor(Color.GREEN)

        // Changing action button text color
        val sbView = snackbar.view
        val textView = sbView.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
        textView.setTextColor(Color.YELLOW)

        snackbar.show()
    }

    public override fun onStop() {
        warehouseListener.remove()
        productListener.remove()
        brandsListener.remove()
        super.onStop()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0,R.anim.slide_to_right)
    }
}
