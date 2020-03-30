package uz.codic.unidis.fixedPrice

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_fixed_price.*
import uz.codic.unidis.R
import uz.codic.unidis.data.ProductModel
import uz.codic.unidis.data.Warehouse
import uz.codic.unidis.utils.Utils

class FixedPriceActivity : AppCompatActivity() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    lateinit var brandListener :ListenerRegistration
    lateinit var productListener :ListenerRegistration
    lateinit var warehouseListener :ListenerRegistration
    internal var brandList: ArrayList<String> = ArrayList()
    internal var productList: ArrayList<ProductModel> = ArrayList()

    internal var warehouseList: ArrayList<Warehouse> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fixed_price)

        val value = android.util.TypedValue()
        this.theme.resolveAttribute(4, value, true)
        val metrics = android.util.DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(metrics)
        val ret = value.getDimension(metrics)
        spinner_brands.minimumHeight = (ret - 1 * metrics.density ).toInt()
        spinner_products.minimumHeight = (ret - 1 * metrics.density).toInt()

        getBrandName()

        getProductList()

        getWareHouseList()

        listenForBrandChange()
//
        listenForProductChange()

        add_product_warehouse.setOnClickListener {
            addToWarehouse()
        }

        btn_back.setOnClickListener {
            finish()
            overridePendingTransition(0,R.anim.slide_to_right)
        }
    }

    /**
     * Get Brands from firestore and show them in spinner
     */

    private fun getBrandName() {
        brandListener = firestore.collection("brands")
            .addSnapshotListener(
                EventListener<QuerySnapshot> { queryDocumentSnapshots, e ->

                    if (e != null) {
                        Log.w("TAG", "listen:error", e)
                        return@EventListener
                    }
                    if (!brandList.isEmpty()) {
                        brandList.clear()
                    }
                    for (dc in queryDocumentSnapshots!!.documents) {
                        brandList.add(dc.data!!["name"] as String)
                        Log.d("BrandTag", dc.data!!["name"].toString())
                    }
                    showBrandList(brandList)
                })
    }

    private fun showBrandList(brandList: List<String>) {
        val adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, brandList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_brands.adapter = adapter
    }


    /**
     * Fetch all products from the Firestore then show them with the respect to brand chosen
     */
    private fun getProductList() {
        productListener = firestore.collection("products")
            .addSnapshotListener(
                EventListener<QuerySnapshot> { queryDocumentSnapshots, e ->
                    if (e != null) {
                        Log.w("TAG", "listen:error", e)
                        return@EventListener
                    }
                    if (!productList.isEmpty()) {
                        productList.clear()
                    }
                    for (dc in queryDocumentSnapshots!!.documents) {
                        val product = ProductModel()
                        product.productId = (dc.id)
                        product.name = dc.data!!["name"] as String
                        product.brandName = dc.data!!["brandName"] as String
                        Log.w("ProductTag", dc.data!!["name"] as String)
                        productList.add(product)
                    }
                    showProductList(spinner_brands.selectedItem.toString())
                })
    }

    private fun showProductList(brandName: String) {
        val productNames = java.util.ArrayList<String>()

        for (product in productList) {
            if (product.brandName == brandName)
                productNames.add(product.name)
        }
        val adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, productNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_products.adapter = adapter
    }

    /**
     * Fetch all the warehouse  added products
     * in order to know if it already exists or not
     */

    private fun getWareHouseList() {
        warehouseListener = firestore.collection("warehouse")
            .addSnapshotListener(
                EventListener<QuerySnapshot> { queryDocumentSnapshots, e ->
                    if (e != null) {
                        Log.w("TAG", "listen:error", e)
                        return@EventListener
                    }
                    if (!warehouseList.isEmpty()) {
                        warehouseList.clear()
                    }
                    for (dc in queryDocumentSnapshots!!.documents) {
                        val warehouse = dc.toObject(Warehouse::class.java)!!
                        warehouse.productId = dc.id
                        warehouseList.add(warehouse)
                    }

                    if(spinner_products.selectedItem != null){
                        val product = getSelectedProduct(spinner_products.selectedItem.toString())
                        if (product.brandName != "") {
                            val existedWarehouse = getWarehouseObject(product.productId)
                            if (existedWarehouse.productName != "") {
                                label_input_price.text = existedWarehouse.inputPrice.toString() + " $"
                                label_sales_price.text = existedWarehouse.salesPrice.toString()+ " $"
                                label_warehouse_quantity.text = existedWarehouse.quantity.toString()+ " ta"
                            } else {
                                label_sales_price.text = ""
                                label_input_price.text = ""
                                label_warehouse_quantity.text = ""
                            }
                        }
                    }

                })
    }

    /**
     * Used for product spinner
     */
    private fun getSelectedProduct(name: String): ProductModel {
        for (product in productList) {
            if (product.name == name) {
                return product
            }
        }
        return ProductModel()
    }

    /**
     * Used for labelling if product is already in the warehouse
     */
    private fun getWarehouseObject(productId: String): Warehouse {
        for (warehouse in warehouseList) {
            if (warehouse.productId == productId)
                return warehouse
        }
        return Warehouse()
    }


    private fun listenForBrandChange() {
        spinner_brands.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                showProductList(spinner_brands.selectedItem.toString())
                if (spinner_products.selectedItem != null) {
                    val product = getSelectedProduct(spinner_products.selectedItem.toString())
                    if (product.brandName != "") {
                        productSelected(product)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun listenForProductChange() {
        spinner_products.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val product = getSelectedProduct(spinner_products.selectedItem.toString())
                if (product.brandName != "") {
                    productSelected(product)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    /**
     * Used to label the warehouse properties
     */
    private fun productSelected(product: ProductModel) {
        if (getWarehouseObject(product.productId).brandName != "") {
            label_input_price.visibility =View.VISIBLE
            label_sales_price.visibility = View.VISIBLE
            label_warehouse_quantity.visibility = View.VISIBLE
            val existedWarehouse = getWarehouseObject(product.productId)
            label_input_price.text = existedWarehouse.inputPrice.toString() + " $"
            label_sales_price.text = existedWarehouse.salesPrice.toString() + " $"
            label_warehouse_quantity.text = existedWarehouse.quantity.toString() + " ta"
            //                        updateWarehouseData(getWarehouseObject(product.productId)!!)
        } else {
            label_input_price.text = ""
            label_sales_price.text = ""
            label_warehouse_quantity.text = ""

            label_input_price.visibility =View.GONE
            label_sales_price.visibility = View.GONE
            label_warehouse_quantity.visibility = View.GONE
        }
    }

    private fun addToWarehouse() {
        add_product_warehouse.setOnClickListener {

            if (Utils.isNetworkAvailable(this)) { // Network Check
                if (spinner_products.selectedItem != null) { // product spinner might be empty
                    val product : ProductModel = getSelectedProduct(spinner_products.selectedItem.toString())

                    //if quantity, inputPrice, salesPrice are specified
                    if (!edtxt_quantity.text.toString().isEmpty() &&
                        !edtxt_input_price.text.toString().isEmpty()
                        && !edtxt_sales_price.text.toString().isEmpty()
                    ) {
                        //if the product is chosen, and is not empty. brandName is always there,
                        // we check if it is specified or not
                        if (product.brandName != "") {

                            //if product is already added to database
                            //need to update
                            // . will give us error. because of double, calculation might get wrong
                            val warehouse = getWarehouseObject(product.productId)
                            if (warehouse.productName != ""
                                && edtxt_input_price.text.toString() != "."
                                && edtxt_sales_price.text.toString() != "."
                                && edtxt_quantity.text.toString() != "."
                            ) {
                                Log.d("TagCondition", "This is")
                                val inputPrice = edtxt_input_price.text.toString().toDouble()
                                val warehousePrice = warehouse.inputPrice
                                val inputQuantity = Integer.parseInt(edtxt_quantity.text.toString())
                                val warehouseQuantity = warehouse.quantity
                                //The golden formula
                                val total =
                                    (inputPrice * inputQuantity + warehousePrice * warehouseQuantity) / (inputQuantity + warehouseQuantity)
                                warehouse.inputPrice = total
                                val totalQuantity =
                                    Integer.parseInt(edtxt_quantity.text.toString()) + warehouse.quantity
                                warehouse.quantity = totalQuantity

                                warehouse.salesPrice = edtxt_sales_price.text.toString().toDouble()
//                                val map = HashMap<String, Any>()
//                                map["brandName"] = warehouse.brandName
//                                map["productName"] = warehouse.productName
//                                map["quantity"] = warehouse.getQuantity()
//                                map["price"] = warehouse.getPrice()
                                firestore.collection("warehouse").document(product.productId)
                                    .set(warehouse)
                                    .addOnSuccessListener {
                                        label_input_price.visibility =View.VISIBLE
                                        label_sales_price.visibility = View.VISIBLE
                                        label_warehouse_quantity.visibility = View.VISIBLE
                                        label_input_price.text = warehouse.inputPrice.toString() + " $"
                                        label_sales_price.text = warehouse.salesPrice.toString() + " $"
                                        label_warehouse_quantity.text = warehouse.quantity.toString() + " ta"
                                        edtxt_input_price.setText("")
                                        edtxt_sales_price.setText("")
                                        edtxt_quantity.setText("")
                                    }
                            } else { //If product is not added to warehouse
                                if (edtxt_input_price.text.toString() != "."
                                    && edtxt_sales_price.text.toString() != "."
                                    && edtxt_sales_price.text.toString() != ".") {

                                    val map = HashMap<String, Any>()
                                    map["brandName"] = product.brandName
                                    map["productName"] = product.name
                                    map["quantity"] = edtxt_quantity.text.toString().toInt()
                                    map["inputPrice"] = edtxt_input_price.text.toString().toDouble()
                                    map["salesPrice"] = edtxt_sales_price.text.toString().toDouble()

                                    //saving the new Product to firebase, with document key nemed with product id
                                    firestore.collection("warehouse").document(product.productId)
                                        .set(map)
                                        .addOnSuccessListener {
                                            label_input_price.visibility =View.VISIBLE
                                            label_sales_price.visibility = View.VISIBLE
                                            label_warehouse_quantity.visibility = View.VISIBLE
                                            label_input_price.text = edtxt_input_price.text.toString() + " $"
                                            label_sales_price.text = edtxt_sales_price.text.toString() + " $"
                                            label_warehouse_quantity.text = edtxt_quantity.text.toString() + " ta"
                                            edtxt_input_price.setText("")
                                            edtxt_sales_price.setText("")
                                            edtxt_quantity.setText("")

                                        }
                                }
                            }// the product is not added to database
                        } else
                            Toast.makeText(this, "Iltimos, maxsulotni tanlang", Toast.LENGTH_SHORT).show()
                    } else
                    {
                        Toast.makeText(this, "Iltimos, maxsulot sonini kiriting!", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    showMessage("Tovarlar yo'q")
                }
            } else {
                showMessage("Internet aloqasi yo'q")
            }
        }
    }

    private fun showMessage(message: String) {
        val snackbar = Snackbar
            .make(this.findViewById(R.id.coordinator_layout), message, Snackbar.LENGTH_LONG)
            .setAction("Sozlamalar ") { startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) }

        // Changing message text color
        snackbar.setActionTextColor(Color.GREEN)

        // Changing action button text color
        val sbView = snackbar.view
        val textView = sbView.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
        textView.setTextColor(Color.YELLOW)

        snackbar.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0,R.anim.slide_to_right)
    }


}
