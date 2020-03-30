package uz.codic.unidis.sales.order

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import uz.codic.unidis.data.Product
import uz.codic.unidis.data.ProductRepository

class OrderViewModel(application: Application) : AndroidViewModel(application){

    private var productRepository = ProductRepository(application)

    private lateinit var productList : MutableLiveData<List<Product>>

    fun insertProduct(product: Product){
        productRepository.insert(product)
    }

    fun getAllProducts() : LiveData<List<Product>>{
        return productRepository.getAllProducts()
    }

    fun deleteProduct(product: Product){
        productRepository.deleteProduct(product)
    }

    fun updateProduct(product: Product){
        productRepository.updateProduct(product)
    }

    fun deleteAllProduct(){
        productRepository.deleteAllProducts()
    }
}