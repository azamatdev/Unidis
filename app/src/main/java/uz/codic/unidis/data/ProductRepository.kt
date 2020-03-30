package uz.codic.unidis.data

import android.app.Application
import android.arch.lifecycle.LiveData
import android.os.AsyncTask
import android.util.Log

class ProductRepository(application: Application) {

    private val productDao: ProductDao
    private val listLiveData: LiveData<List<Product>>

    init {
        val productRoomDatabase = ProductRoomDatabase.getDatabase(application.applicationContext)
        productDao = productRoomDatabase?.productDao()!!
        listLiveData = productDao.getAllProducts()
    }

    fun getAllProducts(): LiveData<List<Product>> {
        return listLiveData
    }

    fun insert(product: Product) {
        insertAsyncTask(productDao).execute(product)
    }

    fun deleteProduct(product: Product){
        deleteAsyncTask(productDao).execute(product)
    }

    fun deleteAllProducts(){
        deleteAllAsyncTask(productDao).execute()
    }
    fun updateProduct(product: Product){
        updateAsyncTask(productDao).execute(product)
    }
    private class insertAsyncTask internal constructor(private val mAsyncTaskDao: ProductDao) : AsyncTask<Product, Void, Void>() {

        override fun doInBackground(vararg params: Product): Void? {
            mAsyncTaskDao.insertProduct(params[0])
            return null
        }
    }

    private class deleteAsyncTask internal constructor(private val mAsyncTaskDao: ProductDao) : AsyncTask<Product, Void, Void>() {

        override fun doInBackground(vararg params: Product): Void? {
            mAsyncTaskDao.deleteProduct(params[0])
            return null
        }
    }

    private class deleteAllAsyncTask internal constructor(private val mAsyncTaskDao: ProductDao) : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg params: Void): Void? {
            mAsyncTaskDao.deleteAllProducts()
            return null
        }
    }

    private class updateAsyncTask internal constructor(private val mAsyncTaskDao: ProductDao) : AsyncTask<Product, Void, Void>() {

        override fun doInBackground(vararg params: Product): Void?{
//            mAsyncTaskDao.updateProduct(params[0]!!.salesPrice, params[0]!!.quantity, params[0]!!.id)
            mAsyncTaskDao.updateProduct(params[0].salesPrice, params[0].quantity, params[0].id)
            Log.d("RoomTag", ""+ mAsyncTaskDao.updateProduct(params[0].salesPrice, params[0].quantity, params[0].id))
            return null
        }

        //        override fun doInBackground(vararg params: String): Void? {
//            mAsyncTaskDao.updateProduct(params[0], )
//            return null
//        }
    }

}