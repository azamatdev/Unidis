package uz.codic.unidis.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.persistence.room.*

@Dao
interface ProductDao {

    @Insert
    fun insertProduct(product: Product)

    @Query("SELECT * FROM product")
    fun getAllProducts(): LiveData<List<Product>>

    @Delete
    fun deleteProduct(product: Product)

    @Query("DELETE from Product")
    fun deleteAllProducts()

    @Query("UPDATE Product SET salesPrice = :salesPrice, quantity = :quantity where id = :id")
    fun updateProduct(salesPrice: Double, quantity : String, id : Int):Int

}