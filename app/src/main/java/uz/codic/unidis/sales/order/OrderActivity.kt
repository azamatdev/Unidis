package uz.codic.unidis.sales.order

import android.arch.lifecycle.*
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_order.*
import uz.codic.unidis.R
import uz.codic.unidis.cart.CartActivity
import uz.codic.unidis.data.Product
import uz.codic.unidis.data.Warehouse

class OrderActivity : AppCompatActivity(), OrderCallback {

    val firestore = FirebaseFirestore.getInstance()
    val brandList = ArrayList<String>()

    val warehouseList = ArrayList<Warehouse>()
    val sortedList = ArrayList<Warehouse>()
    var producList = ArrayList<Product>()

    val adapter: OrderAdapter = OrderAdapter()

    var brandPosition: Int = 0
    lateinit var warehouseListener : ListenerRegistration
    lateinit var brandListener : ListenerRegistration
    lateinit var orderViewModel: OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        brandPosition = intent.getIntExtra("brandPosition", 0)

        //recyler setup
        adapter.setCallback(this@OrderActivity as OrderCallback)
        product_recyler.layoutManager = LinearLayoutManager(this)
        product_recyler.adapter = adapter

        getCartProductList()

        getWareHouseList()

        getBrandNames()

        listenForBrandChange()

        cart.setOnClickListener {
            val intent = Intent(OrderActivity@this, CartActivity::class.java)
            startActivity(intent)
        }

        btn_back.setOnClickListener {
            finish()
            overridePendingTransition(0,R.anim.slide_to_right)
        }

    }

    private fun getCartProductList() {
        orderViewModel = ViewModelProviders.of(this).get(OrderViewModel::class.java)
        orderViewModel.getAllProducts().observe(this, Observer { t ->
            cart_number.text = t?.size.toString()
            producList = t as ArrayList<Product>
        })
    }

    private fun listenForBrandChange() {
        spinner_brands.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                showProductList(spinner_brands.selectedItem.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun showProductList(brandName: String) {
        if (!sortedList.isEmpty())
            sortedList.clear()
        for (warehouse in warehouseList) {
            if (warehouse.brandName == brandName) {
                sortedList.add(warehouse)
//                labelQuantity()
            }

        }
        adapter.updateList(sortedList)
    }

    /*
    this method is used to label the added cart quantity with the chosen quantity by user
    61 - 2 (61 in warehouse, 2 is chosen)
     */
//    private fun labelQuantity() {
//        for(x in 0 until sortedList.size){
//            for (y in 0 until producList.size){
//                if(sortedList[x].productId.equals(producList[y].productId))
//                    sortedList.get(x).quantity += "-" + producList[y].quantity
//            }
//        }
//    }

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
                        Log.d("TagCheck", dc.id)
                        warehouseList.add(warehouse)
                    }

                    showProductList(spinner_brands.selectedItem.toString())

                })
    }

    private fun getBrandNames() {
        brandListener = firestore.collection("brands").addSnapshotListener(

            EventListener { queryDocumentSnapshots, e ->
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
                spinner_brands.setSelection(brandPosition)
            }
        )
    }

    private fun showBrandList(brandList: ArrayList<String>) {
        val adapter = ArrayAdapter(
            this, R.layout.item_spinner, brandList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_brands.adapter = adapter
    }

    fun onBackClick(v: View) = finish()

    override fun onItemClick(warehouse: Warehouse) {
        val fm = supportFragmentManager
        val orderDialogFragment =
            OrderDialogFragment.newInstance(warehouse)
        orderDialogFragment.setCallback(this)
        orderDialogFragment.show(fm, "fragment_edit_name")
    }


    //ADD logic
    override fun onOrderAddClick(warehouse: Warehouse) {
        if(isExist(warehouse.productId)){
            var product = getExistingProduct(warehouse.productId)
            product.salesPrice = warehouse.salesPrice
            product.quantity = warehouse.quantity.toString()
            product.inputPrice = warehouse.inputPrice
            orderViewModel.updateProduct(product)
            Toast.makeText(this, "Yangilandi", Toast.LENGTH_SHORT).show()
        }
        else{
            var product = Product()
            product.name = warehouse.productName
            product.productId = warehouse.productId
            product.salesPrice = warehouse.salesPrice
            product.quantity = warehouse.quantity.toString()
            product.inputPrice = warehouse.inputPrice
            product.brand = warehouse.brandName
            orderViewModel.insertProduct(product)
            Toast.makeText(this, "Qo'shildi", Toast.LENGTH_SHORT).show()
        }
    }

    fun getExistingProduct(productId: String): Product{
        for(product in producList) {
            if (product.productId == productId)
                return product
        }
        return Product()
    }

    fun isExist(productId : String):Boolean{
        for(product in producList){
            if(product.productId == productId)
                return true
        }
        return false
    }

    override fun onStop() {
        warehouseListener.remove()
        brandListener.remove()
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        if(!warehouseList.isEmpty()){
            getWareHouseList()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0,R.anim.slide_to_right)
    }
}
